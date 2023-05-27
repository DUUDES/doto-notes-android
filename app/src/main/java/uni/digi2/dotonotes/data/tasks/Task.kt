package uni.digi2.dotonotes.data.tasks

import java.util.Date

data class Task(
    var id: String = "",
    var categoryId: String? = null,
    val title: String = "",
    val description: String = "",
    val priority: Int = 0,
    val createdOn: Date = Date(),
    val checkedOn: Date? = null,
    val dueTo: Date? = null,
    val completed: Boolean = false,
    val updatedOn: Date = Date()
)
