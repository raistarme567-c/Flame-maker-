package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EditorActiveTab
import com.example.ui.viewmodel.EngineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EngineViewModel,
    onNavigateBack: () -> Unit,
    onOpenBuildCenter: () -> Unit
) {
    val project by viewModel.currentProject.collectAsState()
    val scene by viewModel.currentScene.collectAsState()
    val selectedEntity by viewModel.selectedEntity.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()
    val stats by viewModel.engineStats.collectAsState()
    val aiMessages by viewModel.aiMessages.collectAsState()
    val isAiGenerating by viewModel.isAiGenerating.collectAsState()
    val engineMode by viewModel.engineMode.collectAsState()
    val devicePreset by viewModel.devicePreviewPreset.collectAsState()

    val shaderGraph by viewModel.shaderGraph.collectAsState()
    val activeMaterial by viewModel.activeMaterial.collectAsState()
    val activeAnimationClip by viewModel.activeAnimationClip.collectAsState()
    val particleConfig by viewModel.particleConfig.collectAsState()
    val terrainConfig by viewModel.terrainConfig.collectAsState()
    val waterConfig by viewModel.waterConfig.collectAsState()
    val multiplayerRoom by viewModel.multiplayerRoom.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()
    val assetStoreItems by viewModel.assetStoreItems.collectAsState()

    var showCommandPalette by remember { mutableStateOf(false) }
    var showDeviceMenu by remember { mutableStateOf(false) }
    var showSaveToast by remember { mutableStateOf(false) }

    val commands = remember {
        listOf(
            CommandAction("cmd_save", "Save Project", "Write scene & assets to local storage", "Project") {
                viewModel.saveCurrentProject()
                showSaveToast = true
            },
            CommandAction("cmd_build", "Build & Export APK", "Compile standalone Android APK", "Build") {
                onOpenBuildCenter()
            },
            CommandAction("cmd_cube", "Add 3D Cube", "Spawn primitive cube at origin", "Create") {
                viewModel.addEntity("New Cube", MeshShape.CUBE, "#FF5722", true)
            },
            CommandAction("cmd_sphere", "Add 3D Sphere", "Spawn primitive sphere with physics", "Create") {
                viewModel.addEntity("New Sphere", MeshShape.SPHERE, "#00E5FF", true)
            },
            CommandAction("cmd_droid", "Add AI Droid Enemy", "Spawn AI enemy with patrol behavior", "Create") {
                viewModel.addEntity("AI Droid", MeshShape.CYLINDER, "#E63946", true)
            },
            CommandAction("cmd_shader", "Open Shader Graph", "Node-based visual shader editor", "Editors") {
                viewModel.setActiveTab(EditorActiveTab.SHADER_GRAPH)
            },
            CommandAction("cmd_mat", "Open Material Studio", "PBR surface material inspector", "Editors") {
                viewModel.setActiveTab(EditorActiveTab.MATERIAL_STUDIO)
            },
            CommandAction("cmd_particles", "Open VFX Particle Studio", "Real-time particle simulator", "Editors") {
                viewModel.setActiveTab(EditorActiveTab.PARTICLES)
            },
            CommandAction("cmd_multiplayer", "Open Multiplayer Hub", "LAN / Peer-to-peer sync testing", "Network") {
                viewModel.setActiveTab(EditorActiveTab.MULTIPLAYER)
            },
            CommandAction("cmd_profiler", "Open Engine Profiler", "FPS, draw calls, and vertex stats", "Performance") {
                viewModel.setActiveTab(EditorActiveTab.PROFILER)
            }
        )
    }

    Scaffold(
        containerColor = CanvasDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = project?.name ?: "Game Studio",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (engineMode == EngineMode.PRO) NeonPurple.copy(alpha = 0.25f) else FlameOrange.copy(alpha = 0.25f),
                                modifier = Modifier.testTag("engine_mode_toggle_badge")
                            ) {
                                Text(
                                    text = engineMode.name,
                                    color = if (engineMode == EngineMode.PRO) NeonPurple else FlameOrange,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "${project?.dimension?.name ?: "2D"} • ${scene.entities.size} entities",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_to_home_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    // Command Palette
                    IconButton(onClick = { showCommandPalette = true }, modifier = Modifier.testTag("command_palette_button")) {
                        Icon(Icons.Default.Search, contentDescription = "Commands", tint = PlasmaCyan)
                    }

                    // Mode switch
                    IconButton(onClick = { viewModel.toggleEngineMode() }) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch Mode", tint = FlameAmber)
                    }

                    // Play / Pause Simulation Controls
                    IconButton(
                        onClick = { viewModel.togglePlayMode() },
                        modifier = Modifier.testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (viewModel.simulator.isPlaying) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Play/Stop",
                            tint = if (viewModel.simulator.isPlaying) FlameRed else MatrixGreen
                        )
                    }

                    if (viewModel.simulator.isPlaying) {
                        IconButton(onClick = { viewModel.restartSimulation() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Restart", tint = PlasmaCyan)
                        }
                    }

                    // Save Project
                    IconButton(
                        onClick = {
                            viewModel.saveCurrentProject()
                            showSaveToast = true
                        },
                        modifier = Modifier.testTag("save_project_button")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = TextSecondary)
                    }

                    // BUILD APK Quick Action
                    Button(
                        onClick = onOpenBuildCenter,
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("quick_build_apk_button")
                    ) {
                        Icon(Icons.Default.Android, contentDescription = null, tint = CanvasDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("BUILD APK", color = CanvasDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        bottomBar = {
            // Horizontal scrollable editor tabs
            Surface(
                color = SurfaceDark,
                tonalElevation = 6.dp,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EditorTabPill("Viewport", Icons.Default.ViewInAr, activeTab == EditorActiveTab.VIEWPORT) {
                        viewModel.setActiveTab(EditorActiveTab.VIEWPORT)
                    }
                    EditorTabPill("Hierarchy", Icons.Default.AccountTree, activeTab == EditorActiveTab.HIERARCHY) {
                        viewModel.setActiveTab(EditorActiveTab.HIERARCHY)
                    }
                    EditorTabPill("Inspector", Icons.Default.Tune, activeTab == EditorActiveTab.INSPECTOR) {
                        viewModel.setActiveTab(EditorActiveTab.INSPECTOR)
                    }

                    if (engineMode == EngineMode.PRO) {
                        EditorTabPill("Visual Script", Icons.Default.Hub, activeTab == EditorActiveTab.VISUAL_SCRIPT) {
                            viewModel.setActiveTab(EditorActiveTab.VISUAL_SCRIPT)
                        }
                        EditorTabPill("Code", Icons.Default.Code, activeTab == EditorActiveTab.CODE) {
                            viewModel.setActiveTab(EditorActiveTab.CODE)
                        }
                        EditorTabPill("Shader Graph", Icons.Default.AutoFixHigh, activeTab == EditorActiveTab.SHADER_GRAPH) {
                            viewModel.setActiveTab(EditorActiveTab.SHADER_GRAPH)
                        }
                        EditorTabPill("Material Studio", Icons.Default.Palette, activeTab == EditorActiveTab.MATERIAL_STUDIO) {
                            viewModel.setActiveTab(EditorActiveTab.MATERIAL_STUDIO)
                        }
                        EditorTabPill("Timeline", Icons.Default.MovieFilter, activeTab == EditorActiveTab.ANIMATION_TIMELINE) {
                            viewModel.setActiveTab(EditorActiveTab.ANIMATION_TIMELINE)
                        }
                        EditorTabPill("Particles", Icons.Default.Grain, activeTab == EditorActiveTab.PARTICLES) {
                            viewModel.setActiveTab(EditorActiveTab.PARTICLES)
                        }
                        EditorTabPill("Terrain & Water", Icons.Default.Terrain, activeTab == EditorActiveTab.TERRAIN_WATER) {
                            viewModel.setActiveTab(EditorActiveTab.TERRAIN_WATER)
                        }
                        EditorTabPill("Multiplayer", Icons.Default.Wifi, activeTab == EditorActiveTab.MULTIPLAYER) {
                            viewModel.setActiveTab(EditorActiveTab.MULTIPLAYER)
                        }
                    }

                    EditorTabPill("Flame AI", Icons.Default.AutoAwesome, activeTab == EditorActiveTab.FLAME_AI) {
                        viewModel.setActiveTab(EditorActiveTab.FLAME_AI)
                    }
                    EditorTabPill("Profiler", Icons.Default.Speed, activeTab == EditorActiveTab.PROFILER) {
                        viewModel.setActiveTab(EditorActiveTab.PROFILER)
                    }
                    EditorTabPill("Asset Store", Icons.Default.Storefront, activeTab == EditorActiveTab.ASSET_STORE) {
                        viewModel.setActiveTab(EditorActiveTab.ASSET_STORE)
                    }
                    EditorTabPill("Console", Icons.Default.Terminal, activeTab == EditorActiveTab.CONSOLE) {
                        viewModel.setActiveTab(EditorActiveTab.CONSOLE)
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                EditorActiveTab.VIEWPORT -> {
                    ViewportCanvas(
                        scene = scene,
                        selectedEntity = selectedEntity,
                        simulator = viewModel.simulator,
                        onSelectEntity = { viewModel.selectEntity(it) },
                        onUpdateEntityPosition = { x, y, z ->
                            viewModel.updateSelectedEntity { ent ->
                                ent.position.x = x
                                ent.position.y = y
                                ent.position.z = z
                            }
                        }
                    )
                }
                EditorActiveTab.HIERARCHY -> {
                    HierarchyPanel(
                        entities = scene.entities,
                        selectedEntity = selectedEntity,
                        onSelectEntity = {
                            viewModel.selectEntity(it)
                            viewModel.setActiveTab(EditorActiveTab.INSPECTOR)
                        },
                        onAddEntity = { name, shape, color, hasPhysics ->
                            viewModel.addEntity(name, shape, color, hasPhysics)
                        },
                        onDuplicateEntity = { viewModel.duplicateSelectedEntity() },
                        onDeleteEntity = { viewModel.deleteSelectedEntity() },
                        onToggleVisibility = { ent ->
                            viewModel.updateSelectedEntity { it.isEnabled = !it.isEnabled }
                        }
                    )
                }
                EditorActiveTab.INSPECTOR -> {
                    InspectorPanel(
                        entity = selectedEntity,
                        onUpdateEntity = { modifier ->
                            viewModel.updateSelectedEntity(modifier)
                        }
                    )
                }
                EditorActiveTab.VISUAL_SCRIPT -> {
                    VisualScriptCanvas(
                        graph = scene.visualGraphs.firstOrNull(),
                        onAddNode = { viewModel.addVisualNode(it) },
                        onDeleteNode = { viewModel.deleteVisualNode(it) },
                        onConnectNodes = { fromId, fromPort, toId, toPort ->
                            viewModel.connectVisualNodes(fromId, fromPort, toId, toPort)
                        }
                    )
                }
                EditorActiveTab.CODE -> {
                    CodeEditorView(
                        selectedEntity = selectedEntity,
                        onSaveScript = { code ->
                            viewModel.updateSelectedEntity { it.scriptCode = code }
                        }
                    )
                }
                EditorActiveTab.SHADER_GRAPH -> {
                    ShaderEditorPanel(
                        shaderGraph = shaderGraph,
                        onAddNode = { node ->
                            shaderGraph.nodes.add(node)
                        },
                        onDeleteNode = { nodeId ->
                            shaderGraph.nodes.removeAll { it.id == nodeId }
                        },
                        onConnect = { fromId, fromPort, toId, toPort ->
                            shaderGraph.connections.add(ShaderConnection(fromNodeId = fromId, fromPort = fromPort, toNodeId = toId, toPort = toPort))
                        }
                    )
                }
                EditorActiveTab.MATERIAL_STUDIO -> {
                    MaterialEditorPanel(
                        material = activeMaterial,
                        onUpdateMaterial = { viewModel.updateActiveMaterial(it) }
                    )
                }
                EditorActiveTab.ANIMATION_TIMELINE -> {
                    AnimationTimelinePanel(
                        activeClip = activeAnimationClip,
                        onAddKeyframe = { trackId, time, value ->
                            activeAnimationClip.tracks.find { it.id == trackId }?.keyframes?.add(
                                Keyframe(timeSeconds = time, valueX = value)
                            )
                        }
                    )
                }
                EditorActiveTab.PARTICLES -> {
                    ParticleEditorPanel(
                        config = particleConfig,
                        onUpdateConfig = { viewModel.updateParticleConfig(it) }
                    )
                }
                EditorActiveTab.TERRAIN_WATER -> {
                    TerrainWaterPanel(
                        terrainConfig = terrainConfig,
                        waterConfig = waterConfig,
                        onUpdateTerrain = { viewModel.updateTerrainConfig(it) },
                        onUpdateWater = { viewModel.updateWaterConfig(it) }
                    )
                }
                EditorActiveTab.MULTIPLAYER -> {
                    MultiplayerPanel(
                        room = multiplayerRoom,
                        onRoleChange = { viewModel.setMultiplayerRole(it) }
                    )
                }
                EditorActiveTab.FLAME_AI -> {
                    FlameAiPanel(
                        messages = aiMessages,
                        isGenerating = isAiGenerating,
                        selectedEntity = selectedEntity,
                        onSendPrompt = { viewModel.sendAiPrompt(it) },
                        onApplyCodeToEntity = { code ->
                            viewModel.updateSelectedEntity { it.scriptCode = code }
                            viewModel.setActiveTab(EditorActiveTab.CODE)
                        }
                    )
                }
                EditorActiveTab.PROFILER -> {
                    ProfilerPanel(
                        stats = stats,
                        currentOptimizationLevel = project?.optimizationLevel ?: "High",
                        onSelectOptimization = { opt ->
                            project?.let {
                                viewModel.openProject(it.copy(optimizationLevel = opt))
                            }
                        }
                    )
                }
                EditorActiveTab.ASSET_STORE -> {
                    AssetLibraryStorePanel(
                        assets = assetStoreItems,
                        onInstallAsset = { viewModel.installAssetStoreItem(it) }
                    )
                }
                EditorActiveTab.CONSOLE -> {
                    ConsoleDebuggerPanel(
                        logs = consoleLogs,
                        onClearLogs = { viewModel.clearConsoleLogs() }
                    )
                }
                EditorActiveTab.BUILD -> {
                    // Handled in full screen BUILD center
                }
            }

            if (showCommandPalette) {
                CommandPaletteModal(
                    actions = commands,
                    onDismiss = { showCommandPalette = false }
                )
            }

            if (showSaveToast) {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = SurfaceCard,
                    contentColor = MatrixGreen,
                    action = {
                        TextButton(onClick = { showSaveToast = false }) {
                            Text("OK", color = PlasmaCyan)
                        }
                    }
                ) {
                    Text("Project '${project?.name}' saved to local storage!")
                }
            }
        }
    }
}

@Composable
private fun EditorTabPill(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) FlameOrange else SurfaceCardLight,
        modifier = Modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) CanvasDark else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                color = if (isSelected) CanvasDark else TextSecondary,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
