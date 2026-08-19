package com.example.data.repository

import android.content.Context
import androidx.room.Room
import com.example.data.local.FlameDatabase
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class FlameRepository(private val context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        FlameDatabase::class.java,
        "flame_maker.db"
    ).fallbackToDestructiveMigration().build()

    private val projectDao = db.projectDao()
    private val buildRecordDao = db.buildRecordDao()
    private val sceneDao = db.sceneDao()
    private val assetDao = db.assetDao()

    val allProjects: Flow<List<GameProject>> = projectDao.getAllProjects()
    val allBuildRecords: Flow<List<BuildRecord>> = buildRecordDao.getAllBuildRecords()

    suspend fun getProject(id: String): GameProject? = withContext(Dispatchers.IO) {
        projectDao.getProjectById(id)
    }

    suspend fun saveProject(project: GameProject) = withContext(Dispatchers.IO) {
        projectDao.insertProject(project.copy(lastModified = System.currentTimeMillis()))
    }

    suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
        projectDao.deleteProject(id)
    }

    suspend fun recordBuild(record: BuildRecord) = withContext(Dispatchers.IO) {
        buildRecordDao.insertBuildRecord(record)
    }

    suspend fun deleteBuildRecord(id: String) = withContext(Dispatchers.IO) {
        buildRecordDao.deleteBuildRecord(id)
    }

    suspend fun clearAllBuilds() = withContext(Dispatchers.IO) {
        buildRecordDao.clearAllBuilds()
    }

    // Default Seed Projects if first launch
    suspend fun seedInitialProjectsIfNeeded() = withContext(Dispatchers.IO) {
        // Create 2 rich default game projects so users can immediately test, play, edit, and build APKs
        val demoProject1 = GameProject(
            id = "demo_cyber_platformer",
            name = "Neon Cyber Runner",
            packageName = "com.flame.neonrunner",
            versionName = "1.0.0",
            versionCode = 1,
            genre = "Platformer",
            dimension = GameDimension.TWO_D,
            orientation = GameOrientation.LANDSCAPE,
            activeSceneId = "scene_neon_1",
            splashColorHex = "#0D0814",
            optimizationLevel = "High"
        )
        projectDao.insertProject(demoProject1)

        val demoProject2 = GameProject(
            id = "demo_fps_arena",
            name = "Cyberpunk 3D Arena",
            packageName = "com.flame.arena3d",
            versionName = "1.0.0",
            versionCode = 1,
            genre = "FPS",
            dimension = GameDimension.THREE_D,
            orientation = GameOrientation.LANDSCAPE,
            activeSceneId = "scene_fps_1",
            splashColorHex = "#050711",
            optimizationLevel = "Ultra"
        )
        projectDao.insertProject(demoProject2)
    }

    fun createTemplateScene(templateGenre: String, dimension: GameDimension): GameSceneData {
        val scene = GameSceneData(
            id = UUID.randomUUID().toString(),
            name = "Main Level",
            dimension = dimension,
            backgroundColorHex = if (dimension == GameDimension.TWO_D) "#0C1021" else "#080B15"
        )

        when (templateGenre) {
            "Platformer", "2D Neon Cyber Platformer" -> {
                // Player
                val player = GameObject(
                    name = "CyberHero",
                    tag = "Player",
                    position = Vector3D(0f, 2f, 0f),
                    scale = Vector3D(1.2f, 1.6f, 1f),
                    colorHex = "#00E5FF",
                    meshShape = MeshShape.CUBE,
                    hasPhysics = true,
                    is3DPhysics = false,
                    mass = 1.0f,
                    bounciness = 0.0f,
                    isCharacter = true,
                    moveSpeed = 6.0f,
                    jumpForce = 12.0f,
                    hasParticles = true,
                    particleColorHex = "#00E5FF"
                )
                // Ground platform
                val ground = GameObject(
                    name = "MainPlatform",
                    tag = "Ground",
                    position = Vector3D(0f, -2.5f, 0f),
                    scale = Vector3D(14f, 0.8f, 1f),
                    colorHex = "#37474F",
                    isStatic = true,
                    hasPhysics = true,
                    isKinematic = true
                )
                // Floating platforms
                val plat1 = GameObject(
                    name = "Platform_Left",
                    tag = "Ground",
                    position = Vector3D(-4.5f, 0.2f, 0f),
                    scale = Vector3D(3.5f, 0.5f, 1f),
                    colorHex = "#FF5722",
                    isStatic = true,
                    hasPhysics = true,
                    isKinematic = true
                )
                val plat2 = GameObject(
                    name = "Platform_Right",
                    tag = "Ground",
                    position = Vector3D(4.5f, 1.2f, 0f),
                    scale = Vector3D(3.5f, 0.5f, 1f),
                    colorHex = "#E91E63",
                    isStatic = true,
                    hasPhysics = true,
                    isKinematic = true
                )
                // Collectible Gems
                val gem1 = GameObject(
                    name = "EnergyCore_1",
                    tag = "Collectible",
                    position = Vector3D(-4.5f, 1.5f, 0f),
                    scale = Vector3D(0.6f, 0.6f, 0.6f),
                    colorHex = "#FFD700",
                    hasParticles = true,
                    particleColorHex = "#FFE082"
                )
                val gem2 = GameObject(
                    name = "EnergyCore_2",
                    tag = "Collectible",
                    position = Vector3D(4.5f, 2.5f, 0f),
                    scale = Vector3D(0.6f, 0.6f, 0.6f),
                    colorHex = "#FFD700",
                    hasParticles = true,
                    particleColorHex = "#FFE082"
                )
                // Enemy Droid
                val enemy = GameObject(
                    name = "SecurityDroid",
                    tag = "Enemy",
                    position = Vector3D(2.0f, -1.8f, 0f),
                    scale = Vector3D(1.0f, 1.0f, 1.0f),
                    colorHex = "#F44336",
                    isAI = true,
                    aiBehavior = "Patrol",
                    moveSpeed = 2.0f
                )
                scene.entities.addAll(listOf(player, ground, plat1, plat2, gem1, gem2, enemy))
            }
            "FPS", "3D FPS Arena", "Cyberpunk 3D Arena" -> {
                // 3D Player Camera & Body
                val player3D = GameObject(
                    name = "Player3D",
                    tag = "Player",
                    position = Vector3D(0f, 1f, 0f),
                    scale = Vector3D(1.0f, 2.0f, 1.0f),
                    colorHex = "#00E5FF",
                    meshShape = MeshShape.CYLINDER,
                    hasPhysics = true,
                    is3DPhysics = true,
                    isCharacter = true,
                    moveSpeed = 5.0f
                )
                // 3D Floor Arena
                val floor = GameObject(
                    name = "ArenaFloor",
                    tag = "Ground",
                    position = Vector3D(0f, -0.5f, 0f),
                    scale = Vector3D(18f, 0.5f, 18f),
                    colorHex = "#1E293B",
                    meshShape = MeshShape.PLANE,
                    isStatic = true,
                    hasPhysics = true,
                    is3DPhysics = true,
                    isKinematic = true
                )
                // Pillars / Obstacles
                val pillar1 = GameObject(
                    name = "Pillar_Alpha",
                    position = Vector3D(-4f, 2f, -4f),
                    scale = Vector3D(1.5f, 4f, 1.5f),
                    colorHex = "#FF5722",
                    meshShape = MeshShape.CUBE,
                    isStatic = true,
                    hasPhysics = true,
                    is3DPhysics = true,
                    isKinematic = true
                )
                val pillar2 = GameObject(
                    name = "Pillar_Beta",
                    position = Vector3D(4f, 2f, 4f),
                    scale = Vector3D(1.5f, 4f, 1.5f),
                    colorHex = "#7C4DFF",
                    meshShape = MeshShape.CUBE,
                    isStatic = true,
                    hasPhysics = true,
                    is3DPhysics = true,
                    isKinematic = true
                )
                val targetBot = GameObject(
                    name = "CombatDrone",
                    tag = "Enemy",
                    position = Vector3D(0f, 2f, -6f),
                    scale = Vector3D(1.2f, 1.2f, 1.2f),
                    colorHex = "#FF1744",
                    meshShape = MeshShape.SPHERE,
                    hasParticles = true,
                    particleColorHex = "#FF5252",
                    isAI = true,
                    aiBehavior = "Chase"
                )
                val light = GameObject(
                    name = "SunLight",
                    position = Vector3D(0f, 10f, 0f),
                    isLight = true,
                    lightType = LightType.DIRECTIONAL,
                    lightColorHex = "#FFE0B2",
                    lightIntensity = 2.0f
                )
                scene.entities.addAll(listOf(player3D, floor, pillar1, pillar2, targetBot, light))
            }
            "Space Defender Galaxy Shooter", "Space Shooter" -> {
                val ship = GameObject(
                    name = "StarFighter",
                    tag = "Player",
                    position = Vector3D(0f, -3.5f, 0f),
                    scale = Vector3D(1.2f, 1.2f, 1f),
                    colorHex = "#00E5FF",
                    meshShape = MeshShape.PYRAMID,
                    hasPhysics = true,
                    hasParticles = true,
                    particleColorHex = "#76FF03",
                    moveSpeed = 8.0f
                )
                val boss = GameObject(
                    name = "AlienDreadnought",
                    tag = "Enemy",
                    position = Vector3D(0f, 3.5f, 0f),
                    scale = Vector3D(3.0f, 1.5f, 1f),
                    colorHex = "#D500F9",
                    meshShape = MeshShape.CUBE,
                    hasParticles = true,
                    particleColorHex = "#FF4081",
                    isAI = true,
                    aiBehavior = "Patrol"
                )
                scene.entities.addAll(listOf(ship, boss))
            }
            else -> {
                // Default starter scene
                val cube = GameObject(
                    name = "MainPlayer",
                    tag = "Player",
                    position = Vector3D(0f, 1f, 0f),
                    colorHex = "#FF5722",
                    hasPhysics = true,
                    isCharacter = true
                )
                val floor = GameObject(
                    name = "Ground",
                    tag = "Ground",
                    position = Vector3D(0f, -1f, 0f),
                    scale = Vector3D(10f, 0.5f, 10f),
                    colorHex = "#263238",
                    isStatic = true,
                    hasPhysics = true,
                    isKinematic = true
                )
                scene.entities.addAll(listOf(cube, floor))
            }
        }

        // Add starter visual script graph
        val defaultGraph = VisualScriptGraph(
            name = "PlayerMovementGraph",
            nodes = mutableListOf(
                VisualNode(
                    title = "On Start",
                    category = NodeCategory.EVENT,
                    x = 60f,
                    y = 80f,
                    outputs = listOf(ScriptPort(name = "Trigger", isInput = false, dataType = "flow"))
                ),
                VisualNode(
                    title = "On Joystick Input",
                    category = NodeCategory.EVENT,
                    x = 60f,
                    y = 220f,
                    outputs = listOf(
                        ScriptPort(name = "Exec", isInput = false, dataType = "flow"),
                        ScriptPort(name = "Vector2", isInput = false, dataType = "vector")
                    )
                ),
                VisualNode(
                    title = "Move Entity",
                    category = NodeCategory.MOTION,
                    x = 360f,
                    y = 200f,
                    inputs = listOf(
                        ScriptPort(name = "Exec", isInput = true, dataType = "flow"),
                        ScriptPort(name = "Direction", isInput = true, dataType = "vector")
                    ),
                    outputs = listOf(ScriptPort(name = "Out", isInput = false, dataType = "flow")),
                    propertyValue = "Speed: 5.0"
                ),
                VisualNode(
                    title = "Play Sound",
                    category = NodeCategory.AUDIO,
                    x = 640f,
                    y = 200f,
                    inputs = listOf(ScriptPort(name = "Play", isInput = true, dataType = "flow")),
                    propertyValue = "sfx_jump"
                )
            ),
            connections = mutableListOf(
                NodeConnection(
                    fromNodeId = "node_joystick",
                    fromPortName = "Exec",
                    toNodeId = "node_move",
                    toPortName = "Exec"
                )
            )
        )
        scene.visualGraphs.add(defaultGraph)

        return scene
    }
}
