package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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
                actions = {
                    val context = LocalContext.current
                    val categoryBitmap = ContextCompat.getDrawable(context, R.drawable.baseline_category_24)
                        ?.toBitmap()
                        ?.asImageBitmap()!!
                    val visibilityBitmap = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24)
                        ?.toBitmap()
                        ?.asImageBitmap()!!
                    val editNoteBitmap = ContextCompat.getDrawable(context, R.drawable.baseline_edit_note_24)
                        ?.toBitmap()
                        ?.asImageBitmap()!!

                    IconButton(onClick = { editMode.value = !editMode.value }) {
                        Icon(
                            if(editMode.value) visibilityBitmap else  editNoteBitmap,
                            contentDescription = "Edit Mode",
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    IconButton(onClick = { navController.navigate(Screen.Categories.route) }) {
                        Icon(
                            categoryBitmap,
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
                    val ordered = orderByRule.value.rule(tasks.filter { item -> !item.completed })
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
                            showDeleteDialog = { showDeleteDialog.value = task.id },
                            editMode = editMode.value
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