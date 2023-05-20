package uni.digi2.dotonotes.ui.screens.profile

import uni.digi2.dotonotes.ui.screens.tasks.TodoTask

class User (
    val id: String = "",
    val todoTasks: List<TodoTask> = emptyList()
)