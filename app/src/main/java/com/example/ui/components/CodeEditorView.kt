package com.example.ui.components

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameObject
import com.example.ui.theme.*

@Composable
fun CodeEditorView(
    selectedEntity: GameObject?,
    onSaveScript: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var codeText by remember(selectedEntity?.id) {
        mutableStateOf(
            selectedEntity?.scriptCode?.ifEmpty {
                """
                // FlameScript Entity Component
                // Auto-attached to: ${selectedEntity?.name ?: "GameObject"}
                
                class ${selectedEntity?.name ?: "CustomScript"} : FlameComponent() {
                    var speed: Float = 5.0f
                    var isInvincible: Boolean = false
                    
                    override fun onStart() {
                        Flame.log("Initialized on: " + entity.name)
                    }
                    
                    override fun onUpdate(dt: Float) {
                        val input = Input.getJoystickVector()
                        transform.position.x += input.x * speed * dt
                        
                        if (Input.isButtonDown("Fire")) {
                            spawnBullet()
                        }
                    }
                    
                    fun spawnBullet() {
                        Scene.instantiate("BulletPrefab", transform.position)
                        Audio.play("sfx_laser")
                    }
                }
                """.trimIndent()
            } ?: "// Select an entity to edit attached FlameScript"
        )
    }

    var compileStatus by remember { mutableStateOf("Ready") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Code Toolbar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Code, contentDescription = null, tint = FlameOrange)
                Text(
                    text = "${selectedEntity?.name ?: "MainScript"}.flame",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = {
                        compileStatus = "Compiling..."
                        onSaveScript(codeText)
                        compileStatus = "Build OK (0 errors)"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("compile_script_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CanvasDark)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Hot-Reload", color = CanvasDark, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Status bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Status: $compileStatus",
                color = if (compileStatus.contains("OK")) MatrixGreen else PlasmaCyan,
                fontSize = 11.sp
            )
            Text(
                text = "Lines: ${codeText.lines().size} | FlameScript JIT",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        // Code Editor Text Field
        OutlinedTextField(
            value = codeText,
            onValueChange = { codeText = it },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                color = TextPrimary
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SurfaceDark,
                unfocusedContainerColor = SurfaceDark,
                focusedBorderColor = FlameOrange,
                unfocusedBorderColor = BorderSubtle
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("code_editor_input")
        )
    }
}
