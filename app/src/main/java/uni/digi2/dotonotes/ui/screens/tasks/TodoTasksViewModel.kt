package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uni.digi2.dotonotes.data.categories.Category
import uni.digi2.dotonotes.data.categories.ICategoriesDao
import uni.digi2.dotonotes.data.tasks.ITodoTasksDao
import uni.digi2.dotonotes.data.tasks.Task

class TodoViewModel(
    private val tasksDao: ITodoTasksDao,
    private val categoriesDao: ICategoriesDao
) : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    val selectedTask : MutableState<Task?> = mutableStateOf(null)
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        FirebaseAuth.getInstance().addAuthStateListener {
            it.currentUser?.let { user ->
                getTasks(user.uid)
                viewModelScope.launch {
                    tasksDao.observeTasksRealtime(user.uid)
                        .collect { tasks ->
                            _tasks.value = tasks
                        }
                }
            }
        }
    }



    fun stopObservation() = viewModelScope.launch { tasksDao.stopObservation() }

    suspend fun getCategories(userId: String): List<Category> {
        return categoriesDao.getCategories(userId)
    }

    private fun getTasks(userId: String) {
        viewModelScope.launch {
            _tasks.value = tasksDao.getTasks(userId)
        }
    }

    fun addTask(userId: String, task: Task) {
        viewModelScope.launch {
            tasksDao.addTask(userId, task)
            getTasks(userId)
        }
    }

    fun updateTask(userId: String, task: Task) {
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

    fun deleteAllCompletedTasks(userId: String) {
        viewModelScope.launch {
            tasksDao.deleteAllCompletedTasks(userId)
            getTasks(userId)
        }
    }

    fun getTaskById(userId: String, taskId: String): Task? {
        return runBlocking {
            tasksDao.getTaskById(userId, taskId)
        }
    }
}
