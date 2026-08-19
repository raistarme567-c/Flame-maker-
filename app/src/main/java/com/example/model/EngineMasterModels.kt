package com.example.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

// ==========================================
// 1. ENGINE MODE & PREVIEW CONFIG
// ==========================================
enum class EngineMode {
    BEGINNER,
    PRO
}

enum class DevicePreviewPreset(
    val title: String,
    val widthAspect: Float,
    val heightAspect: Float,
    val hasNotch: Boolean
) {
    PHONE_STANDARD("Phone 16:9 (1080p)", 16f, 9f, false),
    PHONE_TALL("Modern Flagship 19.5:9", 19.5f, 9f, true),
    FOLDABLE_UNFOLDED("Foldable 4:3 (Square)", 4f, 3f, false),
    TABLET_10_INCH("Tablet 16:10 (WQXGA)", 16f, 10f, false)
}

// ==========================================
// 2. SHADER GRAPH MODELS
// ==========================================
enum class ShaderNodeType {
    PBR_MASTER,
    TEXTURE_SAMPLE,
    COLOR_CONSTANT,
    FLOAT_CONSTANT,
    VECTOR3_CONSTANT,
    TIME,
    UV_COORDS,
    FRESNEL,
    NOISE_SIMPLEX,
    VORONOI,
    MATH_ADD,
    MATH_MULTIPLY,
    MATH_LERP,
    MATH_SINE,
    NORMAL_MAP_UNPACK,
    EMISSION_BLAST
}

data class ShaderNode(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    val type: ShaderNodeType,
    var x: Float = 100f,
    var y: Float = 100f,
    var value: String = "",
    val inputs: List<String> = emptyList(),
    val outputs: List<String> = emptyList()
)

data class ShaderConnection(
    val id: String = UUID.randomUUID().toString(),
    val fromNodeId: String,
    val fromPort: String,
    val toNodeId: String,
    val toPort: String
)

data class ShaderGraph(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Custom_PBR_Shader",
    val nodes: MutableList<ShaderNode> = mutableListOf(),
    val connections: MutableList<ShaderConnection> = mutableListOf()
)

// ==========================================
// 3. PBR MATERIAL MODELS
// ==========================================
data class PbrMaterial(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Default_Material",
    var baseColorHex: String = "#FF5722",
    var metallic: Float = 0.2f,
    var roughness: Float = 0.4f,
    var normalMapEnabled: Boolean = false,
    var emissionHex: String = "#000000",
    var emissionIntensity: Float = 0.0f,
    var alpha: Float = 1.0f,
    var uvTilingX: Float = 1.0f,
    var uvTilingY: Float = 1.0f,
    var isDoubleSided: Boolean = false
)

// ==========================================
// 4. ANIMATION & TIMELINE MODELS
// ==========================================
enum class TrackType {
    TRANSFORM,
    CAMERA,
    AUDIO,
    PARTICLE,
    EVENT_TRIGGER
}

data class Keyframe(
    val id: String = UUID.randomUUID().toString(),
    var timeSeconds: Float,
    var valueX: Float,
    var valueY: Float = 0f,
    var valueZ: Float = 0f
)

data class TimelineTrack(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val type: TrackType,
    val targetEntityId: String = "",
    val keyframes: MutableList<Keyframe> = mutableListOf()
)

data class AnimationClip(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "Idle_Sequence",
    var durationSeconds: Float = 4.0f,
    var isLooping: Boolean = true,
    val tracks: MutableList<TimelineTrack> = mutableListOf()
)

// ==========================================
// 5. PARTICLE EMITTER CONFIG
// ==========================================
enum class ParticlePreset {
    FIRE_BLAST,
    SMOKE_PLUME,
    CYBER_SPARKS,
    RAIN_WEATHER,
    SNOW_STORM,
    MAGIC_PORTAL
}

data class ParticleEmitterConfig(
    var preset: ParticlePreset = ParticlePreset.CYBER_SPARKS,
    var emissionRate: Int = 40,
    var maxParticles: Int = 120,
    var particleLife: Float = 1.2f,
    var startSpeed: Float = 4.5f,
    var startSize: Float = 8.0f,
    var gravityMultiplier: Float = -1.5f,
    var colorStartHex: String = "#00E5FF",
    var colorEndHex: String = "#7C4DFF",
    var burstCount: Int = 20,
    var isLooping: Boolean = true
)

// ==========================================
// 6. TERRAIN & WATER CONFIG
// ==========================================
enum class TerrainBrushMode {
    RAISE,
    LOWER,
    SMOOTH,
    PAINT_GRASS,
    PAINT_ROCK,
    PAINT_SAND
}

data class TerrainConfig(
    var gridSize: Int = 16,
    var heightScale: Float = 4.0f,
    var brushRadius: Float = 3.0f,
    var brushStrength: Float = 0.5f,
    var brushMode: TerrainBrushMode = TerrainBrushMode.RAISE,
    var foliageDensity: Float = 0.7f
)

data class WaterSystemConfig(
    var isWaterEnabled: Boolean = true,
    var waterType: String = "Ocean Waves", // Ocean, Lake, River, Sci-Fi Plasma
    var waterLevel: Float = 0.5f,
    var waveSpeed: Float = 1.2f,
    var waveHeight: Float = 0.3f,
    var waterColorHex: String = "#00B0FF",
    var foamEnabled: Boolean = true,
    var underwaterFog: Boolean = true
)

// ==========================================
// 7. MULTIPLAYER & NETWORKING
// ==========================================
enum class NetworkRole {
    OFFLINE,
    HOST_SERVER,
    JOIN_CLIENT
}

data class NetworkPlayerState(
    val playerId: String,
    val playerName: String,
    val pingMs: Int = 24,
    var posX: Float = 0f,
    var posY: Float = 0f,
    var posZ: Float = 0f,
    var score: Int = 0
)

data class MultiplayerRoom(
    var roomId: String = "ROOM-882",
    var roomName: String = "Flame Cyber Arena",
    var maxPlayers: Int = 8,
    var currentRole: NetworkRole = NetworkRole.HOST_SERVER,
    var isMatchmaking: Boolean = false,
    val connectedPlayers: MutableList<NetworkPlayerState> = mutableListOf()
)

// ==========================================
// 8. ASSET STORE & PACKAGE ITEM
// ==========================================
data class AssetStoreItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String, // 3D Models, VFX, Shaders, SFX, Scripts
    val author: String,
    val downloadSize: String,
    val rating: Float,
    val description: String,
    val license: String = "Free Open MIT",
    var isInstalled: Boolean = false
)

// ==========================================
// 9. CONSOLE LOG & COMMAND PALETTE
// ==========================================
enum class LogSeverity {
    INFO,
    WARNING,
    ERROR,
    FRAME_PROFILE
}

data class ConsoleLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val severity: LogSeverity,
    val tag: String,
    val message: String,
    val stackTrace: String? = null
)

data class CommandAction(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String,
    val onExecute: () -> Unit
)

// ==========================================
// 10. UNDO / REDO COMMAND
// ==========================================
interface EditorAction {
    val description: String
    fun execute()
    fun undo()
}
