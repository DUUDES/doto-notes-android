package uni.digi2.dotonotes.data.tasks

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import uni.digi2.dotonotes.ui.screens.profile.User
import uni.digi2.dotonotes.ui.screens.tasks.TodoTask

interface TaskDao {
    suspend fun addTask(userId: String, task: TodoTask)
    suspend fun getTasks(userId: String): List<TodoTask>
    suspend fun updateTask(userId: String, task: TodoTask)
    suspend fun deleteTask(userId: String, taskId: String)
}

class TodoTasksDao : TaskDao {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun addTask(userId: String, task: TodoTask) {
        db.collection("users")
            .document(userId)
            .update("todoTasks", FieldValue.arrayUnion(task))
            .await()
    }

    override suspend fun getTasks(userId: String): List<TodoTask> {
        val userDoc = db.collection("users")
            .document(userId)
            .get()
            .await()

        val user = userDoc.toObject(User::class.java)
        return user?.todoTasks ?: emptyList()
    }

    override suspend fun updateTask(userId: String, task: TodoTask) {
        db.collection("users")
            .document(userId)
            .update("todoTasks", FieldValue.arrayRemove(task))
            .await()

        db.collection("users")
            .document(userId)
            .update("todoTasks", FieldValue.arrayUnion(task))
            .await()
    }

    override suspend fun deleteTask(userId: String, taskId: String) {
        db.collection("users")
            .document(userId)
            .update("todoTasks", FieldValue.arrayRemove(taskId))
            .await()
    }
}