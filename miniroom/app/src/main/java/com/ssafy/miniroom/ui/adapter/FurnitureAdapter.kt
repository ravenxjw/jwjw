package com.ssafy.miniroom.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.miniroom.databinding.ItemFurnitureBinding
import com.ssafy.miniroom.model.FurnitureItem

class FurnitureAdapter(
    private val onItemClick: (FurnitureItem) -> Unit,
    private val onItemLongClick: (FurnitureItem) -> Unit
) : ListAdapter<FurnitureItem, FurnitureAdapter.FurnitureViewHolder>(FurnitureDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FurnitureViewHolder {
        return FurnitureViewHolder(
            ItemFurnitureBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FurnitureViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FurnitureViewHolder(
        private val binding: ItemFurnitureBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick(getItem(adapterPosition))
            }
            binding.root.setOnLongClickListener {
                onItemLongClick(getItem(adapterPosition))
                true
            }
        }

        fun bind(item: FurnitureItem) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}

class FurnitureDiffCallback : DiffUtil.ItemCallback<FurnitureItem>() {
    override fun areItemsTheSame(oldItem: FurnitureItem, newItem: FurnitureItem) = 
        oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FurnitureItem, newItem: FurnitureItem) = 
        oldItem == newItem
} 