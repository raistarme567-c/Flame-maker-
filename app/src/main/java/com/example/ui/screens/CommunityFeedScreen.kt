package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameDimension
import com.example.model.GameProject
import com.example.model.PublishedGame
import com.example.monetization.AdMobBannerView
import com.example.ui.theme.*
import com.example.ui.viewmodel.EngineViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityFeedScreen(
    viewModel: EngineViewModel,
    onNavigateBack: () -> Unit,
    onOpenInEditor: (GameProject) -> Unit
) {
    val communityGames by viewModel.communityGames.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val developerName by viewModel.developerName.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var showPublishDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CanvasDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Apps & Games Hub",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Badge(containerColor = FlameOrange) {
                                Text("Public Feed", color = CanvasDark, fontSize = 9.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "Play creator games & publish your own",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("feed_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    Button(
                        onClick = { showPublishDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp).testTag("publish_game_top_button")
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = CanvasDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Publish Game", color = CanvasDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        },
        bottomBar = {
            AdMobBannerView(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(communityGames, key = { it.id }) { game ->
                CommunityGameCard(
                    game = game,
                    onLikeToggle = { viewModel.toggleLikeCommunityGame(game.id) },
                    onPlayTrigger = { viewModel.recordGamePlayed(game.id) }
                )
            }
        }
    }

    // Publish Game Modal Dialog
    if (showPublishDialog) {
        var selectedProject by remember { mutableStateOf(allProjects.firstOrNull()) }
        var titleInput by remember { mutableStateOf(selectedProject?.name ?: "My Epic Game") }
        var genreInput by remember { mutableStateOf(selectedProject?.genre ?: "Action RPG") }
        var descInput by remember { mutableStateOf("Play my new interactive game! Built on mobile with Flame Maker.") }

        AlertDialog(
            onDismissRequest = { showPublishDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, tint = FlameOrange)
                    Text("Publish to Apps & Games Feed", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Publisher: $developerName ${if (isLoggedIn) "✓ Verified" else "(Guest)"}",
                        color = MatrixGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (allProjects.isNotEmpty()) {
                        Text("Select Project to Publish:", color = TextSecondary, fontSize = 12.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            allProjects.take(3).forEach { proj ->
                                FilterChip(
                                    selected = selectedProject?.id == proj.id,
                                    onClick = {
                                        selectedProject = proj
                                        titleInput = proj.name
                                        genreInput = proj.genre
                                    },
                                    label = { Text(proj.name.take(12), fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = titleInput,
                        onValueChange = { titleInput = it },
                        label = { Text("Game Title") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = genreInput,
                        onValueChange = { genreInput = it },
                        label = { Text("Genre / Category") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descInput,
                        onValueChange = { descInput = it },
                        label = { Text("Description & Instructions") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = FlameOrange,
                            unfocusedBorderColor = BorderSubtle
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val projToPub = selectedProject ?: GameProject(name = titleInput, genre = genreInput)
                        viewModel.publishGameToCommunity(projToPub, titleInput, descInput, genreInput)
                        showPublishDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                    modifier = Modifier.testTag("confirm_publish_button")
                ) {
                    Text("Publish Now", color = CanvasDark, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPublishDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = SurfaceCard
        )
    }
}

@Composable
private fun CommunityGameCard(
    game: PublishedGame,
    onLikeToggle: () -> Unit,
    onPlayTrigger: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var playerX by remember { mutableFloatStateOf(160f) }
    var playerY by remember { mutableFloatStateOf(200f) }
    var score by remember { mutableIntStateOf(0) }
    var health by remember { mutableIntStateOf(100) }

    // Heart scale animation on like
    val heartScale by animateFloatAsState(
        targetValue = if (game.isLikedByMe) 1.25f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "heartScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Publisher Header (Creator Name, Tag & Time)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(game.primaryColorHex), Color(0xFF1E2235))
                                )
                            )
                            .border(1.5.dp, Color(game.primaryColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = "Publisher",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = game.creatorName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(Icons.Default.CheckCircle, contentDescription = "Verified Creator", tint = FlameOrange, modifier = Modifier.size(13.dp))
                        }
                        Text(
                            text = game.creatorTag,
                            color = PlasmaCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    color = CanvasDark,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Text(
                        text = game.genre,
                        color = FlameAmber,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Title & Description
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = game.title,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = game.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            // Playable In-Feed Game Engine Window
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF090A10))
                    .border(1.dp, Color(game.primaryColorHex).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            ) {
                // Interactive Game Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(isPlaying) {
                            if (isPlaying) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    playerX = (playerX + dragAmount.x).coerceIn(20f, 320f)
                                    playerY = (playerY + dragAmount.y).coerceIn(20f, 200f)
                                    score += 1
                                }
                            }
                        }
                ) {
                    // Grid background
                    val gridSpacing = 30f
                    for (x in 0..size.width.toInt() step gridSpacing.toInt()) {
                        drawLine(Color(0xFF1E2235).copy(alpha = 0.5f), Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), 1f)
                    }
                    for (y in 0..size.height.toInt() step gridSpacing.toInt()) {
                        drawLine(Color(0xFF1E2235).copy(alpha = 0.5f), Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), 1f)
                    }

                    // Draw Game Scene Entities
                    game.sceneData.entities.forEach { entity ->
                        val entColor = try {
                            Color(android.graphics.Color.parseColor(entity.colorHex))
                        } catch (e: Exception) {
                            Color(0xFFFF5722)
                        }
                        drawCircle(color = entColor.copy(alpha = 0.85f), radius = 14f, center = Offset(entity.position.x, entity.position.y))
                    }

                    // Draw Controlled Player
                    if (isPlaying) {
                        drawCircle(
                            color = Color(game.primaryColorHex),
                            radius = 18f,
                            center = Offset(playerX, playerY)
                        )
                        // Player Glow
                        drawCircle(
                            color = Color(game.primaryColorHex).copy(alpha = 0.3f),
                            radius = 26f,
                            center = Offset(playerX, playerY)
                        )
                    }
                }

                // Play Overlay when not playing
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.6f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                isPlaying = true
                                onPlayTrigger()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(game.primaryColorHex)),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.testTag("play_community_game_${game.id}")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CanvasDark, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("TAP TO PLAY NOW", color = CanvasDark, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                } else {
                    // Live Game HUD
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = CanvasDark.copy(alpha = 0.8f), shape = RoundedCornerShape(6.dp)) {
                            Text("Score: $score pts", color = MatrixGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Surface(color = CanvasDark.copy(alpha = 0.8f), shape = RoundedCornerShape(6.dp)) {
                            Text("HP: $health%", color = FlameOrange, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    // D-Pad Touch Helper
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 6.dp)
                            .background(CanvasDark.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    ) {
                        Text("Drag finger to move player & score points", color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }

            // Real-Time Like & Social Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Real-Time Like Button
                Button(
                    onClick = onLikeToggle,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (game.isLikedByMe) Color(0xFFFF1744).copy(alpha = 0.2f) else CanvasDark
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (game.isLikedByMe) Color(0xFFFF1744) else BorderSubtle
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("like_game_${game.id}")
                ) {
                    Icon(
                        imageVector = if (game.isLikedByMe) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (game.isLikedByMe) Color(0xFFFF1744) else TextSecondary,
                        modifier = Modifier.size((18 * heartScale).dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${game.likesCount} Likes",
                        color = if (game.isLikedByMe) Color(0xFFFF1744) else TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Plays Counter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                    Text("${game.playsCount} plays", color = TextSecondary, fontSize = 11.sp)
                }

                // Share / Play More
                TextButton(
                    onClick = { onPlayTrigger() },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = PlasmaCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", color = PlasmaCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
