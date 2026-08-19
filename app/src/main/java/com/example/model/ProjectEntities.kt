package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class GameDimension { TWO_D, THREE_D }
enum class GameOrientation { PORTRAIT, LANDSCAPE, AUTO }
enum class BuildType { DEBUG, RELEASE }
enum class TargetArch { UNIVERSAL, ARM64, ARMV7 }

@Entity(tableName = "projects")
data class GameProject(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String = "Untitled Game",
    val packageName: String = "com.flame.mygame",
    val versionName: String = "1.0.0",
    val versionCode: Int = 1,
    val genre: String = "Platformer",
    val dimension: GameDimension = GameDimension.TWO_D,
    val orientation: GameOrientation = GameOrientation.LANDSCAPE,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val activeSceneId: String = "",
    val iconUri: String = "",
    val splashColorHex: String = "#120D1E",
    val optimizationLevel: String = "High",
    val targetMinSdk: Int = 24,
    val targetSdk: Int = 36,
    val permissionsJson: String = "[\"android.permission.INTERNET\", \"android.permission.VIBRATE\"]",
    val signingAlias: String = "flame_key",
    val isDefaultKeystore: Boolean = true
)

@Entity(tableName = "build_history")
data class BuildRecord(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val gameName: String,
    val apkFileName: String,
    val apkFilePath: String,
    val apkSizeBytes: Long,
    val buildDurationMs: Long,
    val buildType: BuildType,
    val targetArch: TargetArch,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuccess: Boolean = true,
    val logs: String = "",
    val sha256Fingerprint: String = ""
)

@Entity(tableName = "game_scenes")
data class SceneEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String = "Main Scene",
    val sceneDataJson: String = "",
    val orderIndex: Int = 0
)

@Entity(tableName = "project_assets")
data class ProjectAsset(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val name: String,
    val assetType: String, // TEXTURE, MESH, AUDIO, SHADER, SCRIPT, PREFAB
    val uriOrPath: String,
    val sizeBytes: Long = 0,
    val createdAt: Long = System.currentTimeMillis()
)
