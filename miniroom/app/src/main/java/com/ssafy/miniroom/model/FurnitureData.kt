package com.ssafy.miniroom.model

object FurnitureData {
    val shopItems = listOf(
        // 침대
        FurnitureItem(
            id = 101L,
            name = "기본 침대",
            category = FurnitureCategory.BED,
            imageUrl = "furniture_bed_temp"
        ),
        FurnitureItem(
            id = 102L,
            name = "프리미엄 침대",
            category = FurnitureCategory.BED,
            imageUrl = "furniture_bed_temp"
        ),
        
        // 책상
        FurnitureItem(
            id = 201L,
            name = "기본 책상",
            category = FurnitureCategory.DESK,
            imageUrl = "furniture_desk_temp"
        ),
        FurnitureItem(
            id = 202L,
            name = "스터디 책상",
            category = FurnitureCategory.DESK,
            imageUrl = "furniture_desk_temp"
        ),
        
        // 의자
        FurnitureItem(
            id = 301L,
            name = "기본 의자",
            category = FurnitureCategory.CHAIR,
            imageUrl = "furniture_chair_temp"
        ),
        FurnitureItem(
            id = 302L,
            name = "게이밍 의자",
            category = FurnitureCategory.CHAIR,
            imageUrl = "furniture_chair_temp"
        )
    )
} 