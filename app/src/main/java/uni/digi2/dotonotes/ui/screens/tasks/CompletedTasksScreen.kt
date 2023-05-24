package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
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
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletedTasksScreen(
    navController: NavController,
    viewModel: TodoViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    val showDeleteDialog = remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()

    Scaffold(
        content = {
            it.calculateBottomPadding()
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Completed ToDo List", style = MaterialTheme.typography.headlineLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(tasks.filter { item -> item.completed }) { task ->
                        CompletedTaskItem(
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
                            showDeleteDialog = { showDeleteDialog.value = task.id }
                        )
                    }
                }
                Button(
                    onClick = {
                        if (tasks.any { item -> item.completed }) {
                            showDeleteDialog.value = "all"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    enabled = tasks.any { item -> item.completed }
                ) {
                    Text("Delete All")
                }
            }
        }
    )

    if (showDeleteDialog.value != "") {
        if (showDeleteDialog.value == "all") {
            DeleteAllCompletedTasksDialog(
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
fun CompletedTaskItem(
    navController: NavController,
    task: TodoTask,
    onTaskUpdate: (TodoTask) -> Unit,
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
            onClick = showDeleteDialog
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Task")
        }
    }
}

@Composable
fun DeleteAllCompletedTasksDialog(
    onTasksDeleted: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Delete All Completed Tasks")
        },
        text = {
            Text(text = "Are you sure you want to delete all completed tasks?")
        },
        confirmButton = {
            Button(
                onClick = {
                    onTasksDeleted()
                    onDismiss()
                }
            ) {
                Text("Delete All")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

