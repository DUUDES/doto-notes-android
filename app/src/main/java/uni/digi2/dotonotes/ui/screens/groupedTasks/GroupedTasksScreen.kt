@file:OptIn(ExperimentalMaterial3Api::class)

package uni.digi2.dotonotes.ui.screens.groupedTasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import uni.digi2.dotonotes.R
import uni.digi2.dotonotes.data.categories.Category
import uni.digi2.dotonotes.data.tasks.Task
import uni.digi2.dotonotes.ui.Screen
import uni.digi2.dotonotes.ui.screens.tasks.CreateTaskDialog
import uni.digi2.dotonotes.ui.screens.tasks.DeleteAllTasksDialog
import uni.digi2.dotonotes.ui.screens.tasks.DeleteTaskDialog
import uni.digi2.dotonotes.ui.screens.tasks.TasksOrderBy
import uni.digi2.dotonotes.ui.screens.tasks.TodoTaskItem
import uni.digi2.dotonotes.ui.screens.tasks.TodoViewModel
import uni.digi2.dotonotes.ui.screens.tasks.UpdateTaskDialog

@Composable
fun ExpandableCategoryCard(
    categoryName: String,
    tasks: List<Task>,
    onUpdate: (Task) -> Unit,
    showEditDialog: MutableState<String>,
    showDeleteDialog: MutableState<String>,
    showTaskInfo: (Task) -> Unit,
    editMode: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 16.dp)
                .animateContentSize()
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            BadgedBox(badge = {
                Badge(
                    backgroundColor = MaterialTheme.colorScheme.secondary,
                ) {
                    Text(
                        tasks.size.toString(),
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }) {
                Text(categoryName, style = MaterialTheme.typography.headlineMedium)
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    tasks.forEach { task ->
                        TodoTaskItem(
                            task = task,
                            onTaskUpdate = onUpdate,
                            showEditDialog = { showEditDialog.value = task.id },
                            showDeleteDialog = { showDeleteDialog.value = task.id },
                            editMode = editMode,
                            showTaskInfo = {
                                showTaskInfo(task)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GroupedTasks(
    navController: NavController,
    viewModel: TodoViewModel
) {

    val tasks by viewModel.tasks.collectAsState()
    val showCreateDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf("") }
    val showDeleteDialog = remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    val editMode = remember { mutableStateOf(false) }

    val fetchedCategories = runBlocking { viewModel.getCategories(auth.currentUser!!.uid) }

    val noneCategoryName = stringResource(R.string.category_none)

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 16.dp
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.background(color = MaterialTheme.colorScheme.primary),
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
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    },
                    title = {
                        Text(stringResource(R.string.grouped_tasks_page), style = MaterialTheme.typography.headlineLarge)
                    },
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog.value = true },
                shape = CircleShape,
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
                    items(
                        fetchedCategories + listOf(
                            Category(
                                name = noneCategoryName,
                                id = "null"
                            )
                        )
                    ) { category ->
                        val groupTasks =
                            tasks.filter { task -> task.categoryId.toString() == category.id }
                        ExpandableCategoryCard(
                            categoryName = category.name,
                            tasks = groupTasks,
                            showDeleteDialog = showDeleteDialog,
                            showEditDialog = showEditDialog,
                            showTaskInfo = { task ->
                                viewModel.selectedTask.value = task
                                navController.navigate("task_details")
                            },
                            onUpdate = { updatedTask ->
                                auth.currentUser?.let { it1 ->
                                    viewModel.updateTask(
                                        it1.uid,
                                        updatedTask
                                    )
                                }
                            },
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