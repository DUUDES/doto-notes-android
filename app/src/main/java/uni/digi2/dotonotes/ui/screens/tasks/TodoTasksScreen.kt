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
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import uni.digi2.dotonotes.R
import uni.digi2.dotonotes.data.categories.CategoriesDao
import uni.digi2.dotonotes.data.tasks.TaskRepository
import uni.digi2.dotonotes.data.tasks.TodoTask
import uni.digi2.dotonotes.data.tasks.TodoTasksDao
import uni.digi2.dotonotes.ui.Screen
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navController: NavController,
    viewModel: TodoViewModel = TodoViewModel(TaskRepository(TodoTasksDao()), CategoriesDao())
) {
    val tasks by viewModel.tasks.collectAsState()
    val showCreateDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf("") }
    val showDeleteDialog = remember { mutableStateOf("") }
    val sortDropdownExpanded = remember { mutableStateOf(false) }
    val orderByRule = remember { mutableStateOf(TasksOrderBy.Priority) }


    val auth = FirebaseAuth.getInstance()

    val fetchedCategories = runBlocking { viewModel.getCategories(auth.currentUser!!.uid) }

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    val context = LocalContext.current
                    val bitmap = ContextCompat.getDrawable(context, R.drawable.baseline_category_24)
                        ?.toBitmap()
                        ?.asImageBitmap()!!

                    IconButton(onClick = { navController.navigate(Screen.Categories.route) }) {
                        Icon(
                            bitmap,
                            contentDescription = "Categories",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    IconButton(onClick = { sortDropdownExpanded.value = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "Categories",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = sortDropdownExpanded.value,
                        onDismissRequest = {
                            sortDropdownExpanded.value = false
                        }
                    ) {
                        TasksOrderBy.values().forEach { itemValue ->
                            androidx.compose.material.DropdownMenuItem(
                                onClick = {
                                    orderByRule.value = itemValue
                                    sortDropdownExpanded.value = false
                                },
                            ) {
                                Text(itemValue.name, style = MaterialTheme.typography.headlineSmall)
                            }
                        }
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

                    val ordered = orderByRule.value.rule(
                        tasks.filter { item -> !item.completed }
                    )

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
            onDismiss = { showCreateDialog.value = false },
            categories = fetchedCategories
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
            onDismiss = { showEditDialog.value = "" },
            categories = fetchedCategories
        )
    } else if (showDeleteDialog.value != "") {
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