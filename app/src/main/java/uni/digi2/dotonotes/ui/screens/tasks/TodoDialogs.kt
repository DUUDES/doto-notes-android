package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uni.digi2.dotonotes.data.tasks.TodoTask


@Composable
fun CreateTaskDialog(
    onTaskCreated: (TodoTask) -> Unit,
    onDismiss: () -> Unit
) = TaskDialog(
    task = null,
    label = "Create ToDo",
    onSubmit = onTaskCreated,
    onDismiss = onDismiss
)

@Composable
fun UpdateTaskDialog(
    todoTask: TodoTask,
    onTaskUpdated: (TodoTask) -> Unit,
    onDismiss: () -> Unit
) = TaskDialog(
    task = todoTask,
    label = "Edit ToDo",
    onSubmit = onTaskUpdated,
    onDismiss = onDismiss
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDialog(
    task: TodoTask?,
    label: String,
    onSubmit: (TodoTask) -> Unit,
    onDismiss: () -> Unit
) {
    var taskTitle by remember { mutableStateOf(task?.title ?: "") }
    var taskDescription by remember { mutableStateOf(task?.description ?: "") }
    var taskPriority by remember { mutableStateOf(task?.priority ?: TaskPriority.None.priority) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            Column {
                TextField(
                    value = taskTitle,
                    onValueChange = { newValue -> taskTitle = newValue },
                    label = { Text("Task Title") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = taskDescription,
                    onValueChange = { newValue -> taskDescription = newValue },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }) {
                    TextField(
                        value = TaskPriority.getByValue(taskPriority).name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false })
                    {
                        TaskPriority.values().forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.name) },
                                onClick = {
                                    taskPriority = priority.ordinal
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onSubmit(
                            task?.copy(
                                title = taskTitle,
                                description = taskDescription,
                                priority = taskPriority
                            ) ?: TodoTask(
                                title = taskTitle,
                                description = taskDescription,
                                priority = taskPriority
                            )
                        )
                        onDismiss()
                    }
                }
            ) {
                Text("Submit!")
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
fun DeleteAllTasksDialog(
    onTasksDeleted: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete All Tasks") },
        text = { Text("Are you sure you want to delete all tasks?") },
        confirmButton = {
            Button(
                onClick = {
                    onTasksDeleted()
                    onDismiss()
                }
            ) {
                Text("Yes!")
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
fun DeleteTaskDialog(
    todoTask: TodoTask,
    onTaskDeleted: (TodoTask) -> Unit,
    onDismiss: () -> Unit
) = DeleteDialog(
    task = todoTask,
    label = "Delete ToDo",
    onSubmit = onTaskDeleted,
    onDismiss = onDismiss
)


@Composable
fun DeleteDialog(
    task: TodoTask?,
    label: String,
    onSubmit: (TodoTask) -> Unit,
    onDismiss: () -> Unit
) {
    var taskTitle by remember { mutableStateOf(task?.title ?: "") }
    var taskDescription by remember { mutableStateOf(task?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            Text(text =  "Are you sure you want to delete  \"${task?.title ?: "this"}\"  task?")
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onSubmit(task?.copy(title = taskTitle, description = taskDescription) ?: TodoTask(title = taskTitle, description = taskDescription))
                        onDismiss()
                    }
                }
            ) {
                Text("Yes!")
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

