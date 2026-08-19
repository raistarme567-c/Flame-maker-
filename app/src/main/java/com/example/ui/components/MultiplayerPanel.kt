package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MultiplayerRoom
import com.example.model.NetworkPlayerState
import com.example.model.NetworkRole
import com.example.ui.theme.*

@Composable
fun MultiplayerPanel(
    room: MultiplayerRoom,
    onRoleChange: (NetworkRole) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Network Role & Room status
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
                        Text("Multiplayer & Networking Hub", color = FlameOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Badge(containerColor = MatrixGreen.copy(alpha = 0.2f)) {
                            Text("LAN / P2P Ready", color = MatrixGreen, fontSize = 10.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = room.currentRole == NetworkRole.HOST_SERVER,
                            onClick = { onRoleChange(NetworkRole.HOST_SERVER) },
                            label = { Text("Host Server (LAN)") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                        )
                        FilterChip(
                            selected = room.currentRole == NetworkRole.JOIN_CLIENT,
                            onClick = { onRoleChange(NetworkRole.JOIN_CLIENT) },
                            label = { Text("Join Client") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FlameOrange)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Room ID: ${room.roomId}", color = TextSecondary, fontSize = 12.sp)
                        Text("Max Players: ${room.maxPlayers}", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            }
        }

        // 2. Connected Players List
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Synchronized Network Peers (${room.connectedPlayers.size})", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    if (room.connectedPlayers.isEmpty()) {
                        Text("No external peers connected. Running local client-prediction loop.", color = TextMuted, fontSize = 12.sp)
                    } else {
                        room.connectedPlayers.forEach { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceCardLight, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(modifier = Modifier.size(8.dp).background(MatrixGreen, CircleShape))
                                    Text(player.playerName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("${player.pingMs} ms", color = PlasmaCyan, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
