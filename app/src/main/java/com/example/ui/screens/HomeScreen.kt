package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameDimension
import com.example.model.GameOrientation
import com.example.model.GameProject
import com.example.monetization.AdMobBannerView
import com.example.monetization.AdMobInterstitialDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.EngineViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    viewModel: EngineViewModel,
    onOpenProject: (GameProject) -> Unit,
    onOpenWizard: () -> Unit,
    onOpenBuildCenter: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAuth: () -> Unit = {},
    onOpenCommunityFeed: () -> Unit = {}
) {
    val projects by viewModel.allProjects.collectAsState()
    val communityGames by viewModel.communityGames.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val devName by viewModel.developerName.collectAsState()
    var showNewProjectDialog by remember { mutableStateOf(false) }
    var showInterstitialAd by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CanvasDark,
        bottomBar = {
            AdMobBannerView(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showNewProjectDialog = true },
                containerColor = FlameOrange,
                contentColor = CanvasDark,
                modifier = Modifier.testTag("create_project_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Game Project")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Brand Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF2A0808), Color(0xFF161224), Color(0xFF0F1829))
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = null,
                                    tint = FlameOrange,
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    text = "FLAME MAKER",
                                    color = TextPrimary,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Create. Build. Play.",
                                color = FlameAmber,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Professional, 100% free mobile game engine with on-device standalone APK generation.",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Button(
                                    onClick = {
                                        if (com.example.monetization.AdMobConfig.shouldShowInterstitial()) {
                                            showInterstitialAd = true
                                        }
                                        onOpenWizard()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("game_wizard_button")
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CanvasDark, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Game Wizard", color = CanvasDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (com.example.monetization.AdMobConfig.shouldShowInterstitial()) {
                                            showInterstitialAd = true
                                        }
                                        onOpenBuildCenter()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PlasmaCyan),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("build_center_shortcut_button")
                                ) {
                                    Icon(Icons.Default.Android, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("APK Center", fontSize = 12.sp)
                                }
                                Surface(
                                    onClick = onOpenAuth,
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isLoggedIn) MatrixGreen.copy(alpha = 0.2f) else FlameOrange.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isLoggedIn) MatrixGreen else FlameOrange),
                                    modifier = Modifier.testTag("phone_auth_shortcut_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            if (isLoggedIn) Icons.Default.VerifiedUser else Icons.Default.PhoneAndroid,
                                            contentDescription = "Login",
                                            tint = if (isLoggedIn) MatrixGreen else FlameOrange,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (isLoggedIn) "Profile" else "Login",
                                            color = if (isLoggedIn) MatrixGreen else FlameOrange,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Surface(
                                    onClick = { showInterstitialAd = true },
                                    shape = RoundedCornerShape(8.dp),
                                    color = MatrixGreen.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MatrixGreen),
                                    modifier = Modifier.testTag("test_interstitial_ad_button")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.MonetizationOn,
                                            contentDescription = "Ad",
                                            tint = MatrixGreen,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Instant Ad",
                                            color = MatrixGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Community Apps & Games Hub Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, PlasmaCyan.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0F1A2E), Color(0xFF1B1228))
                                )
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = PlasmaCyan, modifier = Modifier.size(24.dp))
                                Column {
                                    Text("Apps & Games Public Hub", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                    Text("${communityGames.size} Playable games published by creators", color = MatrixGreen, fontSize = 11.sp)
                                }
                            }
                            Badge(containerColor = PlasmaCyan) {
                                Text("LIVE FEED", color = CanvasDark, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Text(
                            text = "Scroll through creator games, play directly inside the feed, give real-time likes ❤️, or publish your own game with 1 tap!",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenCommunityFeed,
                                colors = ButtonDefaults.buttonColors(containerColor = PlasmaCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("open_community_hub_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CanvasDark, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Open Games Feed", color = CanvasDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onOpenCommunityFeed,
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameOrange),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).testTag("publish_to_community_button")
                            ) {
                                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publish My Game", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Quick Stats Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricInfoCard(
                        title = "Projects",
                        value = "${projects.size}",
                        icon = Icons.Default.Folder,
                        tint = FlameOrange,
                        modifier = Modifier.weight(1f)
                    )
                    MetricInfoCard(
                        title = "Developer",
                        value = if (isLoggedIn) devName.take(12) else "Guest Dev",
                        icon = Icons.Default.AccountCircle,
                        tint = PlasmaCyan,
                        modifier = Modifier.weight(1f)
                    )
                    MetricInfoCard(
                        title = "Cloud Auth",
                        value = if (isLoggedIn) "OTP Active" else "Tap to Sign In",
                        icon = if (isLoggedIn) Icons.Default.CloudDone else Icons.Default.CloudOff,
                        tint = if (isLoggedIn) MatrixGreen else TextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Projects Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "My Game Projects",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onOpenAuth, modifier = Modifier.testTag("top_auth_button")) {
                            Icon(
                                if (isLoggedIn) Icons.Default.AccountCircle else Icons.Default.PhoneIphone,
                                contentDescription = "Profile",
                                tint = if (isLoggedIn) MatrixGreen else FlameOrange
                            )
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary)
                        }
                    }
                }
            }

            // Projects List
            items(projects, key = { it.id }) { project ->
                ProjectCard(
                    project = project,
                    onOpen = {
                        if (com.example.monetization.AdMobConfig.shouldShowInterstitial()) {
                            showInterstitialAd = true
                        }
                        viewModel.openProject(project)
                        onOpenProject(project)
                    },
                    onDelete = { viewModel.deleteProject(project.id) }
                )
            }
        }
    }

    // New Project Dialog
    if (showNewProjectDialog) {
        var nameInput by remember { mutableStateOf("") }
        var genreInput by remember { mutableStateOf("Platformer") }
        var dimensionInput by remember { mutableStateOf(GameDimension.TWO_D) }
        var orientationInput by remember { mutableStateOf(GameOrientation.LANDSCAPE) }

        AlertDialog(
            onDismissRequest = { showNewProjectDialog = false },
            title = { Text("Create New Game Project", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Game Title") },
                        placeholder = { Text("e.g. Cyber Runner 2099") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("new_project_name_input")
                    )

                    Text("Engine Dimension", color = TextSecondary, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = dimensionInput == GameDimension.TWO_D,
                            onClick = { dimensionInput = GameDimension.TWO_D },
                            label = { Text("2D Engine") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                        )
                        FilterChip(
                            selected = dimensionInput == GameDimension.THREE_D,
                            onClick = { dimensionInput = GameDimension.THREE_D },
                            label = { Text("3D Engine") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                        )
                    }

                    Text("Genre Preset", color = TextSecondary, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("Platformer", "FPS", "RPG", "Space Shooter").forEach { g ->
                            FilterChip(
                                selected = genreInput == g,
                                onClick = { genreInput = g },
                                label = { Text(g, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalTitle = nameInput.ifBlank { "My Game Project" }
                        val created = viewModel.createNewProject(finalTitle, genreInput, dimensionInput, orientationInput)
                        showNewProjectDialog = false
                        onOpenProject(created)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    modifier = Modifier.testTag("create_project_confirm_button")
                ) {
                    Text("Create Game", color = CanvasDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewProjectDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }

    if (showInterstitialAd) {
        AdMobInterstitialDialog(
            onDismiss = { showInterstitialAd = false }
        )
    }
}

@Composable
private fun ProjectCard(
    project: GameProject,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(project.lastModified))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("project_card_${project.name}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (project.dimension == GameDimension.TWO_D) FlameOrange else PlasmaCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (project.dimension == GameDimension.TWO_D) Icons.Default.Gamepad else Icons.Default.ViewInAr,
                        contentDescription = null,
                        tint = CanvasDark,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = project.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${project.genre} • ${project.dimension.name} • $dateStr",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = project.packageName,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(20.dp))
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "Open", tint = TextSecondary)
            }
        }
    }
}

@Composable
private fun MetricInfoCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, tint: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(title, color = TextMuted, fontSize = 11.sp)
        }
    }
}
