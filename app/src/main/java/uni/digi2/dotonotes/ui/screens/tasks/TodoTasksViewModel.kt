package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import uni.digi2.dotonotes.data.tasks.TaskRepository
import uni.digi2.dotonotes.data.tasks.TodoTask

class TodoViewModel(private val taskRepository: TaskRepository) : ViewModel() {
    private val _tasks = mutableStateOf<List<TodoTask>>(emptyList())
    val tasks: State<List<TodoTask>> = _tasks

    init {
        FirebaseAuth.getInstance().currentUser?.let { getTasks(it.uid) }
    }

    private fun getTasks(userId: String) {
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
