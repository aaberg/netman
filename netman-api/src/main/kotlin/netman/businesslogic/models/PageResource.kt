package netman.businesslogic.models

data class PageResource<out T>(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val items: List<T>
)

data class PageableResource (
    val page: Int,
    val pageSize: Int
)