package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow

class TodoViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance().reference.child("tasks")
    private val _tasks = mutableStateListOf<TodoTask>()

    val tasks: MutableStateFlow<List<TodoTask>> = MutableStateFlow(_tasks.toList())

    fun getTodos(userId: String) {
        database.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _tasks.clear()
                for (childSnapshot in snapshot.children) {
                    val task = childSnapshot.getValue(TodoTask::class.java)
                    task?.let { _tasks.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    init {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val task = childSnapshot.getValue(TodoTask::class.java)
                    task?.let { _tasks.add(it) }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun addTask(task: TodoTask) {
        val taskId = database.push().key ?: return
        val newTask = task.copy(id = taskId)
        database.child(taskId).setValue(newTask)
    }

    fun updateTask(task: TodoTask) {
        database.child(task.id).setValue(task)
    }

    fun deleteTask(task: TodoTask) {
        database.child(task.id).removeValue()
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
