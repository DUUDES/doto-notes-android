package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.data.tasks.TaskRepository
import uni.digi2.dotonotes.data.tasks.TodoTask
import uni.digi2.dotonotes.data.tasks.TodoTasksDao

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(viewModel: TodoViewModel = TodoViewModel(TaskRepository(TodoTasksDao()))) {
    val tasks by viewModel.tasks.collectAsState()
    val showCreateDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf("") }
    val showDeleteDialog = remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Todos List", style = MaterialTheme.typography.headlineLarge)
                },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary))
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog.value = true },
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.padding(16.dp).size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
            }
        },
        content = {
            it.calculateBottomPadding()
            Column {
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    val ordered = tasks
                        .filter { item -> !item.completed }
                        .sortedBy { task -> task.priority }

                    items(ordered) { task ->
                        TodoTaskItem(
                            task = task,
                            onTaskUpdate = { updatedTask ->
                                auth.currentUser?.let { it1 ->
                                    viewModel.updateTask(
                                        it1.uid,
                                        updatedTask
                                    )
                                }
                            },
                            showEditDialog = { showEditDialog.value = task.id },
                            showDeleteDialog = { showDeleteDialog.value = task.id }
                        )
                    }
                }
//                Button(
//                    onClick = {
//                        if (tasks.isNotEmpty()) {
//                            showDeleteDialog.value = "all"
//                        }
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                        .height(56.dp),
//                    enabled = tasks.isNotEmpty()
//                ) {
//                    Text("Delete All")
//                }
            }
        }
    )

    if (showCreateDialog.value) {
        CreateTaskDialog(
            onTaskCreated = { task ->
                auth.currentUser?.let { it1 ->
                    viewModel.addTask(
                        it1.uid,
                        task
                    )
                }
            },
            onDismiss = { showCreateDialog.value = false }
        )
    } else if (showEditDialog.value != "" && tasks.any { it.id == showEditDialog.value }) {
        UpdateTaskDialog(
            tasks.first { it.id == showEditDialog.value },
            onTaskUpdated = { task ->
                auth.currentUser?.let { it1 ->
                    viewModel.updateTask(
                        it1.uid,
                        task
                    )
                }
            },
            onDismiss = { showEditDialog.value = "" }
        )
    }

    if (showDeleteDialog.value != "") {
        if (showDeleteDialog.value == "all") {
            DeleteAllTasksDialog(
                onTasksDeleted = {
                    auth.currentUser?.let { it1 ->
                        viewModel.deleteAllTasks(it1.uid)
                    }
                },
                onDismiss = { showDeleteDialog.value = "" }
            )
        } else {
            DeleteTaskDialog(
                tasks.first { it.id == showDeleteDialog.value },
                onTaskDeleted = { deletedTask ->
                    auth.currentUser?.let { it1 ->
                        viewModel.deleteTask(
                            it1.uid,
                            deletedTask.id
                        )
                    }
                },
                onDismiss = { showDeleteDialog.value = "" }
            )
        }
    }
}
