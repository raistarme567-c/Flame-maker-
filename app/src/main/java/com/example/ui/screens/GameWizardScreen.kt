package com.example.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EngineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameWizardScreen(
    viewModel: EngineViewModel,
    onNavigateBack: () -> Unit,
    onProjectCreated: (GameProject) -> Unit
) {
    var step by remember { mutableStateOf(1) }

    var gameTitle by remember { mutableStateOf("Neon Cyber Run") }
    var selectedGenre by remember { mutableStateOf("2D Platformer") }
    var selectedDimension by remember { mutableStateOf(GameDimension.TWO_D) }
    var selectedOrientation by remember { mutableStateOf(GameOrientation.LANDSCAPE) }
    var cameraMode by remember { mutableStateOf("Side-Scroll Follow") }
    var controlScheme by remember { mutableStateOf("Virtual Analog + Jump") }
    var environmentTheme by remember { mutableStateOf("Cyberpunk Neon City") }

    Scaffold(
        containerColor = CanvasDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Guided Game Wizard",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Step $step of 3",
                            color = FlameOrange,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        bottomBar = {
            Surface(color = SurfaceDark, modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 1) {
                        OutlinedButton(onClick = { step-- }) {
                            Text("Previous", color = TextPrimary)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = {
                            if (step < 3) {
                                step++
                            } else {
                                // Generate Starter Project
                                val created = viewModel.createNewProject(
                                    name = gameTitle.ifBlank { "Neon Cyber Run" },
                                    genre = selectedGenre,
                                    dimension = selectedDimension,
                                    orientation = selectedOrientation
                                )
                                onProjectCreated(created)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        modifier = Modifier.testTag("wizard_next_button")
                    ) {
                        Text(
                            text = if (step == 3) "Generate Game" else "Next Step",
                            color = CanvasDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (step) {
                1 -> {
                    item {
                        Text(
                            text = "1. Game Concept & Dimensions",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = gameTitle,
                            onValueChange = { gameTitle = it },
                            label = { Text("Game Title") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = FlameOrange,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Text("Select Game Engine Dimension", color = TextSecondary, fontSize = 13.sp)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            WizardOptionCard(
                                title = "2D Engine",
                                subtitle = "Platformers, Top-down RPGs, Pixel Art",
                                isSelected = selectedDimension == GameDimension.TWO_D,
                                icon = Icons.Default.Gamepad,
                                onClick = {
                                    selectedDimension = GameDimension.TWO_D
                                    selectedGenre = "2D Platformer"
                                },
                                modifier = Modifier.weight(1f)
                            )
                            WizardOptionCard(
                                title = "3D Engine",
                                subtitle = "3D FPS, Third-Person, Racing, Arenas",
                                isSelected = selectedDimension == GameDimension.THREE_D,
                                icon = Icons.Default.ViewInAr,
                                onClick = {
                                    selectedDimension = GameDimension.THREE_D
                                    selectedGenre = "3D FPS Arena"
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                2 -> {
                    item {
                        Text(
                            text = "2. Controls & Camera Setup",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("Screen Orientation", color = TextSecondary, fontSize = 13.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedOrientation == GameOrientation.LANDSCAPE,
                                onClick = { selectedOrientation = GameOrientation.LANDSCAPE },
                                label = { Text("Landscape (Standard)") }
                            )
                            FilterChip(
                                selected = selectedOrientation == GameOrientation.PORTRAIT,
                                onClick = { selectedOrientation = GameOrientation.PORTRAIT },
                                label = { Text("Portrait (Mobile Arcade)") }
                            )
                        }
                    }

                    item {
                        Text("Mobile Control Scheme", color = TextSecondary, fontSize = 13.sp)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "Virtual Analog + Jump" to "Dual-touch joystick on left, jump button on right",
                                "Touch-to-Move / Tap" to "Direct tap navigation on screen",
                                "Twin-Stick Shooter" to "Left stick for movement, right stick for 360 aim"
                            ).forEach { (scheme, desc) ->
                                WizardSelectionRow(
                                    title = scheme,
                                    description = desc,
                                    isSelected = controlScheme == scheme,
                                    onClick = { controlScheme = scheme }
                                )
                            }
                        }
                    }
                }

                3 -> {
                    item {
                        Text(
                            text = "3. Starter Assets & Environment Theme",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                "Cyberpunk Neon City" to "Dark asphalt, neon holographic lights, futuristic droids",
                                "Sci-Fi Space Station" to "Metal corridors, laser turrets, cosmic starfield",
                                "Retro 8-Bit Pixel Realm" to "Classic grassy hills, gold coins, pixel monsters",
                                "Minimalist Low-Poly Arena" to "Clean geometric shapes, high contrast lighting"
                            ).forEach { (theme, desc) ->
                                WizardSelectionRow(
                                    title = theme,
                                    description = desc,
                                    isSelected = environmentTheme == theme,
                                    onClick = { environmentTheme = theme }
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
private fun WizardOptionCard(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceCardLight else SurfaceCard
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, FlameOrange) else null,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(icon, contentDescription = null, tint = if (isSelected) FlameOrange else TextSecondary)
            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextMuted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun WizardSelectionRow(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SurfaceCardLight else SurfaceCard
        ),
        shape = RoundedCornerShape(10.dp),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, PlasmaCyan) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(description, color = TextSecondary, fontSize = 11.sp)
            }
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = FlameOrange)
            )
        }
    }
}
