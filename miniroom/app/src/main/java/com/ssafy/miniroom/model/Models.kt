package com.ssafy.miniroom.model

data class FurnitureItem(
    val id: Long,
    val name: String,
    val category: FurnitureCategory,
    val imageUrl: String,
    val position: Position = Position(0f, 0f),
    val rotation: Float? = null
)

data class Position(
    val x: Float,
    val y: Float
)

enum class FurnitureCategory {
    BED, DESK, CHAIR;
    
    val displayName: String
        get() = when (this) {
            BED -> "침대"
            DESK -> "책상"
            CHAIR -> "의자"
        }
} 