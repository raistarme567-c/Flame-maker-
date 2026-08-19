package com.example.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ParticleEmitterConfig
import com.example.model.ParticlePreset
import com.example.ui.theme.*

@Composable
fun ParticleEditorPanel(
    config: ParticleEmitterConfig,
    onUpdateConfig: (ParticleEmitterConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Presets Selector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("VFX & Particle Presets", color = FlameOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ParticlePreset.values().take(3).forEach { preset ->
                            FilterChip(
                                selected = config.preset == preset,
                                onClick = {
                                    config.preset = preset
                                    when (preset) {
                                        ParticlePreset.FIRE_BLAST -> {
                                            config.colorStartHex = "#FF5722"
                                            config.colorEndHex = "#FFD700"
                                            config.startSpeed = 5.0f
                                        }
                                        ParticlePreset.SMOKE_PLUME -> {
                                            config.colorStartHex = "#94A3B8"
                                            config.colorEndHex = "#334155"
                                            config.startSpeed = 2.0f
                                        }
                                        ParticlePreset.CYBER_SPARKS -> {
                                            config.colorStartHex = "#00E5FF"
                                            config.colorEndHex = "#7C4DFF"
                                            config.startSpeed = 7.0f
                                        }
                                        else -> {}
                                    }
                                    onUpdateConfig(config)
                                },
                                label = { Text(preset.name.replace("_", " "), fontSize = 10.sp) }
                            )
                        }
                    }
                }
            }
        }

        // 2. Emission Rate & Lifetime
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Emitter Dynamics", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Text("Emission Rate (${config.emissionRate} / sec)", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = config.emissionRate.toFloat(),
                        onValueChange = {
                            config.emissionRate = it.toInt()
                            onUpdateConfig(config)
                        },
                        valueRange = 5f..150f,
                        colors = SliderDefaults.colors(thumbColor = FlameOrange, activeTrackColor = FlameOrange)
                    )

                    Text("Particle Lifetime (${String.format("%.1f", config.particleLife)}s)", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = config.particleLife,
                        onValueChange = {
                            config.particleLife = it
                            onUpdateConfig(config)
                        },
                        valueRange = 0.2f..4.0f,
                        colors = SliderDefaults.colors(thumbColor = PlasmaCyan, activeTrackColor = PlasmaCyan)
                    )

                    Text("Start Speed (${String.format("%.1f", config.startSpeed)})", color = TextSecondary, fontSize = 12.sp)
                    Slider(
                        value = config.startSpeed,
                        onValueChange = {
                            config.startSpeed = it
                            onUpdateConfig(config)
                        },
                        valueRange = 1f..15f,
                        colors = SliderDefaults.colors(thumbColor = MatrixGreen, activeTrackColor = MatrixGreen)
                    )
                }
            }
        }

        // 3. Color Gradient
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Particle Color Gradient Over Life", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(parseHexColor(config.colorStartHex)))
                            Text("Start Color", color = TextSecondary, fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("End Color", color = TextSecondary, fontSize = 12.sp)
                            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(parseHexColor(config.colorEndHex)))
                        }
                    }
                }
            }
        }
    }
}
