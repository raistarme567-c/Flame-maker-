package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PbrMaterial
import com.example.ui.theme.*

@Composable
fun MaterialEditorPanel(
    material: PbrMaterial,
    onUpdateMaterial: (PbrMaterial) -> Unit,
    modifier: Modifier = Modifier
) {
    var previewShape by remember { mutableStateOf("Sphere") } // Sphere or Cube

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Real-time Material Shader Preview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("PBR Material Preview", color = FlameOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            FilterChip(
                                selected = previewShape == "Sphere",
                                onClick = { previewShape = "Sphere" },
                                label = { Text("Sphere", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = previewShape == "Cube",
                                onClick = { previewShape = "Cube" },
                                label = { Text("Cube", fontSize = 11.sp) }
                            )
                        }
                    }

                    // PBR Render Simulator Canvas
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(if (previewShape == "Sphere") CircleShape else RoundedCornerShape(12.dp))
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        parseHexColor(material.baseColorHex),
                                        parseHexColor(material.baseColorHex).copy(alpha = 0.8f),
                                        Color(0xFF0A0D14)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (material.emissionIntensity > 0f) {
                            Box(
                                modifier = Modifier
                                    .size(70.dp)
                                    .background(
                                        parseHexColor(material.emissionHex).copy(alpha = (material.emissionIntensity / 5f).coerceIn(0.1f, 0.9f)),
                                        CircleShape
                                    )
                            )
                        }
                    }

                    Text(
                        text = "Metallic: ${String.format("%.2f", material.metallic)} • Roughness: ${String.format("%.2f", material.roughness)}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // 2. Base Color & Emission
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Base Albedo & Swatches", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("#FF5722", "#00E5FF", "#7C4DFF", "#00E676", "#FFD700", "#E63946", "#FFFFFF", "#1E293B").forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .clickable {
                                        material.baseColorHex = hex
                                        onUpdateMaterial(material)
                                    }
                            )
                        }
                    }

                    Text("Emission Glow", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Slider(
                            value = material.emissionIntensity,
                            onValueChange = {
                                material.emissionIntensity = it
                                onUpdateMaterial(material)
                            },
                            valueRange = 0f..5f,
                            colors = SliderDefaults.colors(thumbColor = FlameAmber, activeTrackColor = FlameOrange),
                            modifier = Modifier.weight(1f)
                        )
                        Text("${String.format("%.1f", material.emissionIntensity)}x", color = PlasmaCyan, fontSize = 12.sp)
                    }
                }
            }
        }

        // 3. Metallic & Roughness Sliders
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Surface Reflectance & Roughness", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Text("Metallic Map", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = material.metallic,
                        onValueChange = {
                            material.metallic = it
                            onUpdateMaterial(material)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(thumbColor = PlasmaCyan, activeTrackColor = PlasmaCyan)
                    )

                    Text("Roughness Map", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = material.roughness,
                        onValueChange = {
                            material.roughness = it
                            onUpdateMaterial(material)
                        },
                        valueRange = 0f..1f,
                        colors = SliderDefaults.colors(thumbColor = MatrixGreen, activeTrackColor = MatrixGreen)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tangent Normal Map", color = TextPrimary, fontSize = 12.sp)
                        Switch(
                            checked = material.normalMapEnabled,
                            onCheckedChange = {
                                material.normalMapEnabled = it
                                onUpdateMaterial(material)
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = FlameOrange)
                        )
                    }
                }
            }
        }
    }
}
