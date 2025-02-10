package com.ssafy.miniroom.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.core.view.children
import com.ssafy.miniroom.R
import com.ssafy.miniroom.model.FurnitureItem
import kotlin.math.abs
import kotlin.math.roundToInt

class MiniroomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val GRID_SIZE = 40f  // 격자 한 칸의 크기 (dp)
        const val GRID_VISIBLE = true  // 격자 표시 여부
        const val ANIMATION_DURATION = 150L  // 애니메이션 지속 시간
    }

    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private var selectedView: View? = null
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private var initialViewX = 0f
    private var initialViewY = 0f
    private var isDragging = false
    private var currentAnimator: ValueAnimator? = null
    private var onFurnitureMove: ((FurnitureItem, Float, Float) -> Unit)? = null
    private var onFurnitureDelete: ((FurnitureItem) -> Unit)? = null
    private var onFurnitureRotate: ((FurnitureItem) -> Unit)? = null
    private var controlsView: View? = null
    private var lastTouchTime = 0L
    private val CLICK_TIME_THRESHOLD = 200L
    
    private val gridSizePx = context.resources.displayMetrics.density * GRID_SIZE
    private val furnitureViews = mutableMapOf<Long, View>()
    private var popupWindow: PopupWindow? = null

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (GRID_VISIBLE) {
            drawGrid(canvas)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        val gridCount = (width / gridSizePx).toInt() + 1
        val gridCountVertical = (height / gridSizePx).toInt() + 1

        // 세로선
        for (i in 0..gridCount) {
            val x = i * gridSizePx
            canvas.drawLine(x, 0f, x, height.toFloat(), gridPaint)
        }
        // 가로선
        for (i in 0..gridCountVertical) {
            val y = i * gridSizePx
            canvas.drawLine(0f, y, width.toFloat(), y, gridPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val touchedView = findViewUnderTouch(event.x, event.y)
                if (touchedView != null) {
                    selectedView = touchedView
                    initialTouchX = event.x
                    initialTouchY = event.y
                    initialViewX = touchedView.x
                    initialViewY = touchedView.y
                    lastTouchTime = System.currentTimeMillis()
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                selectedView?.let { view ->
                    val dx = event.x - initialTouchX
                    val dy = event.y - initialTouchY
                    
                    if (!isDragging && (abs(dx) > 10 || abs(dy) > 10)) {
                        isDragging = true
                    }
                    
                    if (isDragging) {
                        var newX = initialViewX + dx
                        var newY = initialViewY + dy
                        
                        newX = newX.coerceIn(0f, width - view.width.toFloat())
                        newY = newY.coerceIn(0f, height - view.height.toFloat())
                        
                        view.x = newX
                        view.y = newY
                        
                        updatePopupPosition(view)
                        
                        (view.tag as? FurnitureItem)?.let { item ->
                            onFurnitureMove?.invoke(item, newX + view.width / 2, newY + view.height / 2)
                        }
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                selectedView?.let { view ->
                    val currentTime = System.currentTimeMillis()
                    if (!isDragging && (currentTime - lastTouchTime) < CLICK_TIME_THRESHOLD) {
                        showPopupMenu(view)
                    }
                }
                isDragging = false
                selectedView = null
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun findViewUnderTouch(x: Float, y: Float): View? {
        for (i in childCount - 1 downTo 0) {
            val child = getChildAt(i)
            if (x >= child.x && x <= child.x + child.width &&
                y >= child.y && y <= child.y + child.height) {
                return child
            }
        }
        return null
    }

    fun addFurniture(item: FurnitureItem) {
        if (furnitureViews.containsKey(item.id)) return

        val view = LayoutInflater.from(context)
            .inflate(R.layout.view_furniture, this, false)
        
        view.tag = item

        val imageView = view.findViewById<ImageView>(R.id.furniture_image)

        val resourceId = resources.getIdentifier(
            item.imageUrl,
            "drawable",
            context.packageName
        )
        imageView.setImageResource(resourceId)
        
        // 회전 적용
        imageView.rotation = item.rotation ?: 0f

        view.x = item.position.x - view.layoutParams.width / 2f
        view.y = item.position.y - view.layoutParams.height / 2f

        addView(view)
        furnitureViews[item.id] = view
    }

    fun removeFurniture(itemId: Long) {
        furnitureViews[itemId]?.let { view ->
            removeView(view)
            furnitureViews.remove(itemId)
        }
    }

    fun clearAll() {
        removeAllViews()
        furnitureViews.clear()
    }

    fun setOnFurnitureMoveListener(listener: (FurnitureItem, Float, Float) -> Unit) {
        onFurnitureMove = listener
    }

    private fun updatePopupPosition(view: View) {
        popupWindow?.let { popup ->
            if (popup.isShowing) {
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                popup.update(
                    location[0] + view.width,
                    location[1],
                    popup.width,
                    popup.height
                )
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val item = view.tag as? FurnitureItem ?: return
        
        popupWindow?.dismiss()
        
        val popupView = LayoutInflater.from(context)
            .inflate(R.layout.popup_furniture_menu, null)
        
        popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            isOutsideTouchable = true
            isFocusable = true
            elevation = 10f
            setBackgroundDrawable(null)
        }

        popupView.findViewById<ImageButton>(R.id.btn_rotate).setOnClickListener {
            onFurnitureRotate?.invoke(item)
        }

        popupView.findViewById<ImageButton>(R.id.btn_delete).setOnClickListener {
            onFurnitureDelete?.invoke(item)
            popupWindow?.dismiss()
        }

        popupView.findViewById<ImageButton>(R.id.btn_cancel).setOnClickListener {
            popupWindow?.dismiss()
        }

        val location = IntArray(2)
        view.getLocationOnScreen(location)
        
        popupWindow?.showAtLocation(
            view,
            Gravity.NO_GRAVITY,
            location[0] + view.width,
            location[1]
        )
    }

    fun setOnFurnitureRotateListener(listener: (FurnitureItem) -> Unit) {
        onFurnitureRotate = listener
    }

    fun setOnFurnitureDeleteListener(listener: (FurnitureItem) -> Unit) {
        onFurnitureDelete = listener
    }

    fun rotateFurniture(item: FurnitureItem, rotation: Float) {
        furnitureViews[item.id]?.let { view ->
            view.findViewById<ImageView>(R.id.furniture_image).rotation = rotation
        }
    }
} 