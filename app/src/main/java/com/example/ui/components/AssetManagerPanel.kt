package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class EnginePlugin(
    val name: String,
    val version: String,
    val description: String,
    val category: String,
    val isEnabled: Boolean = true
)

@Composable
fun AssetManagerPanel(
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Assets") }

    val defaultAssets = remember {
        listOf(
            Triple("hero_cyber_diffuse.png", "TEXTURE", "256 KB"),
            Triple("sci_fi_tileset.png", "TEXTURE", "512 KB"),
            Triple("sfx_laser_blast.wav", "AUDIO", "48 KB"),
            Triple("sfx_jump_booster.wav", "AUDIO", "32 KB"),
            Triple("bgm_cyber_arena.ogg", "AUDIO", "1.2 MB"),
            Triple("character_mesh.obj", "3D MESH", "180 KB"),
            Triple("standard_pbr.shader", "SHADER", "12 KB"),
            Triple("PlayerHero.prefab", "PREFAB", "8 KB")
        )
    }

    val plugins = remember {
        mutableStateListOf(
            EnginePlugin("2D Tilemap Pro", "v1.4.0", "Fast auto-tiling, multi-layer parallax, collision generator", "2D Tools"),
            EnginePlugin("3D Voxel & Mesh Importer", "v2.1.0", "OBJ, GLTF 2.0, MagicaVoxel VOX mesh parser & LOD", "3D Tools"),
            EnginePlugin("Retro Chiptune SFX Synth", "v1.0.0", "Procedural real-time 8-bit/16-bit sound generator", "Audio"),
            EnginePlugin("NavMesh 3D Pathfinding Kit", "v3.0.0", "Dynamic obstacle avoidance and A* nav graph", "AI / Navigation"),
            EnginePlugin("Simple LAN Multiplayer Kit", "v1.2.0", "Peer-to-peer room discovery and state sync", "Networking"),
            EnginePlugin("Post-Processing FX Pipeline", "v2.0.0", "Bloom, Vignette, Color Grading, Scanlines", "VFX")
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tab switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == "Assets",
                onClick = { selectedCategory = "Assets" },
                label = { Text("Project Assets (8)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FlameOrange,
                    selectedLabelColor = CanvasDark,
                    labelColor = TextSecondary
                )
            )
            FilterChip(
                selected = selectedCategory == "Plugins",
                onClick = { selectedCategory = "Plugins" },
                label = { Text("Package & Plugin Manager (6)", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = FlameOrange,
                    selectedLabelColor = CanvasDark,
                    labelColor = TextSecondary
                )
            )
        }

        if (selectedCategory == "Assets") {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(defaultAssets) { (name, type, size) ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                val icon = when (type) {
                                    "TEXTURE" -> Icons.Default.Image
                                    "AUDIO" -> Icons.Default.Audiotrack
                                    "3D MESH" -> Icons.Default.ViewInAr
                                    "SHADER" -> Icons.Default.AutoFixHigh
                                    else -> Icons.Default.Widgets
                                }
                                Icon(icon, contentDescription = null, tint = PlasmaCyan)
                                Column {
                                    Text(name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Text("$type • $size", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                            Badge(containerColor = SurfaceCardLight) {
                                Text("Ready", color = MatrixGreen, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(plugins) { plugin ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(plugin.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(plugin.version, color = PlasmaCyan, fontSize = 10.sp)
                                }
                                Text(plugin.description, color = TextSecondary, fontSize = 11.sp)
                            }
                            Badge(containerColor = MatrixGreen.copy(alpha = 0.2f)) {
                                Text("ACTIVE", color = MatrixGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
