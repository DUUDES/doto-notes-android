package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.data.tasks.TaskRepository
import uni.digi2.dotonotes.data.tasks.TodoTasksDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(viewModel: TodoViewModel = TodoViewModel(TaskRepository(TodoTasksDao()))) {
    val tasks = remember { mutableStateListOf<TodoTask>() }
    val showDialog = remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog.value = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        },
        content = {
            it.calculateBottomPadding()
            Column {
                Text("Todo List", style = MaterialTheme.typography.headlineLarge)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(tasks) { task ->
                        TodoTaskItem(
                            task = task,
                            onTaskUpdate = { updatedTask ->
                                auth.currentUser?.let { it1 -> viewModel.updateTask(it1.uid, updatedTask) }
                            },
                            onTaskDelete = { deletedTask ->
                                auth.currentUser?.let { it1 -> viewModel.deleteTask(it1.uid, deletedTask.id) }
                            }
                        )
                    }
                }
            }
        }
    )

    if (showDialog.value) {
        CreateTaskDialog(
            onTaskCreated = { newTaskTitle ->
                auth.currentUser?.let { it1 -> viewModel.addTask(it1.uid, TodoTask(title = newTaskTitle)) }
            },
            onDismiss = { showDialog.value = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    onTaskCreated: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Create New Task") },
        text = {
            TextField(
                value = taskTitle,
                onValueChange = { newValue -> taskTitle = newValue },
                label = { Text("Task Title") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onTaskCreated(taskTitle)
                        onDismiss()
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() }
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun TodoTaskItem(
    task: TodoTask,
    onTaskUpdate: (TodoTask) -> Unit,
    onTaskDelete: (TodoTask) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { isChecked ->
                onTaskUpdate(task.copy(completed = isChecked))
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { onTaskDelete(task) }
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}
