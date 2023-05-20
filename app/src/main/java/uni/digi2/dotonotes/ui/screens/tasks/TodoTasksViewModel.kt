package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uni.digi2.dotonotes.data.tasks.TaskRepository

class TodoViewModel(private val taskRepository: TaskRepository) : ViewModel() {
    private val _tasks = mutableStateOf<List<TodoTask>>(emptyList())
    val tasks: State<List<TodoTask>> = _tasks
    
    fun getTasks(userId: String) {
        viewModelScope.launch {
            _tasks.value = taskRepository.getTasks(userId)
        }
    }

    fun addTask(userId: String, task: TodoTask) {
        viewModelScope.launch {
            taskRepository.addTask(userId, task)
            getTasks(userId)
        }
    }

    fun updateTask(userId: String, task: TodoTask) {
        viewModelScope.launch {
            taskRepository.updateTask(userId, task)
            getTasks(userId)
        }
    }

    fun deleteTask(userId: String, taskId: String) {
        viewModelScope.launch {
            taskRepository.deleteTask(userId, taskId)
            getTasks(userId)
        }
    }
}

data class TodoTask(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val priority: Int = 0,
    val completed: Boolean = false,
    val userId: String = ""
)
