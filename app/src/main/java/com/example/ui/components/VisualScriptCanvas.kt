package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun VisualScriptCanvas(
    graph: VisualScriptGraph?,
    onAddNode: (VisualNode) -> Unit,
    onDeleteNode: (String) -> Unit,
    onConnectNodes: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPalette by remember { mutableStateOf(false) }
    var selectedNodeId by remember { mutableStateOf<String?>(null) }
    var panOffset by remember { mutableStateOf(Offset(20f, 20f)) }

    val nodes = graph?.nodes ?: remember { mutableStateListOf() }
    val connections = graph?.connections ?: remember { mutableStateListOf() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    panOffset += dragAmount
                }
            }
    ) {
        // Wire Connections Layer (Bezier Curves)
        Canvas(modifier = Modifier.fillMaxSize()) {
            connections.forEach { conn ->
                val fromNode = nodes.find { it.id == conn.fromNodeId }
                val toNode = nodes.find { it.id == conn.toNodeId }

                if (fromNode != null && toNode != null) {
                    val startX = fromNode.x + panOffset.x + 180f
                    val startY = fromNode.y + panOffset.y + 45f
                    val endX = toNode.x + panOffset.x
                    val endY = toNode.y + panOffset.y + 45f

                    val path = Path().apply {
                        moveTo(startX, startY)
                        val controlDist = (endX - startX).coerceAtLeast(60f) / 2f
                        cubicTo(
                            startX + controlDist, startY,
                            endX - controlDist, endY,
                            endX, endY
                        )
                    }

                    drawPath(
                        path = path,
                        color = PlasmaCyan,
                        style = Stroke(width = 3.5f)
                    )
                }
            }
        }

        // Draggable Nodes
        nodes.forEach { node ->
            key(node.id) {
                VisualNodeCard(
                    node = node,
                    panOffset = panOffset,
                    isSelected = node.id == selectedNodeId,
                    onSelect = { selectedNodeId = node.id },
                    onDrag = { dx, dy ->
                        node.x += dx
                        node.y += dy
                    },
                    onDelete = { onDeleteNode(node.id) }
                )
            }
        }

        // Top Controls & Add Node Button
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showPalette = true },
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                modifier = Modifier.testTag("add_node_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = CanvasDark)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Node", color = CanvasDark, fontWeight = FontWeight.Bold)
            }

            if (selectedNodeId != null) {
                Button(
                    onClick = {
                        selectedNodeId?.let { onDeleteNode(it) }
                        selectedNodeId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameRed)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = TextPrimary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete Node")
                }
            }
        }

        // Add Node Palette Modal
        if (showPalette) {
            AlertDialog(
                onDismissRequest = { showPalette = false },
                title = { Text("Visual Node Library", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Select a visual logic block to insert into graph:", color = TextSecondary, fontSize = 12.sp)

                        NodePaletteItem("On Start (Event)", NodeCategory.EVENT) {
                            onAddNode(VisualNode(title = "On Start", category = NodeCategory.EVENT, x = 50f, y = 50f, outputs = listOf(ScriptPort(name = "Exec", isInput = false))))
                            showPalette = false
                        }
                        NodePaletteItem("On Joystick Movement", NodeCategory.EVENT) {
                            onAddNode(VisualNode(title = "On Joystick", category = NodeCategory.EVENT, x = 50f, y = 160f, outputs = listOf(ScriptPort(name = "Exec", isInput = false), ScriptPort(name = "Vector2", isInput = false, dataType = "vector"))))
                            showPalette = false
                        }
                        NodePaletteItem("Move Entity (Motion)", NodeCategory.MOTION) {
                            onAddNode(VisualNode(title = "Move Entity", category = NodeCategory.MOTION, x = 320f, y = 100f, inputs = listOf(ScriptPort(name = "Exec", isInput = true)), outputs = listOf(ScriptPort(name = "Out", isInput = false)), propertyValue = "Speed: 6.0"))
                            showPalette = false
                        }
                        NodePaletteItem("Apply Jump Force (Physics)", NodeCategory.PHYSICS) {
                            onAddNode(VisualNode(title = "Apply Jump Force", category = NodeCategory.PHYSICS, x = 320f, y = 220f, inputs = listOf(ScriptPort(name = "Exec", isInput = true)), propertyValue = "Force: 12.0"))
                            showPalette = false
                        }
                        NodePaletteItem("Play Audio SFX", NodeCategory.AUDIO) {
                            onAddNode(VisualNode(title = "Play Sound", category = NodeCategory.AUDIO, x = 520f, y = 100f, inputs = listOf(ScriptPort(name = "Play", isInput = true)), propertyValue = "sfx_laser"))
                            showPalette = false
                        }
                        NodePaletteItem("Spawn Prefab / Bullet", NodeCategory.SPAWN) {
                            onAddNode(VisualNode(title = "Spawn Prefab", category = NodeCategory.SPAWN, x = 520f, y = 220f, inputs = listOf(ScriptPort(name = "Spawn", isInput = true)), propertyValue = "EnergyBlast"))
                            showPalette = false
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showPalette = false }) {
                        Text("Close", color = PlasmaCyan)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

@Composable
private fun VisualNodeCard(
    node: VisualNode,
    panOffset: Offset,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    val headerColor = when (node.category) {
        NodeCategory.EVENT -> NodeEventColor
        NodeCategory.MOTION -> NodeMotionColor
        NodeCategory.PHYSICS -> NodePhysicsColor
        NodeCategory.LOGIC -> NodeLogicColor
        NodeCategory.AUDIO -> NodeAudioColor
        NodeCategory.SPAWN -> NodeSpawnColor
        else -> FlameOrange
    }

    Box(
        modifier = Modifier
            .offset { IntOffset((node.x + panOffset.x).roundToInt(), (node.y + panOffset.y).roundToInt()) }
            .width(190.dp)
            .pointerInput(node.id) {
                detectDragGestures(
                    onDragStart = { onSelect() }
                ) { change, dragAmount ->
                    change.consume()
                    onDrag(dragAmount.x, dragAmount.y)
                }
            }
            .background(SurfaceCard, RoundedCornerShape(10.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PlasmaCyan else BorderSubtle,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = node.title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.Extension,
                    contentDescription = null,
                    tint = TextPrimary.copy(alpha = 0.8f),
                    modifier = Modifier.size(14.dp)
                )
            }

            // Body & Ports
            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (node.propertyValue.isNotEmpty()) {
                    Text(
                        text = node.propertyValue,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Input Ports
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        node.inputs.forEach { port ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(MatrixGreen, CircleShape))
                                Text(port.name, color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }

                    // Output Ports
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        node.outputs.forEach { port ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(port.name, color = TextMuted, fontSize = 10.sp)
                                Box(modifier = Modifier.size(8.dp).background(PlasmaCyan, CircleShape))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NodePaletteItem(
    title: String,
    category: NodeCategory,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardLight),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = TextPrimary, fontSize = 13.sp)
            Badge(containerColor = FlameOrange.copy(alpha = 0.3f)) {
                Text(category.name, color = FlameOrange, fontSize = 10.sp)
            }
        }
    }
}
