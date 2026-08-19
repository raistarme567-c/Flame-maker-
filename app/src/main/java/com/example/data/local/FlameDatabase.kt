package com.example.data.local

import androidx.room.*
import com.example.model.BuildRecord
import com.example.model.GameProject
import com.example.model.ProjectAsset
import com.example.model.SceneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM projects ORDER BY lastModified DESC")
    fun getAllProjects(): Flow<List<GameProject>>

    @Query("SELECT * FROM projects WHERE id = :id")
    suspend fun getProjectById(id: String): GameProject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: GameProject)

    @Update
    suspend fun updateProject(project: GameProject)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProject(id: String)
}

@Dao
interface BuildRecordDao {
    @Query("SELECT * FROM build_history ORDER BY timestamp DESC")
    fun getAllBuildRecords(): Flow<List<BuildRecord>>

    @Query("SELECT * FROM build_history WHERE projectId = :projectId ORDER BY timestamp DESC")
    fun getBuildsForProject(projectId: String): Flow<List<BuildRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBuildRecord(record: BuildRecord)

    @Query("DELETE FROM build_history WHERE id = :id")
    suspend fun deleteBuildRecord(id: String)

    @Query("DELETE FROM build_history")
    suspend fun clearAllBuilds()
}

@Dao
interface SceneDao {
    @Query("SELECT * FROM game_scenes WHERE projectId = :projectId ORDER BY orderIndex ASC")
    fun getScenesForProject(projectId: String): Flow<List<SceneEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScene(scene: SceneEntity)

    @Query("DELETE FROM game_scenes WHERE id = :id")
    suspend fun deleteScene(id: String)
}

@Dao
interface AssetDao {
    @Query("SELECT * FROM project_assets WHERE projectId = :projectId ORDER BY createdAt DESC")
    fun getAssetsForProject(projectId: String): Flow<List<ProjectAsset>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: ProjectAsset)

    @Query("DELETE FROM project_assets WHERE id = :id")
    suspend fun deleteAsset(id: String)
}

@Database(
    entities = [GameProject::class, BuildRecord::class, SceneEntity::class, ProjectAsset::class],
    version = 1,
    exportSchema = false
)
abstract class FlameDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun buildRecordDao(): BuildRecordDao
    abstract fun sceneDao(): SceneDao
    abstract fun assetDao(): AssetDao
}
