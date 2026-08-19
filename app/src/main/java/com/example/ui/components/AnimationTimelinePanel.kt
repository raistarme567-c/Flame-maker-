package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*

@Composable
fun AnimationTimelinePanel(
    activeClip: AnimationClip,
    onAddKeyframe: (String, Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(0.0f) }
    var isPlayingTimeline by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf("Timeline") } // Timeline or State Machine

    LaunchedEffect(isPlayingTimeline) {
        while (isPlayingTimeline) {
            kotlinx.coroutines.delay(33)
            currentTime = (currentTime + 0.033f) % activeClip.durationSeconds
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Tab switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = selectedTab == "Timeline",
                    onClick = { selectedTab = "Timeline" },
                    label = { Text("Keyframe Timeline") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                )
                FilterChip(
                    selected = selectedTab == "StateMachine",
                    onClick = { selectedTab = "StateMachine" },
                    label = { Text("Animation State Machine") },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                )
            }

            // Play / Pause Scrub
            IconButton(
                onClick = { isPlayingTimeline = !isPlayingTimeline },
                modifier = Modifier
                    .size(36.dp)
                    .background(if (isPlayingTimeline) FlameRed else MatrixGreen, CircleShape)
            ) {
                Icon(
                    imageVector = if (isPlayingTimeline) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = CanvasDark
                )
            }
        }

        if (selectedTab == "Timeline") {
            // Timeline Scrubber Header
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
                        Text("Clip: ${activeClip.name}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${String.format("%.2f", currentTime)}s / ${activeClip.durationSeconds}s",
                            color = PlasmaCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = currentTime,
                        onValueChange = { currentTime = it },
                        valueRange = 0f..activeClip.durationSeconds,
                        colors = SliderDefaults.colors(thumbColor = FlameOrange, activeTrackColor = FlameOrange)
                    )
                }
            }

            // Tracks List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(activeClip.tracks) { track ->
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
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(track.name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Type: ${track.type.name} • Keyframes: ${track.keyframes.size}", color = TextSecondary, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onAddKeyframe(track.id, currentTime, 1.0f) },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceCardLight),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.AddLocation, contentDescription = null, tint = PlasmaCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("+ Keyframe", color = PlasmaCyan, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // Visual Animation State Machine
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Character Animation State Machine", color = FlameOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    val states = listOf(
                        "Entry" to MatrixGreen,
                        "Idle_Stance" to PlasmaCyan,
                        "Run_Sprint" to FlameAmber,
                        "Jump_Ascend" to NeonPurple,
                        "Fall_Land" to SoftPink
                    )

                    states.forEach { (stateName, color) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceCardLight, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
                                Text(stateName, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Badge(containerColor = color.copy(alpha = 0.2f)) {
                                Text("Transition Ready", color = color, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
