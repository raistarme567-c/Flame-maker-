package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.EngineViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildExportScreen(
    viewModel: EngineViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val project by viewModel.currentProject.collectAsState()
    val isBuilding by viewModel.isBuilding.collectAsState()
    val buildProgress by viewModel.buildProgress.collectAsState()
    val lastBuildRecord by viewModel.lastBuildRecord.collectAsState()
    val buildHistory by viewModel.allBuildRecords.collectAsState()

    var buildType by remember { mutableStateOf(BuildType.RELEASE) }
    var targetArch by remember { mutableStateOf(TargetArch.UNIVERSAL) }
    var selectedTab by remember { mutableStateOf("Config") } // Config, Logs, History

    // Permissions configuration
    val availablePermissions = remember {
        listOf(
            "android.permission.INTERNET" to "Internet Access (Online features / AI)",
            "android.permission.VIBRATE" to "Haptic Feedback (Controller rumble)",
            "android.permission.CAMERA" to "Camera (AR & Computer Vision)",
            "android.permission.RECORD_AUDIO" to "Microphone (Voice Chat / Audio)",
            "android.permission.ACCESS_FINE_LOCATION" to "Location (GPS Gameplay)"
        )
    }
    val enabledPermissions = remember {
        mutableStateListOf("android.permission.INTERNET", "android.permission.VIBRATE")
    }

    Scaffold(
        containerColor = CanvasDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "BUILD & EXPORT CENTER",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = project?.name ?: "No project selected",
                            color = FlameOrange,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("build_back_button")) {
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // If Build Completed Successfully, Show Result Banner
            if (buildProgress?.isCompleted == true && buildProgress?.isFailed == false && lastBuildRecord != null) {
                item {
                    BuildSuccessBanner(
                        record = lastBuildRecord!!,
                        onInstall = {
                            viewModel.builderEngine.launchInstallApkIntent(File(lastBuildRecord!!.apkFilePath))
                        },
                        onShare = {
                            viewModel.builderEngine.launchShareApkIntent(
                                File(lastBuildRecord!!.apkFilePath),
                                lastBuildRecord!!.gameName
                            )
                        },
                        onBuildAgain = {
                            viewModel.startBuild(buildType, targetArch, enabledPermissions.toList())
                        }
                    )
                }
            }

            // If Build is in Progress
            if (isBuilding) {
                item {
                    BuildProgressCard(buildProgress = buildProgress)
                }
            }

            // Navigation Tab selector
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == "Config",
                        onClick = { selectedTab = "Config" },
                        label = { Text("Build Config") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FlameOrange,
                            selectedLabelColor = CanvasDark
                        )
                    )
                    FilterChip(
                        selected = selectedTab == "Logs",
                        onClick = { selectedTab = "Logs" },
                        label = { Text("Compiler Terminal") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FlameOrange,
                            selectedLabelColor = CanvasDark
                        )
                    )
                    FilterChip(
                        selected = selectedTab == "History",
                        onClick = { selectedTab = "History" },
                        label = { Text("APK History (${buildHistory.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FlameOrange,
                            selectedLabelColor = CanvasDark
                        )
                    )
                }
            }

            when (selectedTab) {
                "Config" -> {
                    // Build Mode (Debug vs Release)
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Build Mode & Architecture", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(
                                        selected = buildType == BuildType.RELEASE,
                                        onClick = { buildType = BuildType.RELEASE },
                                        label = { Text("Release APK (Signed)") },
                                        leadingIcon = { Icon(Icons.Default.Verified, contentDescription = null, tint = MatrixGreen) }
                                    )
                                    FilterChip(
                                        selected = buildType == BuildType.DEBUG,
                                        onClick = { buildType = BuildType.DEBUG },
                                        label = { Text("Debug APK") },
                                        leadingIcon = { Icon(Icons.Default.BugReport, contentDescription = null, tint = FlameYellow) }
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilterChip(
                                        selected = targetArch == TargetArch.UNIVERSAL,
                                        onClick = { targetArch = TargetArch.UNIVERSAL },
                                        label = { Text("Universal (All Devices)") }
                                    )
                                    FilterChip(
                                        selected = targetArch == TargetArch.ARM64,
                                        onClick = { targetArch = TargetArch.ARM64 },
                                        label = { Text("ARM64-v8a (Optimized)") }
                                    )
                                }
                            }
                        }
                    }

                    // Package Identity
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Application Package Identity", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                                InfoField("App Name", project?.name ?: "Game")
                                InfoField("Package Name", project?.packageName ?: "com.flame.game")
                                InfoField("Version Name", "${project?.versionName ?: "1.0.0"} (Code: ${project?.versionCode ?: 1})")
                                InfoField("Orientation", project?.orientation?.name ?: "LANDSCAPE")
                                InfoField("Min / Target SDK", "Min SDK: 24 | Target SDK: 36 (Android 15+)")
                            }
                        }
                    }

                    // Permissions Manager
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Android Permissions Manager", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Flame Maker only injects permissions that you explicitly enable.", color = TextSecondary, fontSize = 11.sp)

                                availablePermissions.forEach { (perm, label) ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                            Text(perm, color = TextMuted, fontSize = 10.sp)
                                        }
                                        Checkbox(
                                            checked = enabledPermissions.contains(perm),
                                            onCheckedChange = { checked ->
                                                if (checked) enabledPermissions.add(perm) else enabledPermissions.remove(perm)
                                            },
                                            colors = CheckboxDefaults.colors(checkedColor = FlameOrange)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // AdMob Monetization Config
                    item {
                        var enableAds by remember { mutableStateOf(true) }
                        var adMobAppId by remember { mutableStateOf(com.example.monetization.AdMobConfig.APP_ID) }
                        var bannerAdId by remember { mutableStateOf(com.example.monetization.AdMobConfig.BANNER_AD_ID) }
                        var interstitialAdId by remember { mutableStateOf(com.example.monetization.AdMobConfig.INTERSTITIAL_AD_ID) }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = MatrixGreen)
                                        Text("AdMob Ads & Earning Setup", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Switch(
                                        checked = enableAds,
                                        onCheckedChange = { enableAds = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = FlameOrange)
                                    )
                                }

                                if (enableAds) {
                                    Text(
                                        "Enable Google AdMob in this APK to monetize with Banner & Interstitial Ads.",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                    OutlinedTextField(
                                        value = adMobAppId,
                                        onValueChange = { adMobAppId = it },
                                        label = { Text("AdMob App ID") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = MatrixGreen,
                                            unfocusedBorderColor = BorderSubtle
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = bannerAdId,
                                        onValueChange = { bannerAdId = it },
                                        label = { Text("Banner Ad Unit ID") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = MatrixGreen,
                                            unfocusedBorderColor = BorderSubtle
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = interstitialAdId,
                                        onValueChange = { interstitialAdId = it },
                                        label = { Text("Instant / Interstitial Ad Unit ID") },
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = MatrixGreen,
                                            unfocusedBorderColor = BorderSubtle
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // APK Signing & Keystore
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Cryptographic APK Signing", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Badge(containerColor = MatrixGreen.copy(alpha = 0.2f)) {
                                        Text("SHA-256 RSA", color = MatrixGreen, fontSize = 10.sp)
                                    }
                                }
                                InfoField("Keystore Alias", project?.signingAlias ?: "flame_master_key")
                                InfoField("Key Algorithm", "RSA 2048-bit (X.509 Certificate)")
                                Text(
                                    "Note: Generated APKs are cryptographically signed with Flame Keystore for direct sideloading and Android package installation.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Main Build Button
                    item {
                        Button(
                            onClick = {
                                viewModel.startBuild(buildType, targetArch, enabledPermissions.toList())
                            },
                            enabled = !isBuilding && project != null,
                            colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("start_apk_build_button")
                        ) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = CanvasDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBuilding) "BUILDING APK..." else "BUILD STANDALONE ANDROID APK",
                                color = CanvasDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                "Logs" -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CanvasDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Terminal Output",
                                    color = MatrixGreen,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Divider(color = BorderSubtle, modifier = Modifier.padding(vertical = 4.dp))
                                Text(
                                    text = buildProgress?.logLine ?: lastBuildRecord?.logs ?: "Ready to compile. Press 'BUILD STANDALONE ANDROID APK' to start.",
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                "History" -> {
                    if (buildHistory.isEmpty()) {
                        item {
                            Text("No APK builds generated yet.", color = TextMuted, fontSize = 13.sp)
                        }
                    } else {
                        items(buildHistory) { record ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(record.apkFileName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("${record.apkSizeBytes / 1024} KB", color = PlasmaCyan, fontSize = 12.sp)
                                    }
                                    Text(
                                        "Built in ${record.buildDurationMs}ms • ${record.buildType}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { viewModel.builderEngine.launchInstallApkIntent(File(record.apkFilePath)) },
                                            colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Install", color = CanvasDark, fontSize = 11.sp)
                                        }
                                        Button(
                                            onClick = { viewModel.builderEngine.launchShareApkIntent(File(record.apkFilePath), record.gameName) },
                                            colors = ButtonDefaults.buttonColors(containerColor = PlasmaCyan),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text("Share", color = CanvasDark, fontSize = 11.sp)
                                        }
                                        IconButton(onClick = { viewModel.deleteBuildHistoryItem(record.id) }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = { viewModel.clearBuildCache() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = FlameRed),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Clear APK Build Cache")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BuildSuccessBanner(
    record: BuildRecord,
    onInstall: () -> Unit,
    onShare: () -> Unit,
    onBuildAgain: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F3820), SurfaceCard)
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MatrixGreen, modifier = Modifier.size(24.dp))
                    Text(
                        text = "BUILD SUCCESSFUL",
                        color = MatrixGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Divider(color = BorderSubtle)

                InfoField("Game Title", record.gameName)
                InfoField("APK File", record.apkFileName)
                InfoField("APK Size", "${record.apkSizeBytes / 1024} KB (${record.apkSizeBytes} bytes)")
                InfoField("Build Type", "${record.buildType.name} (Signed)")
                InfoField("Build Time", "${record.buildDurationMs} ms")
                InfoField("SHA-256", record.sha256Fingerprint.take(24) + "...")

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onInstall,
                        colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("install_apk_button")
                    ) {
                        Icon(Icons.Default.GetApp, contentDescription = null, tint = CanvasDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("INSTALL", color = CanvasDark, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onShare,
                        colors = ButtonDefaults.buttonColors(containerColor = PlasmaCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_apk_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = CanvasDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("SHARE", color = CanvasDark, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = onBuildAgain,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("BUILD AGAIN")
                }
            }
        }
    }
}

@Composable
private fun BuildProgressCard(buildProgress: com.example.builder.BuildProgress?) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildProgress?.step ?: "Compiling...",
                    color = FlameOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${((buildProgress?.progress ?: 0f) * 100).toInt()}%",
                    color = PlasmaCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = { buildProgress?.progress ?: 0f },
                color = FlameOrange,
                trackColor = SurfaceDark,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            )

            Text(
                text = buildProgress?.logLine ?: "Running build tasks...",
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun InfoField(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp)
        Text(value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}
