package com.example.model

import java.util.UUID

data class PublishedGame(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val creatorName: String,
    val creatorTag: String,
    val description: String,
    val genre: String,
    val dimension: GameDimension = GameDimension.TWO_D,
    val likesCount: Int = 0,
    val isLikedByMe: Boolean = false,
    val playsCount: Int = 0,
    val publishedDate: Long = System.currentTimeMillis(),
    val primaryColorHex: Long = 0xFFFF5722,
    val sceneData: GameSceneData = GameSceneData()
)
