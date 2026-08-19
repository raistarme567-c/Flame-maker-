package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.ai.FlameAiMessage
import com.example.model.GameObject
import com.example.ui.theme.*

@Composable
fun FlameAiPanel(
    messages: List<FlameAiMessage>,
    isGenerating: Boolean,
    selectedEntity: GameObject?,
    onSendPrompt: (String) -> Unit,
    onApplyCodeToEntity: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // AI Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = FlameOrange)
                Text(
                    text = "Flame AI Assistant",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Badge(containerColor = NeonPurple.copy(alpha = 0.25f)) {
                Text("Gemini 3.5 Flash", color = NeonPurple, fontSize = 10.sp)
            }
        }

        // Quick Suggestion Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(
                "Create 3rd-Person Controller",
                "Generate Enemy Patrol AI",
                "Optimize for 60 FPS Mobile"
            ).forEach { prompt ->
                SuggestionChip(
                    onClick = { onSendPrompt(prompt) },
                    label = { Text(prompt, fontSize = 10.sp) },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = SurfaceCard,
                        labelColor = PlasmaCyan
                    )
                )
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "user"

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) FlameOrange else SurfaceCard
                        ),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 12.dp
                        ),
                        modifier = Modifier.widthIn(max = 320.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = msg.text,
                                color = if (isUser) CanvasDark else TextPrimary,
                                fontSize = 13.sp
                            )

                            // Code snippet if attached
                            msg.codeSnippet?.let { code ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(CanvasDark, RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = code,
                                        fontFamily = FontFamily.Monospace,
                                        color = MatrixGreen,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { onApplyCodeToEntity(code) },
                                        colors = ButtonDefaults.buttonColors(containerColor = PlasmaCyan),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Text("Apply to Selected", color = CanvasDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = FlameOrange, strokeWidth = 2.dp)
                        Text("Flame AI is formulating solution...", color = TextMuted, fontSize = 12.sp)
                    }
                }
            }
        }

        // Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Flame AI to code, debug, or build...", color = TextMuted, fontSize = 12.sp) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = FlameOrange,
                    unfocusedBorderColor = BorderSubtle
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_prompt_input")
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendPrompt(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(FlameOrange, RoundedCornerShape(8.dp))
                    .testTag("ai_send_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = CanvasDark)
            }
        }
    }
}
