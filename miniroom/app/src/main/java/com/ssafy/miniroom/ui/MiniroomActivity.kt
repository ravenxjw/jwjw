package com.ssafy.miniroom.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout
import com.ssafy.miniroom.databinding.ActivityMiniroomBinding
import com.ssafy.miniroom.model.FurnitureCategory
import com.ssafy.miniroom.model.FurnitureItem
import com.ssafy.miniroom.model.Position

class MiniroomActivity : AppCompatActivity(), FurnitureClickListener {
    private lateinit var binding: ActivityMiniroomBinding
    private val viewModel: MiniroomViewModel by viewModels()
    private lateinit var adapter: FurnitureShopAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMiniroomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupCategoryTabs()
        setupMiniroomView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = FurnitureShopAdapter(this)
        binding.furnitureContainer.adapter = adapter
    }

    private fun setupCategoryTabs() {
        FurnitureCategory.values().forEach { category ->
            binding.categoryTabs.addTab(
                binding.categoryTabs.newTab().setText(category.displayName)
            )
        }

        binding.categoryTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateShopItems()
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupMiniroomView() {
        binding.miniroomView.apply {
            viewModel.setMiniroomView(this)
            
            setOnFurnitureMoveListener { item, x, y ->
                viewModel.updateFurniturePosition(item, x, y)
            }
            
            setOnFurnitureRotateListener { item ->
                viewModel.rotateFurniture(item)
            }
            
            setOnFurnitureDeleteListener { item ->
                viewModel.removeFurniture(item)
            }
        }
    }

    private fun updateShopItems() {
        val selectedCategory = FurnitureCategory.values()[binding.categoryTabs.selectedTabPosition]
        val items = viewModel.shopItems.value.orEmpty()
        adapter.submitList(items.filter { it.category == selectedCategory })
    }

    private fun observeViewModel() {
        viewModel.shopItems.observe(this) {
            updateShopItems()
        }

        viewModel.furnitureItems.observe(this) { items ->
            binding.miniroomView.clearAll()
            items.forEach { item ->
                binding.miniroomView.addFurniture(item)
            }
        }
    }

    override fun onFurnitureClick(item: FurnitureItem) {
        val centerX = binding.miniroomView.width / 2f
        val centerY = binding.miniroomView.height / 2f
        viewModel.addFurniture(item.copy(
            id = System.currentTimeMillis(),
            position = Position(centerX, centerY)
        ))
    }
}