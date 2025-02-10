package com.ssafy.miniroom.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ssafy.miniroom.data.RoomRepository
import com.ssafy.miniroom.model.FurnitureData
import com.ssafy.miniroom.model.FurnitureItem
import com.ssafy.miniroom.model.Position
import com.ssafy.miniroom.ui.custom.MiniroomView

class MiniroomViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = RoomRepository(application)
    
    private val _shopItems = MutableLiveData<List<FurnitureItem>>()
    val shopItems: LiveData<List<FurnitureItem>> = _shopItems
    
    private val _furnitureItems = MutableLiveData<List<FurnitureItem>>()
    val furnitureItems: LiveData<List<FurnitureItem>> = _furnitureItems
    
    private var _miniroomView: MiniroomView? = null
    
    init {
        _shopItems.value = FurnitureData.shopItems
        _furnitureItems.value = repository.loadRoom()
    }
    
    fun setMiniroomView(view: MiniroomView) {
        _miniroomView = view
    }
    
    fun addFurniture(item: FurnitureItem) {
        val currentList = _furnitureItems.value.orEmpty().toMutableList()
        currentList.add(item)
        _furnitureItems.value = currentList
        saveRoom()
    }
    
    fun removeFurniture(item: FurnitureItem) {
        val currentList = _furnitureItems.value.orEmpty().toMutableList()
        currentList.removeAll { it.id == item.id }
        _furnitureItems.value = currentList
        saveRoom()
    }
    
    fun updateFurniturePosition(item: FurnitureItem, x: Float, y: Float) {
        val currentList = _furnitureItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            currentList[index] = item.copy(position = Position(x, y))
            _furnitureItems.value = currentList
            saveRoom()
        }
    }
    
    fun rotateFurniture(item: FurnitureItem) {
        val currentList = _furnitureItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == item.id }
        if (index != -1) {
            val oldItem = currentList[index]
            val currentRotation = oldItem.rotation ?: 0f
            val newRotation = (currentRotation + 90) % 360
            val newItem = oldItem.copy(rotation = newRotation)
            
            // 리스트 업데이트
            currentList[index] = newItem
            _furnitureItems.value = currentList
            
            // 즉시 회전 적용
            _miniroomView?.rotateFurniture(newItem, newRotation)
            
            // 저장
            saveRoom()
        }
    }
    
    private fun saveRoom() {
        repository.saveRoom(_furnitureItems.value.orEmpty())
    }
} 