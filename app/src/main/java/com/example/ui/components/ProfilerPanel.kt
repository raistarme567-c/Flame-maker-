package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.EngineStats
import com.example.ui.theme.*

@Composable
fun ProfilerPanel(
    stats: EngineStats,
    currentOptimizationLevel: String,
    onSelectOptimization: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Live Performance Metrics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Real-Time Engine Profiler",
                            color = FlameOrange,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Badge(containerColor = MatrixGreen.copy(alpha = 0.2f)) {
                            Text(text = "Target: 60 FPS", color = MatrixGreen, fontSize = 11.sp)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMetricCard("FPS", "${stats.fps}", if (stats.fps >= 55) MatrixGreen else FlameOrange, Modifier.weight(1f))
                        StatMetricCard("Frame Time", "${String.format("%.1f", stats.frameTimeMs)} ms", PlasmaCyan, Modifier.weight(1f))
                        StatMetricCard("Draw Calls", "${stats.drawCalls}", FlameYellow, Modifier.weight(1f))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatMetricCard("Vertices", "${stats.vertexCount}", TextPrimary, Modifier.weight(1f))
                        StatMetricCard("Physics Bodies", "${stats.activeBodies}", SoftPink, Modifier.weight(1f))
                        StatMetricCard("RAM Usage", "${stats.memoryMb} MB", NeonPurple, Modifier.weight(1f))
                    }
                }
            }
        }

        // 2. Low-End Device Mobile Optimization Profiles
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Mobile Hardware Optimization Profile",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Automatically scales texture compression, vertex batching, shadow passes, and physics tick rate for low-end Android devices.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    val profiles = listOf("Ultra Low", "Low", "Medium", "High", "Ultra")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        profiles.forEach { profile ->
                            FilterChip(
                                selected = currentOptimizationLevel == profile,
                                onClick = { onSelectOptimization(profile) },
                                label = { Text(profile, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FlameOrange,
                                    selectedLabelColor = CanvasDark,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // 3. Rendering Pipeline Diagnostics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Graphics Pipeline Architecture",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    DiagnosticRow("Graphics API", "Vulkan 1.3 / OpenGL ES 3.2 Mobile", MatrixGreen)
                    DiagnosticRow("Renderer", "Mobile-Optimized Forward+ Dynamic Clustered", PlasmaCyan)
                    DiagnosticRow("Batching Mode", "GPU Instancing & Static Mesh Batching", TextPrimary)
                    DiagnosticRow("Occlusion Culling", "Active (Frustum + Portal Buffer)", MatrixGreen)
                    DiagnosticRow("Shader Pipeline", "SPIR-V / ESSL 300 compiled bytecode", TextPrimary)
                }
            }
        }
    }
}

@Composable
private fun StatMetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCardLight),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
private fun DiagnosticRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
