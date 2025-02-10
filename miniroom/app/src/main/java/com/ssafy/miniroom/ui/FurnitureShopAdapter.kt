package com.ssafy.miniroom.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.miniroom.R
import com.ssafy.miniroom.model.FurnitureItem

class FurnitureShopAdapter(
    private val listener: FurnitureClickListener
) : ListAdapter<FurnitureItem, FurnitureShopAdapter.ViewHolder>(FurnitureDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_furniture_shop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            listener.onFurnitureClick(item)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val imageView: ImageView = view.findViewById(R.id.furniture_image)
        private val nameText: TextView = view.findViewById(R.id.furniture_name)

        fun bind(item: FurnitureItem) {
            // 이미지 설정
            val resourceId = itemView.context.resources.getIdentifier(
                item.imageUrl,
                "drawable",
                itemView.context.packageName
            )
            imageView.setImageResource(resourceId)
            
            // 이름 설정
            nameText.text = item.name
        }
    }
}

class FurnitureDiffCallback : DiffUtil.ItemCallback<FurnitureItem>() {
    override fun areItemsTheSame(oldItem: FurnitureItem, newItem: FurnitureItem) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: FurnitureItem, newItem: FurnitureItem) = oldItem == newItem
}

interface FurnitureClickListener {
    fun onFurnitureClick(item: FurnitureItem)
} 