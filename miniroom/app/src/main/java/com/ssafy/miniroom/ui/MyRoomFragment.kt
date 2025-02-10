package com.ssafy.miniroom.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayout
import com.ssafy.miniroom.databinding.FragmentMyRoomBinding
import com.ssafy.miniroom.model.FurnitureCategory
import com.ssafy.miniroom.model.FurnitureItem
import com.ssafy.miniroom.model.Position

class MyRoomFragment : Fragment(), FurnitureClickListener {
    private var _binding: FragmentMyRoomBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MiniroomViewModel by viewModels()
    private lateinit var adapter: FurnitureShopAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
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
        viewModel.shopItems.observe(viewLifecycleOwner) {
            updateShopItems()
        }

        viewModel.furnitureItems.observe(viewLifecycleOwner) { items ->
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 