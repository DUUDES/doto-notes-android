package uni.digi2.dotonotes.data.tasks

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    suspend fun addTask(userId: String, task: TodoTask) {
        taskDao.addTask(userId, task)
    }

    suspend fun getTasks(userId: String): List<TodoTask> {
        return taskDao.getTasks(userId)
    }

    suspend fun updateTask(userId: String, task: TodoTask) {
        taskDao.updateTask(userId, task)
    }

    suspend fun deleteTask(userId: String, taskId: String) {
        taskDao.deleteTask(userId, taskId)
    }

    suspend fun deleteAllTasks(userId: String) {
        taskDao.deleteAllTasks(userId)
    }
    fun observeTasksRealtime(userId: String) : Flow<List<TodoTask>> {
        return taskDao.observeTasksRealtime(userId)
    }
}