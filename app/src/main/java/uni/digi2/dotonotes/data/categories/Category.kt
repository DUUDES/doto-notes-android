package uni.digi2.dotonotes.data.categories

import java.util.Date

data class Category(
    var id: String = "",
    val name: String = "",
    val createdOn: Date = Date(),
)