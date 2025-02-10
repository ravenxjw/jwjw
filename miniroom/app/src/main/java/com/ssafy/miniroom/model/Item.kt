data class FurnitureItem(
    val id: Long,
    val name: String,
    val category: FurnitureCategory,
    val imageUrl: String,
    val position: Position,
    var isSelected: Boolean = false
)

enum class FurnitureCategory {
    BED, DESK, CHAIR, DECORATION, WALL, FLOOR
}

data class Position(
    var x: Float,
    var y: Float
) 