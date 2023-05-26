package uni.digi2.dotonotes.data.categories

import android.content.ContentValues
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import uni.digi2.dotonotes.data.tasks.TodoTask
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import uni.digi2.dotonotes.data.tasks.TodoTasksCollection
import java.util.Date

data class TaskCategory(
    var id: String = "",
    val name: String = "",
    val createdOn: Date = Date(),
)

interface ICategoriesDao {
    suspend fun addCategory(userId: String, category: TaskCategory)
    suspend fun getCategories(userId: String): List<TaskCategory>
    suspend fun updateCategory(userId: String, category: TaskCategory)
    suspend fun deleteCategory(userId: String, categoryId: String)
    fun observeCategoriesRealtime(userId: String): Flow<List<TaskCategory>>
    suspend fun stopObservation()
}

class CategoriesDao : ICategoriesDao {
    private val db = FirebaseFirestore.getInstance()

    private var listenerRegistration : ListenerRegistration? = null

    override suspend fun addCategory(userId: String, category: TaskCategory) {
        val documentRef = db.collection("users").document(userId)

        val taskRef = documentRef.collection("categories")
            .document()

        val categoryId = taskRef.id
        category.id = categoryId

        documentRef.get().addOnSuccessListener {
            val data = it.toObject(TodoTasksCollection::class.java) ?: TodoTasksCollection()

            data.categories.add(category)

            documentRef.set(data).addOnSuccessListener {
                Log.d(ContentValues.TAG, "Document added with ID: $userId")
            }.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
        }
    }

    override suspend fun getCategories(userId: String): List<TaskCategory> {
        val documentRef = db.collection("users").document(userId)

        val documentSnapshot = documentRef.get().await()
        val todos = documentSnapshot.toObject(TodoTasksCollection::class.java)

        return todos?.categories ?: emptyList()
    }

    override suspend fun updateCategory(userId: String, category: TaskCategory) {
        val documentRef = db.collection("users").document(userId)

        documentRef.get().addOnSuccessListener {
            val data = it.toObject(TodoTasksCollection::class.java) ?: TodoTasksCollection()

            data.categories.removeIf { c -> c.id == category.id }
            data.categories.add(category)

            documentRef.set(data).addOnSuccessListener {
                Log.d(ContentValues.TAG, "Document added with ID: $userId")
            }.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
        }
    }

    override suspend fun deleteCategory(userId: String, categoryId: String) {
        val documentRef = db.collection("users").document(userId)

        documentRef.get().addOnSuccessListener {
            val data = it.toObject(TodoTasksCollection::class.java) ?: TodoTasksCollection()

            data.tasks
                .filter { task -> task.categoryId == categoryId }
                .forEach { task -> task.categoryId = null }

            data.categories.removeIf { category -> category.id == categoryId }

            documentRef.set(data).addOnSuccessListener {
                Log.d(ContentValues.TAG, "Document added with ID: $userId")
            }.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
        }
    }

    override fun observeCategoriesRealtime(userId: String): Flow<List<TaskCategory>> =
        callbackFlow {
            val documentRef = db.collection("users").document(userId)

            listenerRegistration =
                documentRef.addSnapshotListener(EventListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@EventListener
                    }

                    val categories = snapshot?.toObject(TodoTasksCollection::class.java)?.categories
                        ?: emptyList()
                    try {
                        trySend(categories).isSuccess
                    } catch (exception: Exception) {
                        close(exception)
                    }
                })

            awaitClose { listenerRegistration?.remove() }
        }

    override suspend fun stopObservation() {
        listenerRegistration?.remove()
    }
}