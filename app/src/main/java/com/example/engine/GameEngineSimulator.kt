package com.example.engine

import android.graphics.Color as AndroidColor
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.example.model.*
import kotlin.math.*

data class RuntimeParticle(
    var x: Float,
    var y: Float,
    var z: Float,
    var vx: Float,
    var vy: Float,
    var vz: Float,
    var life: Float,
    val maxLife: Float,
    val color: Color,
    val size: Float
)

data class CameraTransform(
    var posX: Float = 0f,
    var posY: Float = 2f,
    var posZ: Float = -8f,
    var yaw: Float = 0f,
    var pitch: Float = 15f,
    var zoom: Float = 1.0f
)

data class EngineStats(
    val fps: Int = 60,
    val frameTimeMs: Float = 16.6f,
    val drawCalls: Int = 12,
    val vertexCount: Int = 348,
    val triangleCount: Int = 116,
    val activeBodies: Int = 6,
    val activeParticles: Int = 24,
    val memoryMb: Int = 42,
    val score: Int = 0
)

class GameEngineSimulator {

    var isPlaying: Boolean = false
    var isPaused: Boolean = false
    val camera = CameraTransform()
    val particles = mutableListOf<RuntimeParticle>()
    
    var score: Int = 0
    var fpsCounter: Int = 60
    var frameDurationMs: Float = 16.6f
    
    // Joystick inputs from UI
    var inputJoystickX: Float = 0f
    var inputJoystickY: Float = 0f
    var inputJumpPressed: Boolean = false
    var inputActionPressed: Boolean = false

    private var lastTickTime: Long = System.currentTimeMillis()
    private var framesAccumulated = 0
    private var timeAccumulated = 0L

    fun reset() {
        particles.clear()
        score = 0
        inputJoystickX = 0f
        inputJoystickY = 0f
        inputJumpPressed = false
        inputActionPressed = false
        lastTickTime = System.currentTimeMillis()
    }

    fun tick(scene: GameSceneData, deltaSeconds: Float) {
        // Calculate real FPS
        framesAccumulated++
        val now = System.currentTimeMillis()
        if (now - timeAccumulated >= 500) {
            fpsCounter = ((framesAccumulated * 1000f) / (now - timeAccumulated)).toInt().coerceIn(20, 120)
            frameDurationMs = 1000f / fpsCounter.coerceAtLeast(1)
            framesAccumulated = 0
            timeAccumulated = now
        }

        if (!isPlaying || isPaused) return

        val dt = deltaSeconds.coerceIn(0.001f, 0.05f)

        // 1. Process Entities Physics and Game Logic
        val players = scene.entities.filter { it.isCharacter || it.tag == "Player" }
        val grounds = scene.entities.filter { it.tag == "Ground" || it.isStatic }
        val collectibles = scene.entities.filter { it.tag == "Collectible" && it.isEnabled }
        val enemies = scene.entities.filter { it.isAI || it.tag == "Enemy" }

        // Update Players
        players.forEach { player ->
            // Movement from Joystick
            val moveX = inputJoystickX * player.moveSpeed
            val moveZ = inputJoystickY * player.moveSpeed

            if (scene.dimension == GameDimension.TWO_D) {
                player.position.x += moveX * dt
                
                // Gravity & Jump in 2D
                if (player.hasPhysics) {
                    player.velocity.y += scene.gravity.y * dt
                    player.position.y += player.velocity.y * dt

                    // Check ground collision
                    var onGround = false
                    grounds.forEach { ground ->
                        val halfWidthG = ground.scale.x / 2f
                        val halfHeightG = ground.scale.y / 2f
                        val halfWidthP = player.scale.x / 2f
                        val halfHeightP = player.scale.y / 2f

                        val overlapsX = abs(player.position.x - ground.position.x) < (halfWidthG + halfWidthP)
                        val overlapsY = (player.position.y - ground.position.y) in (0f..(halfHeightG + halfHeightP + 0.3f))

                        if (overlapsX && overlapsY && player.velocity.y <= 0) {
                            player.position.y = ground.position.y + halfHeightG + halfHeightP
                            player.velocity.y = 0f
                            onGround = true
                        }
                    }
                    player.isGrounded = onGround

                    if (inputJumpPressed && player.isGrounded) {
                        player.velocity.y = player.jumpForce
                        player.isGrounded = false
                        // Emit jump sparks
                        emitParticles(player.position.x, player.position.y - 0.5f, player.position.z, 12, Color(0xFF00E5FF))
                    }
                }
            } else {
                // 3D Player movement
                player.position.x += moveX * dt
                player.position.z -= moveZ * dt
                
                if (inputJumpPressed && player.isGrounded) {
                    player.velocity.y = 6.0f
                    player.isGrounded = false
                }
                if (player.hasPhysics) {
                    player.velocity.y += scene.gravity.y * dt
                    player.position.y += player.velocity.y * dt
                    if (player.position.y <= 1.0f) {
                        player.position.y = 1.0f
                        player.velocity.y = 0f
                        player.isGrounded = true
                    }
                }
            }

            // Check Collectibles collision
            collectibles.forEach { item ->
                val dist = sqrt(
                    (player.position.x - item.position.x).pow(2) +
                    (player.position.y - item.position.y).pow(2) +
                    (player.position.z - item.position.z).pow(2)
                )
                if (dist < (player.scale.x / 2f + item.scale.x / 2f + 0.3f)) {
                    item.isEnabled = false
                    score += 100
                    emitParticles(item.position.x, item.position.y, item.position.z, 20, Color(0xFFFFD700))
                }
            }
        }

        // Update Enemies AI
        enemies.forEach { enemy ->
            if (enemy.isAI) {
                when (enemy.aiBehavior) {
                    "Patrol" -> {
                        // Oscillate left/right
                        enemy.position.x += sin(now * 0.003f) * enemy.moveSpeed * dt
                    }
                    "Chase" -> {
                        players.firstOrNull()?.let { target ->
                            val dx = target.position.x - enemy.position.x
                            val dz = target.position.z - enemy.position.z
                            val len = sqrt(dx * dx + dz * dz).coerceAtLeast(0.01f)
                            if (len > 1.2f) {
                                enemy.position.x += (dx / len) * enemy.moveSpeed * dt
                                enemy.position.z += (dz / len) * enemy.moveSpeed * dt
                            }
                        }
                    }
                }
            }
        }

        // Update Particles
        val it = particles.iterator()
        while (it.hasNext()) {
            val p = it.next()
            p.life -= dt
            p.x += p.vx * dt
            p.y += p.vy * dt
            p.z += p.vz * dt
            p.vy -= 4.0f * dt // gentle gravity
            if (p.life <= 0) {
                it.remove()
            }
        }
    }

