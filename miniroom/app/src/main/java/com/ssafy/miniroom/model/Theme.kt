package com.ssafy.miniroom.model

import com.ssafy.miniroom.R

enum class RoomTheme(val wallColorResId: Int, val floorColorResId: Int) {
    BASIC(R.color.theme_basic_wall, R.color.theme_basic_floor),
    PASTEL(R.color.theme_pastel_wall, R.color.theme_pastel_floor),
    MODERN(R.color.theme_modern_wall, R.color.theme_modern_floor),
    VINTAGE(R.color.theme_vintage_wall, R.color.theme_vintage_floor)
} 