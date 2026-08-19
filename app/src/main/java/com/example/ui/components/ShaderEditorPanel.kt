package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.roundToInt

@Composable
fun ShaderEditorPanel(
    shaderGraph: ShaderGraph,
    onAddNode: (ShaderNode) -> Unit,
    onDeleteNode: (String) -> Unit,
    onConnect: (String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNodeMenu by remember { mutableStateOf(false) }
    var selectedNodeId by remember { mutableStateOf<String?>(null) }
    var panOffset by remember { mutableStateOf(Offset(30f, 30f)) }
    var compiledCodePreview by remember { mutableStateOf(false) }

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
        // Wire connections
        Canvas(modifier = Modifier.fillMaxSize()) {
            shaderGraph.connections.forEach { conn ->
                val fromNode = shaderGraph.nodes.find { it.id == conn.fromNodeId }
                val toNode = shaderGraph.nodes.find { it.id == conn.toNodeId }

                if (fromNode != null && toNode != null) {
                    val startX = fromNode.x + panOffset.x + 180f
                    val startY = fromNode.y + panOffset.y + 40f
                    val endX = toNode.x + panOffset.x
                    val endY = toNode.y + panOffset.y + 40f

                    val path = Path().apply {
                        moveTo(startX, startY)
                        val dist = (endX - startX).coerceAtLeast(40f) / 2f
                        cubicTo(startX + dist, startY, endX - dist, endY, endX, endY)
                    }

                    drawPath(path, color = NeonPurple, style = Stroke(width = 3.5f))
                }
            }
        }

        // Draggable Shader Nodes
        shaderGraph.nodes.forEach { node ->
            key(node.id) {
                ShaderNodeCard(
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

        // Top Controls
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showNodeMenu = true },
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple),
                modifier = Modifier.testTag("add_shader_node_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Shader Node", color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { compiledCodePreview = !compiledCodePreview },
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard)
            ) {
                Icon(Icons.Default.Code, contentDescription = null, tint = PlasmaCyan)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Compiled SPIR-V/GLSL", color = PlasmaCyan)
            }

            if (selectedNodeId != null) {
                IconButton(
                    onClick = {
                        selectedNodeId?.let { onDeleteNode(it) }
                        selectedNodeId = null
                    },
                    modifier = Modifier.background(FlameRed, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextPrimary)
                }
            }
        }

        // Compiled Code Sheet
        if (compiledCodePreview) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceDark.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Generated PBR Fragment Shader (Vulkan/OpenGL ES 3.2)", color = MatrixGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { compiledCodePreview = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                    Text(
                        text = """
                        #version 320 es
                        precision highp float;
                        layout(location = 0) out vec4 outColor;
                        in vec3 vNormal; in vec2 vUV;
                        uniform sampler2D uAlbedo;
                        void main() {
                            vec4 albedo = texture(uAlbedo, vUV);
                            vec3 N = normalize(vNormal);
                            float NdotL = max(dot(N, vec3(0.5, 1.0, 0.3)), 0.0);
                            vec3 pbrLighting = albedo.rgb * (NdotL + 0.15);
                            outColor = vec4(pbrLighting, 1.0);
                        }
                        """.trimIndent(),
                        fontFamily = FontFamily.Monospace,
                        color = MatrixGreen,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Add Node Dropdown Modal
        if (showNodeMenu) {
            AlertDialog(
                onDismissRequest = { showNodeMenu = false },
                title = { Text("Shader Node Palette", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ShaderPaletteButton("PBR Master Output", ShaderNodeType.PBR_MASTER, NeonPurple) {
                            onAddNode(ShaderNode(title = "PBR Master Output", type = ShaderNodeType.PBR_MASTER, x = 450f, y = 120f, inputs = listOf("Albedo", "Metallic", "Roughness", "Normal", "Emission", "Alpha")))
                            showNodeMenu = false
                        }
                        ShaderPaletteButton("Texture 2D Sample", ShaderNodeType.TEXTURE_SAMPLE, PlasmaCyan) {
                            onAddNode(ShaderNode(title = "Texture Sample", type = ShaderNodeType.TEXTURE_SAMPLE, x = 120f, y = 80f, inputs = listOf("UV"), outputs = listOf("RGBA", "R", "G", "B", "A"), value = "hero_diffuse.png"))
                            showNodeMenu = false
                        }
                        ShaderPaletteButton("Fresnel Glow Effect", ShaderNodeType.FRESNEL, FlameOrange) {
                            onAddNode(ShaderNode(title = "Fresnel Glow", type = ShaderNodeType.FRESNEL, x = 120f, y = 220f, inputs = listOf("Exponent"), outputs = listOf("Out"), value = "Power: 3.0"))
                            showNodeMenu = false
                        }
                        ShaderPaletteButton("Voronoi Procedural Noise", ShaderNodeType.VORONOI, FlameYellow) {
                            onAddNode(ShaderNode(title = "Voronoi Noise", type = ShaderNodeType.VORONOI, x = 120f, y = 340f, inputs = listOf("UV", "Scale"), outputs = listOf("Distance", "Cells"), value = "Scale: 8.0"))
                            showNodeMenu = false
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showNodeMenu = false }) {
                        Text("Done", color = PlasmaCyan)
                    }
                },
                containerColor = SurfaceCard
            )
        }
    }
}

@Composable
private fun ShaderNodeCard(
    node: ShaderNode,
    panOffset: Offset,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onDelete: () -> Unit
) {
    val headerColor = when (node.type) {
        ShaderNodeType.PBR_MASTER -> NeonPurple
        ShaderNodeType.TEXTURE_SAMPLE -> PlasmaCyan
        ShaderNodeType.FRESNEL -> FlameOrange
        ShaderNodeType.VORONOI -> FlameYellow
        else -> MatrixGreen
    }

    Box(
        modifier = Modifier
            .offset { IntOffset((node.x + panOffset.x).roundToInt(), (node.y + panOffset.y).roundToInt()) }
            .width(180.dp)
            .pointerInput(node.id) {
                detectDragGestures(onDragStart = { onSelect() }) { change, dragAmount ->
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(headerColor, RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(node.title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.AutoFixHigh, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(12.dp))
            }

            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (node.value.isNotEmpty()) {
                    Text(node.value, color = TextSecondary, fontSize = 10.sp)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        node.inputs.forEach { port ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(MatrixGreen, CircleShape))
                                Text(port, color = TextMuted, fontSize = 9.sp)
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        node.outputs.forEach { port ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(port, color = TextMuted, fontSize = 9.sp)
                                Box(modifier = Modifier.size(6.dp).background(NeonPurple, CircleShape))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShaderPaletteButton(title: String, type: ShaderNodeType, color: Color, onClick: () -> Unit) {
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
            Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
        }
    }
}