    fun emitParticles(x: Float, y: Float, z: Float, count: Int, color: Color) {
        for (i in 0 until count) {
            val angle = (Math.random() * Math.PI * 2).toFloat()
            val speed = (Math.random() * 3.5 + 1.0).toFloat()
            particles.add(
                RuntimeParticle(
                    x = x,
                    y = y,
                    z = z,
                    vx = cos(angle) * speed,
                    vy = (sin(angle) * speed).coerceAtLeast(0.5f),
                    vz = (Math.random() * 2.0 - 1.0).toFloat(),
                    life = (Math.random() * 0.8 + 0.4).toFloat(),
                    maxLife = 1.0f,
                    color = color,
                    size = (Math.random() * 8.0 + 4.0).toFloat()
                )
            )
        }
    }

    fun getStats(scene: GameSceneData): EngineStats {
        val totalVertices = scene.entities.size * 24 + particles.size * 4
        return EngineStats(
            fps = fpsCounter,
            frameTimeMs = frameDurationMs,
            drawCalls = scene.entities.size + (if (particles.isNotEmpty()) 1 else 0) + 4,
            vertexCount = totalVertices,
            triangleCount = totalVertices / 3,
            activeBodies = scene.entities.count { it.hasPhysics },
            activeParticles = particles.size,
            memoryMb = 38 + scene.entities.size * 2,
            score = score
        )
    }

    // 3D Perspective Projection Math
    fun project3DPoint(
        point: Vector3D,
        screenWidth: Float,
        screenHeight: Float
    ): Offset? {
        // Rotate around Camera Yaw and Pitch
        val radYaw = Math.toRadians(camera.yaw.toDouble())
        val radPitch = Math.toRadians(camera.pitch.toDouble())

        // Translate relative to camera
        val tx = point.x - camera.posX
        val ty = point.y - camera.posY
        val tz = point.z - camera.posZ

        // Apply Yaw (Y rotation)
        val x1 = (tx * cos(radYaw) - tz * sin(radYaw)).toFloat()
        val z1 = (tx * sin(radYaw) + tz * cos(radYaw)).toFloat()
        val y1 = ty

        // Apply Pitch (X rotation)
        val y2 = (y1 * cos(radPitch) - z1 * sin(radPitch)).toFloat()
        val z2 = (y1 * sin(radPitch) + z1 * cos(radPitch)).toFloat()
        val x2 = x1

        if (z2 <= 0.1f) return null // Behind camera

        val fov = 350f * camera.zoom
        val projX = (x2 / z2) * fov + screenWidth / 2f
        val projY = (-y2 / z2) * fov + screenHeight / 2f

        return Offset(projX, projY)
    }
}
