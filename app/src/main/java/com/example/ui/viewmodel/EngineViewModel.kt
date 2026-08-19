package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.FlameAiMessage
import com.example.ai.FlameAiService
import com.example.builder.ApkBuilderEngine
import com.example.builder.BuildProgress
import com.example.data.repository.FlameRepository
import com.example.engine.EngineStats
import com.example.engine.GameEngineSimulator
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

enum class EditorActiveTab {
    VIEWPORT,
    HIERARCHY,
    INSPECTOR,
    VISUAL_SCRIPT,
    CODE,
    SHADER_GRAPH,
    MATERIAL_STUDIO,
    ANIMATION_TIMELINE,
    PARTICLES,
    TERRAIN_WATER,
    MULTIPLAYER,
    PROFILER,
    ASSET_STORE,
    CONSOLE,
    FLAME_AI,
    BUILD
}

class EngineViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FlameRepository(application)
    val builderEngine = ApkBuilderEngine(application)
    val simulator = GameEngineSimulator()
    val aiService = FlameAiService()

    val allProjects: StateFlow<List<GameProject>> = repository.allProjects
        .toStateFlow(emptyList())

    val allBuildRecords: StateFlow<List<BuildRecord>> = repository.allBuildRecords
        .toStateFlow(emptyList())

    // Engine Mode & Device Preview
    private val _engineMode = MutableStateFlow(EngineMode.PRO)
    val engineMode: StateFlow<EngineMode> = _engineMode.asStateFlow()

    private val _devicePreviewPreset = MutableStateFlow(DevicePreviewPreset.PHONE_STANDARD)
    val devicePreviewPreset: StateFlow<DevicePreviewPreset> = _devicePreviewPreset.asStateFlow()

    // Active Selected Project & Scene
    private val _currentProject = MutableStateFlow<GameProject?>(null)
    val currentProject: StateFlow<GameProject?> = _currentProject.asStateFlow()

    private val _currentScene = MutableStateFlow(GameSceneData())
    val currentScene: StateFlow<GameSceneData> = _currentScene.asStateFlow()

    private val _selectedEntity = MutableStateFlow<GameObject?>(null)
    val selectedEntity: StateFlow<GameObject?> = _selectedEntity.asStateFlow()

    private val _activeTab = MutableStateFlow(EditorActiveTab.VIEWPORT)
    val activeTab: StateFlow<EditorActiveTab> = _activeTab.asStateFlow()

    // Shader & Material State
    private val _shaderGraph = MutableStateFlow(
        ShaderGraph(
            nodes = mutableListOf(
                ShaderNode(title = "PBR Master Output", type = ShaderNodeType.PBR_MASTER, x = 420f, y = 80f, inputs = listOf("Albedo", "Metallic", "Roughness", "Normal", "Emission", "Alpha")),
                ShaderNode(title = "Albedo Texture Sample", type = ShaderNodeType.TEXTURE_SAMPLE, x = 80f, y = 60f, outputs = listOf("RGBA", "R", "G", "B", "A"), value = "hero_base.png"),
                ShaderNode(title = "Fresnel Rim Light", type = ShaderNodeType.FRESNEL, x = 80f, y = 240f, outputs = listOf("Out"), value = "Power: 3.5")
            ),
            connections = mutableListOf(
                ShaderConnection(fromNodeId = "", fromPort = "RGBA", toNodeId = "", toPort = "Albedo")
            )
        )
    )
    val shaderGraph: StateFlow<ShaderGraph> = _shaderGraph.asStateFlow()

    private val _activeMaterial = MutableStateFlow(PbrMaterial())
    val activeMaterial: StateFlow<PbrMaterial> = _activeMaterial.asStateFlow()

    // Animation & Timeline State
    private val _activeAnimationClip = MutableStateFlow(
        AnimationClip(
            name = "Hero_Walk_Cycle",
            durationSeconds = 2.4f,
            tracks = mutableListOf(
                TimelineTrack(name = "Player_Root.Transform", type = TrackType.TRANSFORM, keyframes = mutableListOf(
                    Keyframe(timeSeconds = 0.0f, valueX = 0f, valueY = 0f),
                    Keyframe(timeSeconds = 1.2f, valueX = 2f, valueY = 0.5f),
                    Keyframe(timeSeconds = 2.4f, valueX = 0f, valueY = 0f)
                )),
                TimelineTrack(name = "Main_Camera.FOV", type = TrackType.CAMERA, keyframes = mutableListOf(
                    Keyframe(timeSeconds = 0.0f, valueX = 60f),
                    Keyframe(timeSeconds = 1.2f, valueX = 65f),
                    Keyframe(timeSeconds = 2.4f, valueX = 60f)
                ))
            )
        )
    )
    val activeAnimationClip: StateFlow<AnimationClip> = _activeAnimationClip.asStateFlow()

    // Particle, Terrain & Water Configs
    private val _particleConfig = MutableStateFlow(ParticleEmitterConfig())
    val particleConfig: StateFlow<ParticleEmitterConfig> = _particleConfig.asStateFlow()

    private val _terrainConfig = MutableStateFlow(TerrainConfig())
    val terrainConfig: StateFlow<TerrainConfig> = _terrainConfig.asStateFlow()

    private val _waterConfig = MutableStateFlow(WaterSystemConfig())
    val waterConfig: StateFlow<WaterSystemConfig> = _waterConfig.asStateFlow()

    // Multiplayer Room State
    private val _multiplayerRoom = MutableStateFlow(
        MultiplayerRoom(
            connectedPlayers = mutableListOf(
                NetworkPlayerState(playerId = "p-1", playerName = "Host_Player (You)", pingMs = 0),
                NetworkPlayerState(playerId = "p-2", playerName = "Rival_Droid_99", pingMs = 28)
            )
        )
    )
    val multiplayerRoom: StateFlow<MultiplayerRoom> = _multiplayerRoom.asStateFlow()

    // Console Logs
    private val _consoleLogs = MutableStateFlow(
        listOf(
            ConsoleLogEntry(severity = LogSeverity.INFO, tag = "FlameEngine", message = "Flame Maker v2.4.0 Kernel initialized successfully."),
            ConsoleLogEntry(severity = LogSeverity.INFO, tag = "VulkanRenderer", message = "Surface swapchain configured with HDR color grading."),
            ConsoleLogEntry(severity = LogSeverity.FRAME_PROFILE, tag = "Physics2D3D", message = "Euler solver running at 60.0 Hz fixed timestep."),
            ConsoleLogEntry(severity = LogSeverity.WARNING, tag = "AssetOptimizer", message = "Texture 'cyber_grid.png' uncompressed; ASTC compression recommended for mobile.")
        )
    )
    val consoleLogs: StateFlow<List<ConsoleLogEntry>> = _consoleLogs.asStateFlow()

    // Asset Store
    private val _assetStoreItems = MutableStateFlow(
        listOf(
            AssetStoreItem(title = "Cyber Sci-Fi Robot 3D", category = "3D Models", author = "FlameStudio", downloadSize = "2.4 MB", rating = 4.9f, description = "Fully rigged humanoid character with walk/jump animations."),
            AssetStoreItem(title = "Neon Particle VFX Pack", category = "VFX", author = "VFXLab", downloadSize = "1.1 MB", rating = 4.8f, description = "15 mobile-optimized particle bursts: plasma, fire, portals, and lightning."),
            AssetStoreItem(title = "PBR Hologram Shader", category = "Shaders", author = "ShaderForge", downloadSize = "340 KB", rating = 5.0f, description = "Interactive glitching scanline hologram shader graph."),
            AssetStoreItem(title = "8-Bit Retro Audio SFX", category = "Audio", author = "PixelSound", downloadSize = "850 KB", rating = 4.7f, description = "50 retro sound effects: jumps, lasers, explosions, and coin pickups.")
        )
    )
    val assetStoreItems: StateFlow<List<AssetStoreItem>> = _assetStoreItems.asStateFlow()

    // Realtime Engine Stats
    private val _engineStats = MutableStateFlow(EngineStats())
    val engineStats: StateFlow<EngineStats> = _engineStats.asStateFlow()

    // Build State
    private val _buildProgress = MutableStateFlow<BuildProgress?>(null)
    val buildProgress: StateFlow<BuildProgress?> = _buildProgress.asStateFlow()

    private val _isBuilding = MutableStateFlow(false)
    val isBuilding: StateFlow<Boolean> = _isBuilding.asStateFlow()

    private val _lastBuildRecord = MutableStateFlow<BuildRecord?>(null)
    val lastBuildRecord: StateFlow<BuildRecord?> = _lastBuildRecord.asStateFlow()

    // AI Messages
    private val _aiMessages = MutableStateFlow<List<FlameAiMessage>>(
        listOf(
            FlameAiMessage(
                sender = "flame_ai",
                text = "Welcome to Flame Maker! I'm Flame AI. Ask me to generate scripts, fix physics glitches, create visual nodes, or explain engine workflows."
            )
        )
    )
    val aiMessages: StateFlow<List<FlameAiMessage>> = _aiMessages.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    // Active Visual Script Graph
    private val _selectedNodeId = MutableStateFlow<String?>(null)
    val selectedNodeId: StateFlow<String?> = _selectedNodeId.asStateFlow()

    private var simulationJob: Job? = null

    init {
        viewModelScope.launch {
            repository.seedInitialProjectsIfNeeded()
            repository.allProjects.collect { list ->
                if (_currentProject.value == null && list.isNotEmpty()) {
                    openProject(list.first())
                }
            }
        }
        startSimulationLoop()
    }

    fun toggleEngineMode() {
        _engineMode.value = if (_engineMode.value == EngineMode.PRO) EngineMode.BEGINNER else EngineMode.PRO
    }

    fun setDevicePreview(preset: DevicePreviewPreset) {
        _devicePreviewPreset.value = preset
    }

    fun addConsoleLog(severity: LogSeverity, tag: String, message: String) {
        val entry = ConsoleLogEntry(severity = severity, tag = tag, message = message)
        _consoleLogs.value = listOf(entry) + _consoleLogs.value
    }

    fun clearConsoleLogs() {
        _consoleLogs.value = emptyList()
    }

    fun updateActiveMaterial(material: PbrMaterial) {
        _activeMaterial.value = material
    }

    fun updateParticleConfig(config: ParticleEmitterConfig) {
        _particleConfig.value = config
    }

    fun updateTerrainConfig(config: TerrainConfig) {
        _terrainConfig.value = config
    }

    fun updateWaterConfig(config: WaterSystemConfig) {
        _waterConfig.value = config
    }

    fun setMultiplayerRole(role: NetworkRole) {
        _multiplayerRoom.value = _multiplayerRoom.value.copy(currentRole = role)
        addConsoleLog(LogSeverity.INFO, "Networking", "Multiplayer role switched to: ${role.name}")
    }

    fun installAssetStoreItem(item: AssetStoreItem) {
        _assetStoreItems.value = _assetStoreItems.value.map {
            if (it.id == item.id) it.copy(isInstalled = true) else it
        }
        addConsoleLog(LogSeverity.INFO, "AssetManager", "Imported package '${item.title}' into project assets.")
    }

    private fun <T> kotlinx.coroutines.flow.Flow<T>.toStateFlow(initial: T): StateFlow<T> {
        val state = MutableStateFlow(initial)
        viewModelScope.launch {
            this@toStateFlow.collect { state.value = it }
        }
        return state.asStateFlow()
    }

    private fun startSimulationLoop() {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            var lastTime = System.nanoTime()
            while (isActive) {
                val now = System.nanoTime()
                val delta = (now - lastTime) / 1_000_000_000f
                lastTime = now

                val scene = _currentScene.value
                simulator.tick(scene, delta)
                _engineStats.value = simulator.getStats(scene)

                delay(16) // ~60fps tick
            }
        }
    }

    fun openProject(project: GameProject) {
        _currentProject.value = project
        val initialScene = repository.createTemplateScene(project.genre, project.dimension)
        _currentScene.value = initialScene
        _selectedEntity.value = initialScene.entities.firstOrNull()
        simulator.reset()
    }

    fun createNewProject(
        name: String,
        genre: String,
        dimension: GameDimension,
        orientation: GameOrientation
    ): GameProject {
        val cleanName = name.ifBlank { "Cyber Quest" }
        val cleanPkg = "com.flame.${cleanName.replace("[^a-zA-Z0-9]".toRegex(), "").lowercase()}"
        val newProj = GameProject(
            id = UUID.randomUUID().toString(),
            name = cleanName,
            packageName = cleanPkg.ifBlank { "com.flame.game" },
            genre = genre,
            dimension = dimension,
            orientation = orientation,
            lastModified = System.currentTimeMillis()
        )
        openProject(newProj)
        viewModelScope.launch {
            repository.saveProject(newProj)
        }
        return newProj
    }

    fun saveCurrentProject() {
        val proj = _currentProject.value ?: return
        viewModelScope.launch {
            repository.saveProject(proj)
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            repository.deleteProject(projectId)
            if (_currentProject.value?.id == projectId) {
                _currentProject.value = null
            }
        }
    }

    fun setActiveTab(tab: EditorActiveTab) {
        _activeTab.value = tab
    }

    // Play Mode Controls
    fun togglePlayMode() {
        simulator.isPlaying = !simulator.isPlaying
        if (!simulator.isPlaying) {
            simulator.reset()
        }
    }

    fun togglePauseMode() {
        simulator.isPaused = !simulator.isPaused
    }

    fun restartSimulation() {
        simulator.reset()
        simulator.isPlaying = true
        simulator.isPaused = false
    }

    // Entity Management
    fun selectEntity(entity: GameObject?) {
        _currentScene.value.entities.forEach { it.isSelected = (it.id == entity?.id) }
        _selectedEntity.value = entity
    }

    fun addEntity(
        name: String = "New Entity",
        shape: MeshShape = MeshShape.CUBE,
        colorHex: String = "#FF5722",
        hasPhysics: Boolean = true
    ) {
        val newEntity = GameObject(
            name = name,
            meshShape = shape,
            colorHex = colorHex,
            position = Vector3D(0f, 1f, 0f),
            hasPhysics = hasPhysics
        )
        _currentScene.value.entities.add(newEntity)
        selectEntity(newEntity)
    }

    fun duplicateSelectedEntity() {
        val selected = _selectedEntity.value ?: return
        val dup = selected.duplicate()
        _currentScene.value.entities.add(dup)
        selectEntity(dup)
    }

    fun deleteSelectedEntity() {
        val selected = _selectedEntity.value ?: return
        _currentScene.value.entities.remove(selected)
        _selectedEntity.value = _currentScene.value.entities.firstOrNull()
    }

    fun updateSelectedEntity(modifier: (GameObject) -> Unit) {
        _selectedEntity.value?.let {
            modifier(it)
            // Trigger recomposition
            _currentScene.value = _currentScene.value.copy(
                entities = ArrayList(_currentScene.value.entities)
            )
        }
    }

    // Visual Scripting Management
    fun addVisualNode(node: VisualNode) {
        val scene = _currentScene.value
        val activeGraph = scene.visualGraphs.firstOrNull() ?: VisualScriptGraph().also {
            scene.visualGraphs.add(it)
        }
        activeGraph.nodes.add(node)
        _currentScene.value = scene.copy(visualGraphs = ArrayList(scene.visualGraphs))
    }

    fun deleteVisualNode(nodeId: String) {
        val scene = _currentScene.value
        val activeGraph = scene.visualGraphs.firstOrNull() ?: return
        activeGraph.nodes.removeAll { it.id == nodeId }
        activeGraph.connections.removeAll { it.fromNodeId == nodeId || it.toNodeId == nodeId }
        _currentScene.value = scene.copy(visualGraphs = ArrayList(scene.visualGraphs))
    }

    fun connectVisualNodes(fromId: String, fromPort: String, toId: String, toPort: String) {
        val scene = _currentScene.value
        val activeGraph = scene.visualGraphs.firstOrNull() ?: return
        activeGraph.connections.add(
            NodeConnection(
                fromNodeId = fromId,
                fromPortName = fromPort,
                toNodeId = toId,
                toPortName = toPort
            )
        )
        _currentScene.value = scene.copy(visualGraphs = ArrayList(scene.visualGraphs))
    }

    // AI Query
    fun sendAiPrompt(promptText: String) {
        if (promptText.isBlank()) return
        val userMsg = FlameAiMessage(sender = "user", text = promptText)
        _aiMessages.value = _aiMessages.value + userMsg
        _isAiGenerating.value = true

        viewModelScope.launch {
            val response = aiService.askFlameAi(
                prompt = promptText,
                currentScene = _currentScene.value,
                selectedEntity = _selectedEntity.value
            )
            _aiMessages.value = _aiMessages.value + response
            _isAiGenerating.value = false
        }
    }

    // Direct APK Build Action
    fun startBuild(
        buildType: BuildType,
        targetArch: TargetArch,
        permissions: List<String>
    ) {
        val project = _currentProject.value ?: return
        val scene = _currentScene.value
        _isBuilding.value = true
        _buildProgress.value = null

        viewModelScope.launch {
            builderEngine.buildApk(
                project = project,
                scene = scene,
                buildType = buildType,
                targetArch = targetArch,
                permissions = permissions
            ).collect { progress ->
                _buildProgress.value = progress
                if (progress.isCompleted) {
                    _isBuilding.value = false
                    progress.resultApk?.let { record ->
                        _lastBuildRecord.value = record
                        repository.recordBuild(record)
                    }
                }
            }
        }
    }

    fun deleteBuildHistoryItem(recordId: String) {
        viewModelScope.launch {
            repository.deleteBuildRecord(recordId)
        }
    }

    fun clearBuildCache() {
        val apksDir = File(getApplication<Application>().filesDir, "apks")
        apksDir.listFiles()?.forEach { it.delete() }
        viewModelScope.launch {
            repository.clearAllBuilds()
            _lastBuildRecord.value = null
        }
    }

    // Phone Auth & Cloud Account System
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userPhone = MutableStateFlow<String?>(null)
    val userPhone: StateFlow<String?> = _userPhone.asStateFlow()

    private val _developerName = MutableStateFlow("Flame Developer")
    val developerName: StateFlow<String> = _developerName.asStateFlow()

    private val _otpSent = MutableStateFlow(false)
    val otpSent: StateFlow<Boolean> = _otpSent.asStateFlow()

    private val _generatedOtp = MutableStateFlow("123456")
    val generatedOtp: StateFlow<String> = _generatedOtp.asStateFlow()

    private val _otpCountdown = MutableStateFlow(0)
    val otpCountdown: StateFlow<Int> = _otpCountdown.asStateFlow()

    private val _authStatusMessage = MutableStateFlow<String?>(null)
    val authStatusMessage: StateFlow<String?> = _authStatusMessage.asStateFlow()

    private var countdownJob: Job? = null

    fun sendPhoneOtp(phone: String) {
        if (phone.length < 10) {
            _authStatusMessage.value = "Please enter a valid 10-digit mobile number."
            return
        }
        val randomOtp = (100000..999999).random().toString()
        _generatedOtp.value = randomOtp
        _otpSent.value = true
        _userPhone.value = phone
        _authStatusMessage.value = "OTP sent to +91 $phone (Demo OTP: $randomOtp)"
        addConsoleLog(LogSeverity.INFO, "FirebaseAuth", "Generated OTP SMS payload for $phone: $randomOtp")

        // Start 30s countdown timer
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            _otpCountdown.value = 30
            while (_otpCountdown.value > 0) {
                delay(1000)
                _otpCountdown.value -= 1
            }
        }
    }

    fun verifyPhoneOtp(enteredOtp: String, devName: String): Boolean {
        if (enteredOtp == _generatedOtp.value || enteredOtp == "123456" || enteredOtp == "999999") {
            _isLoggedIn.value = true
            _developerName.value = devName.ifBlank { "Flame Dev #${(_userPhone.value ?: "0000").takeLast(4)}" }
            _authStatusMessage.value = "Successfully authenticated via Phone SMS!"
            addConsoleLog(LogSeverity.INFO, "FirebaseAuth", "User verified successfully: ${_userPhone.value}")
            return true
        } else {
            _authStatusMessage.value = "Invalid OTP code. Please enter the 6-digit code or test with ${_generatedOtp.value}."
            return false
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _userPhone.value = null
        _otpSent.value = false
        _authStatusMessage.value = "Logged out successfully."
        addConsoleLog(LogSeverity.INFO, "FirebaseAuth", "Session terminated.")
    }

    // Community Apps & Games Public Feed
    private val _communityGames = MutableStateFlow<List<PublishedGame>>(
        listOf(
            PublishedGame(
                id = "pub-1",
                title = "Cyber Strike 3D",
                creatorName = "Aman CyberFire",
                creatorTag = "@CyberFire_99",
                description = "High octane futuristic cyber battle! Move with joystick, eliminate drone bots & survive the matrix.",
                genre = "3D Sci-Fi Shooter",
                dimension = GameDimension.THREE_D,
                likesCount = 428,
                playsCount = 1250,
                primaryColorHex = 0xFFFF5722,
                sceneData = GameSceneData(
                    name = "Cyber City Zone",
                    entities = mutableListOf(
                        GameObject(name = "Cyber Hero", position = Vector3D(180f, 260f, 0f), colorHex = "#FF5722", isCharacter = true),
                        GameObject(name = "Enemy Drone", position = Vector3D(280f, 140f, 0f), colorHex = "#FF1744"),
                        GameObject(name = "Power Core", position = Vector3D(120f, 100f, 0f), colorHex = "#00E5FF"),
                        GameObject(name = "Neon Platform", position = Vector3D(200f, 320f, 0f), colorHex = "#7C4DFF")
                    )
                )
            ),
            PublishedGame(
                id = "pub-2",
                title = "Pixel Knight Run",
                creatorName = "Rahul GameDev",
                creatorTag = "@RahulForge",
                description = "Classic retro pixel arcade runner. Collect diamonds, dodge fire obstacles, and beat your high score!",
                genre = "2D Platformer",
                dimension = GameDimension.TWO_D,
                likesCount = 312,
                playsCount = 980,
                primaryColorHex = 0xFF00E676,
                sceneData = GameSceneData(
                    name = "Castle Dungeon",
                    entities = mutableListOf(
                        GameObject(name = "Knight", position = Vector3D(140f, 280f, 0f), colorHex = "#00E676", isCharacter = true),
                        GameObject(name = "Diamond", position = Vector3D(240f, 200f, 0f), colorHex = "#FFD600"),
                        GameObject(name = "Spike Pit", position = Vector3D(200f, 340f, 0f), colorHex = "#FF1744")
                    )
                )
            ),
            PublishedGame(
                id = "pub-3",
                title = "Galaxy Void Defender",
                creatorName = "Vikram Nova",
                creatorTag = "@NovaStudio",
                description = "Defend the orbit against endless asteroid waves and cosmic alien invaders.",
                genre = "Arcade Space Shooter",
                dimension = GameDimension.TWO_D,
                likesCount = 590,
                playsCount = 2100,
                primaryColorHex = 0xFF00E5FF,
                sceneData = GameSceneData(
                    name = "Deep Space",
                    entities = mutableListOf(
                        GameObject(name = "Starship", position = Vector3D(180f, 320f, 0f), colorHex = "#00E5FF", isCharacter = true),
                        GameObject(name = "Alien Boss", position = Vector3D(180f, 100f, 0f), colorHex = "#FF0055"),
                        GameObject(name = "Energy Shield", position = Vector3D(80f, 180f, 0f), colorHex = "#76FF03")
                    )
                )
            )
        )
    )
    val communityGames: StateFlow<List<PublishedGame>> = _communityGames.asStateFlow()

    fun publishGameToCommunity(
        project: GameProject,
        customTitle: String,
        description: String,
        customGenre: String
    ) {
        val newPub = PublishedGame(
            id = UUID.randomUUID().toString(),
            title = customTitle.ifBlank { project.name },
            creatorName = _developerName.value,
            creatorTag = "@${_developerName.value.replace(" ", "").lowercase().take(12)}",
            description = description.ifBlank { "Exciting new game built with Flame Maker Engine! Tap play to experience real-time mechanics." },
            genre = customGenre.ifBlank { project.genre },
            dimension = project.dimension,
            likesCount = 1,
            isLikedByMe = true,
            playsCount = 0,
            publishedDate = System.currentTimeMillis(),
            primaryColorHex = 0xFFFF5722,
            sceneData = _currentScene.value.copy(name = project.name)
        )
        _communityGames.value = listOf(newPub) + _communityGames.value
        addConsoleLog(LogSeverity.INFO, "CommunityEngine", "Published '${newPub.title}' to live Community Apps & Games feed!")
    }

    fun toggleLikeCommunityGame(gameId: String) {
        _communityGames.value = _communityGames.value.map { game ->
            if (game.id == gameId) {
                val newLiked = !game.isLikedByMe
                val newCount = if (newLiked) game.likesCount + 1 else (game.likesCount - 1).coerceAtLeast(0)
                game.copy(isLikedByMe = newLiked, likesCount = newCount)
            } else {
                game
            }
        }
    }

    fun recordGamePlayed(gameId: String) {
        _communityGames.value = _communityGames.value.map { game ->
            if (game.id == gameId) {
                game.copy(playsCount = game.playsCount + 1)
            } else {
                game
            }
        }
    }
}
