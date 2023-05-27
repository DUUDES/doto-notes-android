package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import uni.digi2.dotonotes.R
import uni.digi2.dotonotes.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    navController: NavController,
    viewModel: TodoViewModel
) {
    val tasks by viewModel.tasks.collectAsState()
    val showCreateDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf("") }
    val showDeleteDialog = remember { mutableStateOf("") }
    val sortDropdownExpanded = remember { mutableStateOf(false) }
    val orderByRule = remember { mutableStateOf(TasksOrderBy.Priority) }
    val auth = FirebaseAuth.getInstance()
    val fetchedCategories = runBlocking { viewModel.getCategories(auth.currentUser!!.uid) }
    val editMode = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { editMode.value = !editMode.value }) {
                        Icon(
                            imageVector = if (editMode.value)
                                ImageVector.vectorResource(id = R.drawable.baseline_visibility_24)
                            else
                                ImageVector.vectorResource(id = R.drawable.baseline_edit_note_24),
                            contentDescription = "Edit Mode",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    IconButton(onClick = { navController.navigate(Screen.Categories.route) }) {
                        Icon(
                            ImageVector.vectorResource(id = R.drawable.baseline_category_24),
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
                                Text(
                                    itemValue.ruleName,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                },
                title = {
                    Text("Todos List", style = MaterialTheme.typography.headlineLarge)
                })
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
            it.calculateTopPadding()
            Column {
                Spacer(modifier = Modifier.height(64.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    val ordered = orderByRule.value.rule(tasks.filter { item -> !item.completed })
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
                            showDeleteDialog = { showDeleteDialog.value = task.id },
                            editMode = editMode.value,
                            showTaskInfo = {
                                viewModel.selectedTask.value = task
                                navController.navigate("task_details")
                            }
                        )
                    }
                }
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