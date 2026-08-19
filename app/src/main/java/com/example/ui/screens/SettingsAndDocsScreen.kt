package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.EngineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAndDocsScreen(
    viewModel: EngineViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedSection by remember { mutableStateOf("Manifesto") }

    Scaffold(
        containerColor = CanvasDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Engine Docs & Architecture",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Section Switcher
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedSection == "Manifesto",
                        onClick = { selectedSection = "Manifesto" },
                        label = { Text("Zero-Cost Model") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                    )
                    FilterChip(
                        selected = selectedSection == "Docs",
                        onClick = { selectedSection = "Docs" },
                        label = { Text("API Reference") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                    )
                    FilterChip(
                        selected = selectedSection == "Storage",
                        onClick = { selectedSection = "Storage" },
                        label = { Text("Storage & Cache") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                    )
                    FilterChip(
                        selected = selectedSection == "AdMob",
                        onClick = { selectedSection = "AdMob" },
                        label = { Text("AdMob Ads & Earning") },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                    )
                }
            }

            when (selectedSection) {
                "AdMob" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MatrixGreen)
                                    Text("Google AdMob Integration Guide", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    "Haan! Aap apne Android app aur banaye hue games me Google AdMob Ads laga kar paise kama sakte hain.",
                                    color = TextSecondary,
                                    fontSize = 13.sp
                                )

                                HorizontalDivider(color = BorderSubtle)

                                Text("1. Kaun-Kaun se Ads Laga Sakte Hain:", color = FlameOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "• Banner Ads: Screen ke bottom ya top par hamesha dikhne wale rectangular ads.\n" +
                                    "• Interstitial Ads: Level complete hone ya game over hone par full-screen popup ads.\n" +
                                    "• Rewarded Video Ads: User video ad dekhkar extra lives, coins, ya premium assets unlock karta hai.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )

                                HorizontalDivider(color = BorderSubtle)

                                Text("2. AdMob Setup Steps:", color = FlameOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    "Step 1: Google AdMob (admob.google.com) par account banayein.\n" +
                                    "Step 2: App add karke apna 'AdMob App ID' (ca-app-pub-...) prapt karein.\n" +
                                    "Step 3: Banner aur Rewarded Ad Unit IDs generate karein.\n" +
                                    "Step 4: AndroidManifest.xml me App ID meta-data aur build.gradle me play-services-ads lagayein.",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    item {
                        // Interactive Ad Simulator Card
                        var rewardedCredits by remember { mutableStateOf(50) }
                        var isShowingRewardedAd by remember { mutableStateOf(false) }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = CanvasDark),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MatrixGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Live Ad Simulator & Preview", color = MatrixGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Badge(containerColor = MatrixGreen.copy(alpha = 0.2f)) {
                                        Text("Credits: $rewardedCredits", color = MatrixGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                // Simulated Banner Ad Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .background(SurfaceDark, RoundedCornerShape(6.dp))
                                        .border(1.dp, FlameOrange.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Badge(containerColor = FlameOrange) {
                                            Text("Ad", color = CanvasDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Text("Google AdMob Banner (320x50 Smart Banner)", color = TextSecondary, fontSize = 11.sp)
                                    }
                                }

                                // Rewarded Ad Trigger
                                Button(
                                    onClick = {
                                        isShowingRewardedAd = true
                                        rewardedCredits += 100
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("simulate_rewarded_ad_button")
                                ) {
                                    Icon(Icons.Default.PlayCircle, contentDescription = null, tint = CanvasDark)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Watch Rewarded Video (+100 Game Credits)", color = CanvasDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                if (isShowingRewardedAd) {
                                    Text(
                                        "Rewarded Video Ad Watched! +100 Credits added to your game account.",
                                        color = MatrixGreen,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
                "Manifesto" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = MatrixGreen)
                                    Text("Zero-Cost Product Model", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    text = "FLAME MAKER is engineered under a strict 100% Free philosophy:\n\n" +
                                            "• No subscriptions or paywalls\n" +
                                            "• No export watermarks or branding on generated APKs\n" +
                                            "• No credit limits or pay-per-build locks\n" +
                                            "• Full access to 2D & 3D renderers, physics simulation, FlameScript JIT, and visual node graphs\n" +
                                            "• Standalone on-device APK generation directly on your Android device.",
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Engine Tagline & Brand", color = FlameOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("FLAME MAKER — “Create. Build. Play.”", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                                Text("Version 1.0.0-Release • Built with Android Jetpack & Room", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }

                "Docs" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("FlameScript API Reference", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)

                                CodeDocBlock(
                                    title = "Component Lifecycle",
                                    code = """
                                    class MyScript : FlameComponent() {
                                        override fun onStart() { /* Init */ }
                                        override fun onUpdate(dt: Float) { /* Frame */ }
                                        override fun onCollision(other: GameObject) { /* Hit */ }
                                    }
                                    """.trimIndent()
                                )

                                CodeDocBlock(
                                    title = "Input Management",
                                    code = """
                                    val joy = Input.getJoystickVector() // x, y (-1..1)
                                    val jump = Input.isButtonDown("Jump") // true/false
                                    val touch = Input.getTouchPosition()
                                    """.trimIndent()
                                )

                                CodeDocBlock(
                                    title = "Physics & Spawning",
                                    code = """
                                    rigidbody.addForce(Vector3(0f, 10f, 0f))
                                    Scene.instantiate("BulletPrefab", position)
                                    Audio.play("sfx_explosion")
                                    """.trimIndent()
                                )
                            }
                        }
                    }
                }

                "Storage" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Storage & Build Cache", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("Generated APKs and compiled game assets are stored locally in sandbox storage.", color = TextSecondary, fontSize = 12.sp)

                                Button(
                                    onClick = { viewModel.clearBuildCache() },
                                    colors = ButtonDefaults.buttonColors(containerColor = FlameRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.CleaningServices, contentDescription = null)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Clear All Generated APKs & Build Cache")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CodeDocBlock(title: String, code: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, color = PlasmaCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CanvasDark, RoundedCornerShape(6.dp))
                .padding(10.dp)
        ) {
            Text(
                text = code,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MatrixGreen
            )
        }
    }
}
