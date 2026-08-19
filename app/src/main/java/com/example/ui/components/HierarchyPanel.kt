package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.model.GameObject
import com.example.model.MeshShape
import com.example.ui.theme.*

@Composable
fun HierarchyPanel(
    entities: List<GameObject>,
    selectedEntity: GameObject?,
    onSelectEntity: (GameObject) -> Unit,
    onAddEntity: (String, MeshShape, String, Boolean) -> Unit,
    onDuplicateEntity: () -> Unit,
    onDeleteEntity: () -> Unit,
    onToggleVisibility: (GameObject) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(12.dp)
    ) {
        // Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Scene Hierarchy (${entities.size})",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = { showAddMenu = true },
                    modifier = Modifier.size(36.dp).testTag("add_entity_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Object", tint = FlameOrange)
                }
                IconButton(
                    onClick = onDuplicateEntity,
                    enabled = selectedEntity != null,
                    modifier = Modifier.size(36.dp).testTag("duplicate_entity_button")
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = if (selectedEntity != null) PlasmaCyan else TextMuted)
                }
                IconButton(
                    onClick = onDeleteEntity,
                    enabled = selectedEntity != null,
                    modifier = Modifier.size(36.dp).testTag("delete_entity_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = if (selectedEntity != null) FlameRed else TextMuted)
                }
            }
        }

        DropdownMenu(
            expanded = showAddMenu,
            onDismissRequest = { showAddMenu = false },
            modifier = Modifier.background(SurfaceCard)
        ) {
            DropdownMenuItem(
                text = { Text("3D / 2D Cube", color = TextPrimary) },
                leadingIcon = { Icon(Icons.Default.CropSquare, contentDescription = null, tint = FlameOrange) },
                onClick = {
                    onAddEntity("Cube_${entities.size + 1}", MeshShape.CUBE, "#FF5722", true)
                    showAddMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Sphere / Ball", color = TextPrimary) },
                leadingIcon = { Icon(Icons.Default.Circle, contentDescription = null, tint = PlasmaCyan) },
                onClick = {
                    onAddEntity("Sphere_${entities.size + 1}", MeshShape.SPHERE, "#00E5FF", true)
                    showAddMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Platform / Floor", color = TextPrimary) },
                leadingIcon = { Icon(Icons.Default.HorizontalRule, contentDescription = null, tint = MatrixGreen) },
                onClick = {
                    onAddEntity("Platform_${entities.size + 1}", MeshShape.PLANE, "#37474F", false)
                    showAddMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Enemy Droid (AI)", color = TextPrimary) },
                leadingIcon = { Icon(Icons.Default.SmartToy, contentDescription = null, tint = FlameRed) },
                onClick = {
                    onAddEntity("EnemyDroid", MeshShape.CUBE, "#F44336", true)
                    showAddMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Point Light Source", color = TextPrimary) },
                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, tint = FlameYellow) },
                onClick = {
                    onAddEntity("LightSource", MeshShape.SPHERE, "#FFF9C4", false)
                    showAddMenu = false
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Entity List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(entities, key = { it.id }) { entity ->
                val isSelected = entity.id == selectedEntity?.id

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isSelected) SurfaceCardLight else SurfaceCard,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { onSelectEntity(entity) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tag/Type Icon
                        val icon = when {
                            entity.tag == "Player" -> Icons.Default.DirectionsRun
                            entity.tag == "Enemy" || entity.isAI -> Icons.Default.SmartToy
                            entity.isLight -> Icons.Default.LightMode
                            entity.hasParticles -> Icons.Default.AutoAwesome
                            else -> Icons.Default.Widgets
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) FlameOrange else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )

                        Column {
                            Text(
                                text = entity.name,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = "Tag: ${entity.tag} | (${String.format("%.1f", entity.position.x)}, ${String.format("%.1f", entity.position.y)})",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = { onToggleVisibility(entity) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (entity.isEnabled) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Visibility",
                                tint = if (entity.isEnabled) TextSecondary else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
