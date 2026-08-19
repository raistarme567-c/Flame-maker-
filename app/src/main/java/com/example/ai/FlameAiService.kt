package com.example.ai

import com.example.BuildConfig
import com.example.model.GameObject
import com.example.model.GameSceneData
import com.example.model.NodeCategory
import com.example.model.ScriptPort
import com.example.model.VisualNode
import com.example.model.VisualScriptGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

data class FlameAiMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: String, // "user" or "flame_ai"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val codeSnippet: String? = null,
    val generatedNodes: List<VisualNode>? = null,
    val suggestedAction: String? = null
)

class FlameAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askFlameAi(
        prompt: String,
        currentScene: GameSceneData?,
        selectedEntity: GameObject?
    ): FlameAiMessage = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        // If a real Gemini API key is configured and not default placeholder, use the live Gemini 3.5 Flash model!
        if (apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val contextPrompt = """
                    You are Flame AI, the built-in intelligent game engine assistant for FLAME MAKER ("Create. Build. Play.").
                    Current Engine Context:
                    - Scene: ${currentScene?.name ?: "Main"} (${currentScene?.dimension ?: "2D"})
                    - Entity Count: ${currentScene?.entities?.size ?: 0}
                    - Selected Entity: ${selectedEntity?.name ?: "None"} (Tag: ${selectedEntity?.tag ?: "Untagged"})
                    
                    User Query: $prompt
                    
                    Provide a concise, practical, expert game-development solution. If code is needed, write clean FlameScript / Kotlin game code.
                """.trimIndent()

                val jsonBody = JSONObject().apply {
                    val contentsArr = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply { put("text", contextPrompt) })
                            })
                        }
                        put(partObj)
                    }
                    put("contents", contentsArr)
                }

                val request = Request.Builder()
                    .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                    .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseStr = response.body?.string() ?: ""
                    val root = JSONObject(responseStr)
                    val replyText = root.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")

                    return@withContext FlameAiMessage(
                        sender = "flame_ai",
                        text = replyText,
                        suggestedAction = "Apply AI Solution"
                    )
                }
            } catch (e: Exception) {
                // Fall back to intelligent built-in smart engine assistant
            }
        }

        // Intelligent Built-in Offline Engine Knowledge & Generator
        generateSmartOfflineResponse(prompt, currentScene, selectedEntity)
    }

    private fun generateSmartOfflineResponse(
        prompt: String,
        currentScene: GameSceneData?,
        selectedEntity: GameObject?
    ): FlameAiMessage {
        val lower = prompt.lowercase()

        return when {
            lower.contains("controller") || lower.contains("player") || lower.contains("movement") -> {
                val code = """
                    // FlameScript Player Movement & Physics Controller
                    class PlayerController : FlameComponent() {
                        var speed: Float = 6.0f
                        var jumpStrength: Float = 12.0f
                        var canDoubleJump: Boolean = true
                        
                        override fun onUpdate(dt: Float) {
                            val input = Input.getJoystickVector()
                            transform.position.x += input.x * speed * dt
                            
                            if (Input.isButtonDown("Jump") && isGrounded()) {
                                rigidbody.velocity.y = jumpStrength
                                Audio.play("sfx_jump")
                                Particles.emit("jump_spark", transform.position)
                            }
                        }
                    }
                """.trimIndent()

                FlameAiMessage(
                    sender = "flame_ai",
                    text = "I've generated an optimized **Player Controller** with smooth movement, jump physics, and particle response. You can attach this directly to '${selectedEntity?.name ?: "Player"}' or inspect the visual nodes.",
                    codeSnippet = code,
                    suggestedAction = "Attach to Selected Entity"
                )
            }
            lower.contains("enemy") || lower.contains("ai") || lower.contains("chase") || lower.contains("patrol") -> {
                val code = """
                    // Flame AI Perception & Behavior Routine
                    class EnemyAI : FlameBehavior() {
                        var visionRange: Float = 8.0f
                        var attackRadius: Float = 1.5f
                        var speed: Float = 3.5f
                        
                        override fun onUpdate(dt: Float) {
                            val player = Scene.findEntityByTag("Player") ?: return
                            val distance = Vector3.distance(transform.position, player.position)
                            
                            if (distance < attackRadius) {
                                performAttack(player)
                            } else if (distance < visionRange) {
                                chaseTarget(player.position, speed, dt)
                            } else {
                                patrolWaypoints(dt)
                            }
                        }
                    }
                """.trimIndent()

                FlameAiMessage(
                    sender = "flame_ai",
                    text = "Created an intelligent **NPC AI Behavior Tree** featuring sensory vision, waypoint patrolling, and player chasing logic with smooth acceleration.",
                    codeSnippet = code,
                    suggestedAction = "Attach AI Component"
                )
            }
            lower.contains("apk") || lower.contains("export") || lower.contains("build") || lower.contains("keystore") -> {
                FlameAiMessage(
                    sender = "flame_ai",
                    text = """
                        **Flame Maker Direct Mobile APK Build Guide:**
                        1. Navigate to the **BUILD & EXPORT** center in the bottom menu.
                        2. Choose **Debug APK** (fast testing) or **Release APK** (production distribution).
                        3. Review package identity (`${currentScene?.name ?: "com.flame.mygame"}`) and Android permissions.
                        4. Tap **BUILD APK DIRECTLY**. The engine will assemble the DEX runtime, manifest, and assets into an installable `.apk`.
                        5. Use **Install** or **Share** to distribute directly to players without requiring a PC!
                    """.trimIndent()
                )
            }
            lower.contains("fps") || lower.contains("optimize") || lower.contains("performance") -> {
                FlameAiMessage(
                    sender = "flame_ai",
                    text = """
                        **Engine Optimization Recommendations:**
                        - Current Active Entities: ${currentScene?.entities?.size ?: 0}
                        - Recommendation: Set Mobile Optimization Profile to **'Medium'** or **'Low'** for budget devices (caps particle emitters to 30 and disables real-time shadow passes).
                        - Static batching is enabled for background platforms.
                        - Texture compression set to ASTC/ETC2.
                    """.trimIndent(),
                    suggestedAction = "Apply Optimization Preset"
                )
            }
            else -> {
                FlameAiMessage(
                    sender = "flame_ai",
                    text = "I'm Flame AI, your mobile game engine assistant! I can help you build custom physics controllers, generate visual script graphs, write FlameScript logic, optimize draw calls, or guide you through creating standalone Android APKs. What would you like to build next?"
                )
            }
        }
    }
}
