package uni.digi2.dotonotes.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uni.digi2.dotonotes.data.categories.ICategoriesDao
import uni.digi2.dotonotes.data.categories.TaskCategory
import uni.digi2.dotonotes.data.tasks.ITodoTasksDao
import uni.digi2.dotonotes.data.tasks.TodoTask

class TodoViewModel(
    private val tasksDao: ITodoTasksDao,
    private val categoriesDao: ICategoriesDao
    ) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TodoTask>>(emptyList())
    val tasks: StateFlow<List<TodoTask>> = _tasks

    init {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            getTasks(user.uid)
            viewModelScope.launch {
                tasksDao.observeTasksRealtime(user.uid)
                    .collect {
                        _tasks.value = it
                    }
            }
        }
    }

    fun stopObservation() = viewModelScope.launch { tasksDao.stopObservation() }

    suspend fun getCategories(userId: String) : List<TaskCategory> {
        return categoriesDao.getCategories(userId)
    }

    private fun getTasks(userId: String) {
        viewModelScope.launch {
            _tasks.value = tasksDao.getTasks(userId)
        }
    }

    fun addTask(userId: String, task: TodoTask) {
        viewModelScope.launch {
            tasksDao.addTask(userId, task)
            getTasks(userId)
        }
    }

    fun updateTask(userId: String, task: TodoTask) {
        viewModelScope.launch {
            tasksDao.updateTask(userId, task)
            getTasks(userId)
        }
    }

    fun deleteTask(userId: String, taskId: String) {
        viewModelScope.launch {
            tasksDao.deleteTask(userId, taskId)
            getTasks(userId)
        }
    }

    fun deleteAllTasks(userId: String) {
        viewModelScope.launch {
            tasksDao.deleteAllTasks(userId)
            getTasks(userId)
        }
    }

    fun getTaskById(userId: String, taskId: String): TodoTask? {
        return runBlocking {
            tasksDao.getTaskById(userId, taskId)
        }
    }
}
