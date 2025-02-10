package com.ssafy.miniroom.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ssafy.miniroom.model.FurnitureItem

class RoomRepository(private val context: Context) {
    private val sharedPreferences = context.getSharedPreferences("miniroom", Context.MODE_PRIVATE)
    
    fun saveRoom(items: List<FurnitureItem>) {
        val json = Gson().toJson(items)
        sharedPreferences.edit().putString("room_items", json).apply()
    }
    
    fun loadRoom(): List<FurnitureItem> {
        val json = sharedPreferences.getString("room_items", null)
        return if (json != null) {
            Gson().fromJson(json, object : TypeToken<List<FurnitureItem>>() {}.type)
        } else {
            emptyList()
        }
    }
} 