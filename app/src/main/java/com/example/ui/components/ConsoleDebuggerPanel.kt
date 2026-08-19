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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConsoleLogEntry
import com.example.model.LogSeverity
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ConsoleDebuggerPanel(
    logs: List<ConsoleLogEntry>,
    onClearLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<LogSeverity?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredLogs = remember(logs, selectedFilter, searchQuery) {
        logs.filter { entry ->
            (selectedFilter == null || entry.severity == selectedFilter) &&
            (searchQuery.isEmpty() || entry.message.contains(searchQuery, ignoreCase = true) || entry.tag.contains(searchQuery, ignoreCase = true))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("All (${logs.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == LogSeverity.INFO,
                    onClick = { selectedFilter = LogSeverity.INFO },
                    label = { Text("Info", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == LogSeverity.WARNING,
                    onClick = { selectedFilter = LogSeverity.WARNING },
                    label = { Text("Warn", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == LogSeverity.ERROR,
                    onClick = { selectedFilter = LogSeverity.ERROR },
                    label = { Text("Error", fontSize = 11.sp) }
                )
            }

            IconButton(
                onClick = onClearLogs,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Clear", tint = TextMuted)
            }
        }

        // Logs Output
        Card(
            colors = CardDefaults.cardColors(containerColor = CanvasDark),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredLogs) { entry ->
                    val color = when (entry.severity) {
                        LogSeverity.INFO -> TextPrimary
                        LogSeverity.WARNING -> FlameYellow
                        LogSeverity.ERROR -> FlameRed
                        LogSeverity.FRAME_PROFILE -> PlasmaCyan
                    }

                    val timeStr = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(entry.timestamp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(timeStr, color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("[${entry.tag}]", color = PlasmaCyan, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        Text(entry.message, color = color, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
