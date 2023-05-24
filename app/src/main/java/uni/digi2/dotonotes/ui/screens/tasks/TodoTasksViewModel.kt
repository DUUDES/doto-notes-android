package uni.digi2.dotonotes.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uni.digi2.dotonotes.data.tasks.TaskRepository
import uni.digi2.dotonotes.data.tasks.TodoTask

class TodoViewModel(private val taskRepository: TaskRepository) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TodoTask>>(emptyList())
    val tasks: StateFlow<List<TodoTask>> = _tasks

    init {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            getTasks(user.uid)
            viewModelScope.launch {
                taskRepository.observeTasksRealtime(user.uid)
                    .collect {
                        _tasks.value = it
                    }
            }
        }
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

    fun deleteAllTasks(userId: String) {
        viewModelScope.launch {
            taskRepository.deleteAllTasks(userId)
            getTasks(userId)
        }
    }

    fun getTaskById(userId: String, taskId: String): TodoTask? {
        return runBlocking {
            taskRepository.getTaskById(userId, taskId)
        }
    }
}
