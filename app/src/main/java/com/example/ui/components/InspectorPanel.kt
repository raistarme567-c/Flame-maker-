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
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun InspectorPanel(
    entity: GameObject?,
    onUpdateEntity: ((GameObject) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    if (entity == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(SurfaceDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Select an Entity from Hierarchy or Viewport to inspect",
                color = TextMuted,
                fontSize = 13.sp
            )
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Entity Header & Tag
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Inspector: ${entity.name}",
                            color = FlameOrange,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Badge(containerColor = PlasmaCyan.copy(alpha = 0.2f)) {
                            Text(text = entity.tag, color = PlasmaCyan, fontSize = 11.sp)
                        }
                    }

                    OutlinedTextField(
                        value = entity.name,
                        onValueChange = { newName ->
                            onUpdateEntity { it.name = newName }
                        },
                        label = { Text("Entity Name", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // 2. Transform Component
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Transform",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Position X, Y, Z
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CoordinateInput(
                            label = "Pos X",
                            value = entity.position.x,
                            color = Color.Red,
                            onValueChange = { v -> onUpdateEntity { it.position.x = v } },
                            modifier = Modifier.weight(1f)
                        )
                        CoordinateInput(
                            label = "Pos Y",
                            value = entity.position.y,
                            color = MatrixGreen,
                            onValueChange = { v -> onUpdateEntity { it.position.y = v } },
                            modifier = Modifier.weight(1f)
                        )
                        CoordinateInput(
                            label = "Pos Z",
                            value = entity.position.z,
                            color = PlasmaCyan,
                            onValueChange = { v -> onUpdateEntity { it.position.z = v } },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Scale X, Y, Z
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CoordinateInput(
                            label = "Scale X",
                            value = entity.scale.x,
                            color = TextSecondary,
                            onValueChange = { v -> onUpdateEntity { it.scale.x = v } },
                            modifier = Modifier.weight(1f)
                        )
                        CoordinateInput(
                            label = "Scale Y",
                            value = entity.scale.y,
                            color = TextSecondary,
                            onValueChange = { v -> onUpdateEntity { it.scale.y = v } },
                            modifier = Modifier.weight(1f)
                        )
                        CoordinateInput(
                            label = "Scale Z",
                            value = entity.scale.z,
                            color = TextSecondary,
                            onValueChange = { v -> onUpdateEntity { it.scale.z = v } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 3. Mesh & Material Color
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Mesh & Material",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    // Shape selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MeshShape.values().take(4).forEach { shape ->
                            FilterChip(
                                selected = entity.meshShape == shape,
                                onClick = { onUpdateEntity { it.meshShape = shape } },
                                label = { Text(shape.name, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FlameOrange,
                                    selectedLabelColor = CanvasDark,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    // Palette Swatches
                    Text(text = "Color Palette", color = TextSecondary, fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val colors = listOf("#FF5722", "#00E5FF", "#7C4DFF", "#00E676", "#FFD700", "#F44336", "#37474F")
                        colors.forEach { hex ->
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(parseHexColor(hex))
                                    .clickable { onUpdateEntity { it.colorHex = hex } }
                            )
                        }
                    }
                }
            }
        }

        // 4. Rigidbody Physics
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Rigidbody Physics",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Switch(
                            checked = entity.hasPhysics,
                            onCheckedChange = { checked -> onUpdateEntity { it.hasPhysics = checked } },
                            colors = SwitchDefaults.colors(checkedThumbColor = FlameOrange)
                        )
                    }

                    if (entity.hasPhysics) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CoordinateInput(
                                label = "Mass",
                                value = entity.mass,
                                color = TextSecondary,
                                onValueChange = { v -> onUpdateEntity { it.mass = v } },
                                modifier = Modifier.weight(1f)
                            )
                            CoordinateInput(
                                label = "Bounce",
                                value = entity.bounciness,
                                color = TextSecondary,
                                onValueChange = { v -> onUpdateEntity { it.bounciness = v } },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = entity.useGravity,
                                onCheckedChange = { chk -> onUpdateEntity { it.useGravity = chk } }
                            )
                            Text(text = "Enable Gravity", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 5. Character & AI Controller
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Character & AI Logic",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CoordinateInput(
                            label = "Speed",
                            value = entity.moveSpeed,
                            color = TextSecondary,
                            onValueChange = { v -> onUpdateEntity { it.moveSpeed = v } },
                            modifier = Modifier.weight(1f)
                        )
                        CoordinateInput(
                            label = "Jump Force",
                            value = entity.jumpForce,
                            color = TextSecondary,
                            onValueChange = { v -> onUpdateEntity { it.jumpForce = v } },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (entity.isAI || entity.tag == "Enemy") {
                        Text(text = "AI Routine: ${entity.aiBehavior}", color = PlasmaCyan, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Patrol", "Chase", "Flee").forEach { routine ->
                                FilterChip(
                                    selected = entity.aiBehavior == routine,
                                    onClick = { onUpdateEntity { it.aiBehavior = routine } },
                                    label = { Text(routine, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CoordinateInput(
    label: String,
    value: Float,
    color: Color,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) { mutableStateOf(String.format("%.2f", value)) }

    Column(modifier = modifier) {
        Text(text = label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = textValue,
            onValueChange = {
                textValue = it
                it.toFloatOrNull()?.let(onValueChange)
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = color,
                unfocusedBorderColor = BorderSubtle
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
