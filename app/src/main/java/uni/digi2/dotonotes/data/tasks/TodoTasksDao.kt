package uni.digi2.dotonotes.data.tasks

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date


data class TodoTask(
    var id: String = "",
    val title: String = "",
    val description: String = "",
    val priority: Int = 0,
    val createdOn: Date = Date(),
    val checkedOn: Date? = null,
    val completed: Boolean = false
)

data class TodoTasksCollection(
    val tasks: MutableList<TodoTask> = mutableListOf()
)

interface TaskDao {
    suspend fun addTask(userId: String, task: TodoTask)
    suspend fun getTasks(userId: String): List<TodoTask>
    suspend fun updateTask(userId: String, task: TodoTask)
    suspend fun deleteTask(userId: String, taskId: String)
    suspend fun deleteAllTasks(userId: String)
    suspend fun getTaskById(userId: String, taskId: String): TodoTask?
    fun observeTasksRealtime(userId: String): Flow<List<TodoTask>>
}

class TodoTasksDao : TaskDao {
    private val db = FirebaseFirestore.getInstance()

    override suspend fun getTasks(userId: String): List<TodoTask> {
        val documentRef = db.collection("users").document(userId)

        val documentSnapshot = documentRef.get().await()
        val todos = documentSnapshot.toObject(TodoTasksCollection::class.java)

        return todos?.tasks ?: emptyList()
    }

    override fun observeTasksRealtime(userId: String): Flow<List<TodoTask>> = callbackFlow  {
        val documentRef = db.collection("users").document(userId)

        val listenerRegistration = documentRef.addSnapshotListener(EventListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@EventListener
            }

            val tasks = snapshot?.toObject(TodoTasksCollection::class.java)?.tasks ?: emptyList()
            try {
                trySend(tasks).isSuccess
            } catch (exception: Exception) {
                close(exception)
            }
        })

        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun addTask(userId: String, task: TodoTask) {
        val documentRef = db.collection("users").document(userId)

        val taskRef = documentRef.collection("tasks")
            .document()

        val taskId = taskRef.id
        task.id = taskId

        documentRef.get().addOnSuccessListener {
            val data = it.toObject(TodoTasksCollection::class.java) ?: TodoTasksCollection()

            data.tasks.add(task)

            documentRef.set(data).addOnSuccessListener {
                Log.d(TAG, "Document added with ID: $userId")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        }
    }

    override suspend fun updateTask(userId: String, task: TodoTask) {
        val documentRef = db.collection("users").document(userId)

        documentRef.get().addOnSuccessListener {
            val data = it.toObject(TodoTasksCollection::class.java) ?: TodoTasksCollection()

            data.tasks.removeIf { t -> t.id == task.id }
            data.tasks.add(task)

            documentRef.set(data).addOnSuccessListener {
                Log.d(TAG, "Document added with ID: $userId")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        }
    }

    override suspend fun deleteTask(userId: String, taskId: String) {
        val documentRef = db.collection("users").document(userId)

        documentRef.get().addOnSuccessListener {
            val data = it.toObject(TodoTasksCollection::class.java) ?: TodoTasksCollection()

            data.tasks.removeIf { task -> task.id == taskId }

            documentRef.set(data).addOnSuccessListener {
                Log.d(TAG, "Document added with ID: $userId")
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        }

    }

    override suspend fun deleteAllTasks(userId: String) {
        val documentRef = db.collection("users").document(userId)

        documentRef.set(TodoTasksCollection()).addOnSuccessListener {
            Log.d(TAG, "All tasks deleted for user: $userId")
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error deleting tasks", e)
        }
    }

    override suspend fun getTaskById(userId: String, taskId: String): TodoTask? {
        val documentRef = db.collection("users").document(userId)

        val documentSnapshot = documentRef.get().await()
        val todos = documentSnapshot.toObject(TodoTasksCollection::class.java)

        return todos?.tasks?.find { it.id == taskId }
    }


}
