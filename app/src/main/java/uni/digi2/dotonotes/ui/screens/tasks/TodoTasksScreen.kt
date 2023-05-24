package uni.digi2.dotonotes.ui.screens.tasks

import android.telecom.Call.Details
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.data.tasks.TaskRepository
import uni.digi2.dotonotes.data.tasks.TodoTask
import uni.digi2.dotonotes.data.tasks.TodoTasksDao
import uni.digi2.dotonotes.ui.Screen

import uni.digi2.dotonotes.ui.Screen
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navController: NavController,
    viewModel: TodoViewModel = TodoViewModel(TaskRepository(TodoTasksDao()))
) {
    val tasks by viewModel.tasks.collectAsState()
    val showCreateDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf("") }
    val showDeleteDialog = remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()


    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Categories.route) }) {
                        Icon(
                            Icons.Default.List,
                            contentDescription = "Categories",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                },
                title = {
                    Text("Todos List", style = MaterialTheme.typography.headlineLarge)
                },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog.value = true },
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier
                    .padding(16.dp)
                    .size(64.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(24.dp))
            }
        },
        content = {
            it.calculateBottomPadding()
            Column {
                Spacer(modifier = Modifier.height(48.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {

                    val ordered = tasks
                        .filter { item -> !item.completed }
                        .sortedBy { task -> task.priority }

                    items(ordered) { task ->
                        TodoTaskItem(
                            navController = navController,
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

@Composable
fun TodoTaskItem(
    navController: NavController,
    task: TodoTask,
    onTaskUpdate: (TodoTask) -> Unit,
    showEditDialog: () -> Unit,
    showDeleteDialog: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.completed,
            onCheckedChange = { check ->
                onTaskUpdate(
                    task.copy(
                        completed = check,
                        checkedOn = if (check) Date() else task.checkedOn
                    )
                )
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = {
                navController.navigate("task_details/${task.id}")
            }
        ) {
            Icon(Icons.Default.Info, contentDescription = "Task Details")
        }
        IconButton(
            onClick = showEditDialog
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
        }
        IconButton(
            onClick = showDeleteDialog
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}
