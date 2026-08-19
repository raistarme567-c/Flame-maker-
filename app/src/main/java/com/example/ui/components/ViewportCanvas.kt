package com.example.ui.components

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GameEngineSimulator
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.*

@Composable
fun ViewportCanvas(
    scene: GameSceneData,
    selectedEntity: GameObject?,
    simulator: GameEngineSimulator,
    onSelectEntity: (GameObject?) -> Unit,
    onUpdateEntityPosition: (Float, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("viewport_canvas")
                .pointerInput(scene, simulator.isPlaying) {
                    if (!simulator.isPlaying) {
                        detectTapGestures { tapOffset ->
                            // Hit test entities in 2D
                            var clickedEntity: GameObject? = null
                            val centerX = size.width / 2f
                            val centerY = size.height / 2f
                            val scaleFactor = 40f

                            for (entity in scene.entities.reversed()) {
                                val entScreenX = centerX + entity.position.x * scaleFactor
                                val entScreenY = centerY - entity.position.y * scaleFactor
                                val entWidth = (entity.scale.x * scaleFactor).coerceAtLeast(24f)
                                val entHeight = (entity.scale.y * scaleFactor).coerceAtLeast(24f)

                                if (tapOffset.x in (entScreenX - entWidth / 2)..(entScreenX + entWidth / 2) &&
                                    tapOffset.y in (entScreenY - entHeight / 2)..(entScreenY + entHeight / 2)
                                ) {
                                    clickedEntity = entity
                                    break
                                }
                            }
                            onSelectEntity(clickedEntity)
                        }
                    }
                }
                .pointerInput(selectedEntity, simulator.isPlaying, scene.dimension) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        if (!simulator.isPlaying) {
                            if (selectedEntity != null) {
                                // Move selected entity with drag
                                val deltaX = dragAmount.x / 40f
                                val deltaY = -dragAmount.y / 40f
                                onUpdateEntityPosition(
                                    selectedEntity.position.x + deltaX,
                                    selectedEntity.position.y + deltaY,
                                    selectedEntity.position.z
                                )
                            } else {
                                // Orbit / Pan Camera
                                if (scene.dimension == GameDimension.THREE_D) {
                                    simulator.camera.yaw += dragAmount.x * 0.5f
                                    simulator.camera.pitch = (simulator.camera.pitch - dragAmount.y * 0.5f).coerceIn(-60f, 85f)
                                } else {
                                    simulator.camera.posX -= dragAmount.x * 0.05f
                                    simulator.camera.posY += dragAmount.y * 0.05f
                                }
                            }
                        }
                    }
                }
        ) {
            canvasSize = size

            if (scene.dimension == GameDimension.TWO_D) {
                draw2DScene(scene, selectedEntity, simulator)
            } else {
                draw3DScene(scene, selectedEntity, simulator)
            }

            // Draw Live Particles
            simulator.particles.forEach { p ->
                val alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
                val screenPos = if (scene.dimension == GameDimension.TWO_D) {
                    Offset(
                        size.width / 2f + p.x * 40f,
                        size.height / 2f - p.y * 40f
                    )
                } else {
                    simulator.project3DPoint(Vector3D(p.x, p.y, p.z), size.width, size.height)
                }

                screenPos?.let { pos ->
                    drawCircle(
                        color = p.color.copy(alpha = alpha),
                        radius = p.size * alpha,
                        center = pos
                    )
                }
            }
        }

        // Play Mode Virtual On-Screen Controller (Dual-Touch Joysticks)
        if (simulator.isPlaying) {
            PlayModeVirtualControls(
                simulator = simulator,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        // Live Mode Watermark / Simulation HUD
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(SurfaceCard.copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (simulator.isPlaying) MatrixGreen else FlameOrange, CircleShape)
            )
            Text(
                text = if (simulator.isPlaying) "PLAYING (Score: ${simulator.score})" else "EDIT MODE",
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${simulator.fpsCounter} FPS",
                color = PlasmaCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun DrawScope.draw2DScene(
    scene: GameSceneData,
    selectedEntity: GameObject?,
    simulator: GameEngineSimulator
) {
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val scaleFactor = 40f

    // 1. Draw Grid lines
    val gridStep = 40f
    for (x in 0..(size.width.toInt()) step gridStep.toInt()) {
        drawLine(
            color = BorderSubtle.copy(alpha = 0.3f),
            start = Offset(x.toFloat(), 0f),
            end = Offset(x.toFloat(), size.height),
            strokeWidth = 1f
        )
    }
    for (y in 0..(size.height.toInt()) step gridStep.toInt()) {
        drawLine(
            color = BorderSubtle.copy(alpha = 0.3f),
            start = Offset(0f, y.toFloat()),
            end = Offset(size.width, y.toFloat()),
            strokeWidth = 1f
        )
    }

    // Origin Axes
    drawLine(Color.Red.copy(alpha = 0.6f), Offset(centerX - 40f, centerY), Offset(centerX + 40f, centerY), 2f)
    drawLine(MatrixGreen.copy(alpha = 0.6f), Offset(centerX, centerY - 40f), Offset(centerX, centerY + 40f), 2f)

    // 2. Draw Entities
    scene.entities.forEach { entity ->
        if (!entity.isEnabled) return@forEach

        val entScreenX = centerX + entity.position.x * scaleFactor
        val entScreenY = centerY - entity.position.y * scaleFactor
        val entWidth = (entity.scale.x * scaleFactor).coerceAtLeast(16f)
        val entHeight = (entity.scale.y * scaleFactor).coerceAtLeast(16f)

        val color = parseHexColor(entity.colorHex)

        when (entity.meshShape) {
            MeshShape.SPHERE -> {
                drawCircle(
                    color = color,
                    radius = entWidth / 2f,
                    center = Offset(entScreenX, entScreenY)
                )
            }
            MeshShape.PYRAMID -> {
                val path = Path().apply {
                    moveTo(entScreenX, entScreenY - entHeight / 2f)
                    lineTo(entScreenX + entWidth / 2f, entScreenY + entHeight / 2f)
                    lineTo(entScreenX - entWidth / 2f, entScreenY + entHeight / 2f)
                    close()
                }
                drawPath(path, color)
            }
            else -> {
                // Rectangle / Cube
                drawRoundRect(
                    color = color,
                    topLeft = Offset(entScreenX - entWidth / 2f, entScreenY - entHeight / 2f),
                    size = Size(entWidth, entHeight),
                    cornerRadius = CornerRadius(4f, 4f)
                )
            }
        }

        // Draw Selection Outline & Gizmo
        if (entity.id == selectedEntity?.id) {
            drawRoundRect(
                color = PlasmaCyan,
                topLeft = Offset(entScreenX - entWidth / 2f - 4f, entScreenY - entHeight / 2f - 4f),
                size = Size(entWidth + 8f, entHeight + 8f),
                cornerRadius = CornerRadius(6f, 6f),
                style = Stroke(width = 2.5f)
            )

            // Gizmo handle arrows
            drawLine(Color.Red, Offset(entScreenX, entScreenY), Offset(entScreenX + 35f, entScreenY), 3f)
            drawLine(MatrixGreen, Offset(entScreenX, entScreenY), Offset(entScreenX, entScreenY - 35f), 3f)
        }
    }
}

private fun DrawScope.draw3DScene(
    scene: GameSceneData,
    selectedEntity: GameObject?,
    simulator: GameEngineSimulator
) {
    // Draw 3D Ground Grid
    val gridSize = 8
    val step = 2f
    for (i in -gridSize..gridSize) {
        val p1 = simulator.project3DPoint(Vector3D(i * step, 0f, -gridSize * step), size.width, size.height)
        val p2 = simulator.project3DPoint(Vector3D(i * step, 0f, gridSize * step), size.width, size.height)
        if (p1 != null && p2 != null) {
            drawLine(BorderSubtle.copy(alpha = 0.4f), p1, p2, 1f)
        }

        val p3 = simulator.project3DPoint(Vector3D(-gridSize * step, 0f, i * step), size.width, size.height)
        val p4 = simulator.project3DPoint(Vector3D(gridSize * step, 0f, i * step), size.width, size.height)
        if (p3 != null && p4 != null) {
            drawLine(BorderSubtle.copy(alpha = 0.4f), p3, p4, 1f)
        }
    }

    // Draw 3D Entities (Software 3D Box / Mesh projection)
    scene.entities.forEach { entity ->
        if (!entity.isEnabled) return@forEach

        val pos = entity.position
        val hw = entity.scale.x / 2f
        val hh = entity.scale.y / 2f
        val hd = entity.scale.z / 2f

        val vertices = listOf(
            Vector3D(pos.x - hw, pos.y - hh, pos.z - hd),
            Vector3D(pos.x + hw, pos.y - hh, pos.z - hd),
            Vector3D(pos.x + hw, pos.y + hh, pos.z - hd),
            Vector3D(pos.x - hw, pos.y + hh, pos.z - hd),
            Vector3D(pos.x - hw, pos.y - hh, pos.z + hd),
            Vector3D(pos.x + hw, pos.y - hh, pos.z + hd),
            Vector3D(pos.x + hw, pos.y + hh, pos.z + hd),
            Vector3D(pos.x - hw, pos.y + hh, pos.z + hd)
        )

        val projected = vertices.map { simulator.project3DPoint(it, size.width, size.height) }
        val color = parseHexColor(entity.colorHex)

        // Draw 3D Edges
        val edges = listOf(
            0 to 1, 1 to 2, 2 to 3, 3 to 0, // front
            4 to 5, 5 to 6, 6 to 7, 7 to 4, // back
            0 to 4, 1 to 5, 2 to 6, 3 to 7  // connectors
        )

        edges.forEach { (i1, i2) ->
            val p1 = projected.getOrNull(i1)
            val p2 = projected.getOrNull(i2)
            if (p1 != null && p2 != null) {
                drawLine(
                    color = color,
                    start = p1,
                    end = p2,
                    strokeWidth = if (entity.id == selectedEntity?.id) 3.5f else 2f
                )
            }
        }

        // Draw Center Point & Gizmo
        val centerProj = simulator.project3DPoint(pos, size.width, size.height)
        if (centerProj != null && entity.id == selectedEntity?.id) {
            drawCircle(PlasmaCyan, radius = 6f, center = centerProj)
            val pX = simulator.project3DPoint(Vector3D(pos.x + 1.2f, pos.y, pos.z), size.width, size.height)
            val pY = simulator.project3DPoint(Vector3D(pos.x, pos.y + 1.2f, pos.z), size.width, size.height)
            val pZ = simulator.project3DPoint(Vector3D(pos.x, pos.y, pos.z + 1.2f), size.width, size.height)

            if (pX != null) drawLine(Color.Red, centerProj, pX, 3f)
            if (pY != null) drawLine(MatrixGreen, centerProj, pY, 3f)
            if (pZ != null) drawLine(PlasmaCyan, centerProj, pZ, 3f)
        }
    }
}

@Composable
private fun PlayModeVirtualControls(
    simulator: GameEngineSimulator,
    modifier: Modifier = Modifier
) {
    var knobOffset by remember { mutableStateOf(Offset.Zero) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        // Virtual D-Pad / Analog Joystick
        Box(
            modifier = Modifier
                .size(130.dp)
                .background(SurfaceCard.copy(alpha = 0.75f), CircleShape)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            knobOffset = Offset.Zero
                            simulator.inputJoystickX = 0f
                            simulator.inputJoystickY = 0f
                        },
                        onDragCancel = {
                            knobOffset = Offset.Zero
                            simulator.inputJoystickX = 0f
                            simulator.inputJoystickY = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        val newOffset = knobOffset + dragAmount
                        val maxRadius = 45f
                        val dist = sqrt(newOffset.x.pow(2) + newOffset.y.pow(2))
                        knobOffset = if (dist > maxRadius) {
                            Offset(newOffset.x / dist * maxRadius, newOffset.y / dist * maxRadius)
                        } else {
                            newOffset
                        }
                        simulator.inputJoystickX = (knobOffset.x / maxRadius).coerceIn(-1f, 1f)
                        simulator.inputJoystickY = (-knobOffset.y / maxRadius).coerceIn(-1f, 1f)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Inner Handle
            Box(
                modifier = Modifier
                    .offset { IntOffset(knobOffset.x.roundToInt(), knobOffset.y.roundToInt()) }
                    .size(54.dp)
                    .background(FlameOrange, CircleShape)
            )
        }

        // Action Buttons (Jump / Shoot)
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Action Button
            IconButton(
                onClick = {
                    simulator.inputActionPressed = true
                },
                modifier = Modifier
                    .size(60.dp)
                    .background(PlasmaCyan, CircleShape)
                    .testTag("action_button")
            ) {
                Icon(Icons.Default.FlashOn, contentDescription = "Action", tint = CanvasDark)
            }

            // Jump Button
            IconButton(
                onClick = {
                    simulator.inputJumpPressed = true
                },
                modifier = Modifier
                    .size(72.dp)
                    .background(FlameAmber, CircleShape)
                    .testTag("jump_button")
            ) {
                Icon(Icons.Default.ArrowUpward, contentDescription = "Jump", tint = CanvasDark, modifier = Modifier.size(32.dp))
            }
        }
    }
}

fun parseHexColor(hex: String): Color {
    return try {
        Color(AndroidColor.parseColor(hex))
    } catch (e: Exception) {
        FlameOrange
    }
}
