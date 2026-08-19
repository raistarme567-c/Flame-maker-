package com.example.model

import java.util.UUID

// Game Engine Component Types
enum class ComponentType {
    TRANSFORM,
    SPRITE_RENDERER,
    MESH_RENDERER,
    CAMERA,
    RIGIDBODY_2D,
    RIGIDBODY_3D,
    COLLIDER,
    LIGHT,
    AUDIO_SOURCE,
    PARTICLE_SYSTEM,
    CHARACTER_CONTROLLER,
    NAV_AGENT,
    UI_ELEMENT,
    VISUAL_SCRIPT,
    CODE_SCRIPT
}

enum class MeshShape {
    CUBE, SPHERE, CYLINDER, PYRAMID, PLANE, TORUS
}

enum class ColliderShape {
    BOX, SPHERE, CAPSULE, MESH
}

enum class LightType {
    DIRECTIONAL, POINT, SPOT
}

enum class UIElementType {
    BUTTON, JOYSTICK, TEXT, HEALTH_BAR, SCORE
}

data class Vector3D(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    fun copy(): Vector3D = Vector3D(x, y, z)
}

data class ColorRGBA(
    val r: Float = 1f,
    val g: Float = 1f,
    val b: Float = 1f,
    val a: Float = 1f
) {
    fun toHex(): String = String.format("#%02X%02X%02X%02X", (a * 255).toInt(), (r * 255).toInt(), (g * 255).toInt(), (b * 255).toInt())
}

// Entity Model
data class GameObject(
    val id: String = UUID.randomUUID().toString(),
    var name: String = "GameObject",
    var tag: String = "Untagged",
    var isEnabled: Boolean = true,
    var isStatic: Boolean = false,
    var isSelected: Boolean = false,
    
    // Transform
    var position: Vector3D = Vector3D(0f, 0f, 0f),
    var rotation: Vector3D = Vector3D(0f, 0f, 0f),
    var scale: Vector3D = Vector3D(1f, 1f, 1f),
    
    // Render properties
    var meshShape: MeshShape = MeshShape.CUBE,
    var colorHex: String = "#FF5722",
    var spriteName: String = "default_sprite",
    var isWireframe: Boolean = false,
    var metallic: Float = 0.2f,
    var roughness: Float = 0.5f,
    
    // Physics
    var hasPhysics: Boolean = false,
    var is3DPhysics: Boolean = false,
    var mass: Float = 1.0f,
    var useGravity: Boolean = true,
    var isKinematic: Boolean = false,
    var velocity: Vector3D = Vector3D(0f, 0f, 0f),
    var bounciness: Float = 0.5f,
    var friction: Float = 0.2f,
    var colliderShape: ColliderShape = ColliderShape.BOX,
    var colliderSize: Vector3D = Vector3D(1f, 1f, 1f),
    var isTrigger: Boolean = false,
    
    // Particle System
    var hasParticles: Boolean = false,
    var particleCount: Int = 30,
    var particleSpeed: Float = 2.5f,
    var particleColorHex: String = "#FF9800",
    var particleLife: Float = 1.2f,
    
    // Light
    var isLight: Boolean = false,
    var lightType: LightType = LightType.POINT,
    var lightIntensity: Float = 1.5f,
    var lightRange: Float = 10f,
    var lightColorHex: String = "#FFFFFF",
    
    // Audio
    var hasAudio: Boolean = false,
    var audioClipName: String = "sfx_laser",
    var audioVolume: Float = 0.8f,
    var audioLoop: Boolean = false,
    
    // UI Element
    var isUI: Boolean = false,
    var uiType: UIElementType = UIElementType.BUTTON,
    var uiText: String = "Press Play",
    
    // Character / AI
    var isCharacter: Boolean = false,
    var moveSpeed: Float = 4.0f,
    var jumpForce: Float = 7.0f,
    var isGrounded: Boolean = true,
    var isAI: Boolean = false,
    var aiBehavior: String = "Patrol", // Patrol, Chase, Flee, Wander
    
    // Scripting
    var scriptName: String = "",
    var scriptCode: String = "",
    var visualScriptGraphId: String = ""
) {
    fun duplicate(): GameObject {
        return this.copy(
            id = UUID.randomUUID().toString(),
            name = "${this.name}_Copy",
            position = Vector3D(this.position.x + 0.5f, this.position.y, this.position.z + 0.5f),
            rotation = this.rotation.copy(),
            scale = this.scale.copy(),
            velocity = Vector3D(0f, 0f, 0f)
        )
    }
}

// Visual Scripting Node System
enum class NodeCategory {
    EVENT, MOTION, PHYSICS, LOGIC, MATH, AUDIO, SPAWN, UI, AI
}

data class ScriptPort(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val isInput: Boolean,
    val dataType: String = "flow" // flow, number, string, boolean, vector
)

data class VisualNode(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: NodeCategory,
    var x: Float = 100f,
    var y: Float = 100f,
    val inputs: List<ScriptPort> = emptyList(),
    val outputs: List<ScriptPort> = emptyList(),
    var propertyValue: String = "",
    var extraParams: Map<String, String> = emptyMap()
)

data class NodeConnection(
    val id: String = UUID.randomUUID().toString(),
    val fromNodeId: String,
    val fromPortName: String,
    val toNodeId: String,
    val toPortName: String
)

data class VisualScriptGraph(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "PlayerControllerGraph",
    val nodes: MutableList<VisualNode> = mutableListOf(),
    val connections: MutableList<NodeConnection> = mutableListOf()
)

// Active Scene runtime container
data class GameSceneData(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Level 1",
    val dimension: GameDimension = GameDimension.TWO_D,
    val backgroundColorHex: String = "#0A0D18",
    val gravity: Vector3D = Vector3D(0f, -9.8f, 0f),
    val entities: MutableList<GameObject> = mutableListOf(),
    val visualGraphs: MutableList<VisualScriptGraph> = mutableListOf()
)
