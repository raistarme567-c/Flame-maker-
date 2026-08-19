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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TerrainBrushMode
import com.example.model.TerrainConfig
import com.example.model.WaterSystemConfig
import com.example.ui.theme.*

@Composable
fun TerrainWaterPanel(
    terrainConfig: TerrainConfig,
    waterConfig: WaterSystemConfig,
    onUpdateTerrain: (TerrainConfig) -> Unit,
    onUpdateWater: (WaterSystemConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeSubTab by remember { mutableStateOf("Terrain") } // Terrain or Water

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Sub-tabs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = activeSubTab == "Terrain",
                onClick = { activeSubTab = "Terrain" },
                label = { Text("3D Terrain Sculptor") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
            )
            FilterChip(
                selected = activeSubTab == "Water",
                onClick = { activeSubTab = "Water" },
                label = { Text("Dynamic Water & Ocean") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
            )
        }

        if (activeSubTab == "Terrain") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Brush Mode
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Sculpting & Painting Brushes", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TerrainBrushMode.values().take(3).forEach { mode ->
                                    FilterChip(
                                        selected = terrainConfig.brushMode == mode,
                                        onClick = {
                                            terrainConfig.brushMode = mode
                                            onUpdateTerrain(terrainConfig)
                                        },
                                        label = { Text(mode.name, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Brush Settings
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Brush Radius & Height Scale", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                            Text("Radius: ${String.format("%.1f", terrainConfig.brushRadius)}m", color = TextSecondary, fontSize = 12.sp)
                            Slider(
                                value = terrainConfig.brushRadius,
                                onValueChange = {
                                    terrainConfig.brushRadius = it
                                    onUpdateTerrain(terrainConfig)
                                },
                                valueRange = 0.5f..10.0f,
                                colors = SliderDefaults.colors(thumbColor = FlameOrange, activeTrackColor = FlameOrange)
                            )

                            Text("Foliage & Tree Density: ${(terrainConfig.foliageDensity * 100).toInt()}%", color = TextSecondary, fontSize = 12.sp)
                            Slider(
                                value = terrainConfig.foliageDensity,
                                onValueChange = {
                                    terrainConfig.foliageDensity = it
                                    onUpdateTerrain(terrainConfig)
                                },
                                valueRange = 0.0f..1.0f,
                                colors = SliderDefaults.colors(thumbColor = MatrixGreen, activeTrackColor = MatrixGreen)
                            )
                        }
                    }
                }
            }
        } else {
            // Water settings
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dynamic Water Simulation", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Switch(
                                    checked = waterConfig.isWaterEnabled,
                                    onCheckedChange = {
                                        waterConfig.isWaterEnabled = it
                                        onUpdateWater(waterConfig)
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = PlasmaCyan)
                                )
                            }

                            Text("Wave Speed (${String.format("%.1f", waterConfig.waveSpeed)}x)", color = TextSecondary, fontSize = 12.sp)
                            Slider(
                                value = waterConfig.waveSpeed,
                                onValueChange = {
                                    waterConfig.waveSpeed = it
                                    onUpdateWater(waterConfig)
                                },
                                valueRange = 0.2f..4.0f,
                                colors = SliderDefaults.colors(thumbColor = PlasmaCyan, activeTrackColor = PlasmaCyan)
                            )

                            Text("Wave Height (${String.format("%.2f", waterConfig.waveHeight)}m)", color = TextSecondary, fontSize = 12.sp)
                            Slider(
                                value = waterConfig.waveHeight,
                                onValueChange = {
                                    waterConfig.waveHeight = it
                                    onUpdateWater(waterConfig)
                                },
                                valueRange = 0.05f..1.5f,
                                colors = SliderDefaults.colors(thumbColor = FlameAmber, activeTrackColor = FlameAmber)
                            )
                        }
                    }
                }
            }
        }
    }
}
