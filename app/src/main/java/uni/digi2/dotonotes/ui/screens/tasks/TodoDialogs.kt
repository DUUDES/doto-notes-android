package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import uni.digi2.dotonotes.data.categories.Category
import uni.digi2.dotonotes.data.tasks.Task
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date

fun createTomorrowDateWithTime(): LocalDateTime {
    val currentTime = LocalDateTime.now()
    return currentTime.plus(1, ChronoUnit.DAYS)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    onTaskCreated: (Task) -> Unit,
    onDismiss: () -> Unit,
    categories: List<Category>
) = TaskDialog(
    task = null,
    label = "Create ToDo",
    onSubmit = onTaskCreated,
    onDismiss = onDismiss,
    categories = categories
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTaskDialog(
    todoTask: Task,
    onTaskUpdated: (Task) -> Unit,
    onDismiss: () -> Unit,
    categories: List<Category>
) = TaskDialog(
    task = todoTask,
    label = "Edit ToDo",
    onSubmit = onTaskUpdated,
    onDismiss = onDismiss,
    categories = categories
)

@ExperimentalMaterial3Api
@Composable
fun TaskDialog(
    task: Task?,
    label: String,
    onSubmit: (Task) -> Unit,
    onDismiss: () -> Unit,
    categories: List<Category>
) {
    var taskTitle by remember { mutableStateOf(task?.title ?: "") }
    var taskDescription by remember { mutableStateOf(task?.description ?: "") }
    var taskPriority by remember { mutableStateOf(task?.priority ?: TaskPriority.None.priority) }
    var taskCategory by remember { mutableStateOf(task?.categoryId) }
    var priorityDropdown by remember { mutableStateOf(false) }
    var categoriesDropdown by remember { mutableStateOf(false) }
    var taskHasDeadline by remember { mutableStateOf(task?.dueTo?.let { true } ?: false) }
    var taskDeadline by remember { mutableStateOf(createTomorrowDateWithTime()) }

    AlertDialog(
        modifier = Modifier.fillMaxHeight(0.82f),
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            Column {
                TextField(
                    value = taskTitle,
                    onValueChange = { newValue -> taskTitle = newValue },
                    label = { Text("Task Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = categoriesDropdown,
                    onExpandedChange = { categoriesDropdown = !categoriesDropdown }) {
                    TextField(
                        value = taskCategory.let { id -> categories.firstOrNull { it.id == id } }?.name
                            ?: "None",
                        label = { Text("Category") },
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriesDropdown) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = categoriesDropdown,
                        onDismissRequest = { categoriesDropdown = false })
                    {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    taskCategory = category.id
                                    categoriesDropdown = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = taskDescription,
                    onValueChange = { newValue -> taskDescription = newValue },
                    label = { Text("Description") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    Checkbox(
                        checked = taskHasDeadline,
                        onCheckedChange = { taskHasDeadline = it })
                    Text("Has deadline")
                }
                Spacer(modifier = Modifier.height(8.dp))
                if (taskHasDeadline) {
                    WheelDateTimePicker(
                        startDateTime = LocalDateTime.now(),
                        minDateTime = taskDeadline,
                        maxDateTime = LocalDateTime.of(
                            2025, 10, 20, 5, 30
                        ),
                        size = DpSize(300.dp, 100.dp),
                        rowCount = 5,
                        textStyle = MaterialTheme.typography.titleSmall,
                        textColor = Color(0xFF666666),
                        selectorProperties = WheelPickerDefaults.selectorProperties(
                            enabled = true,
                            shape = RoundedCornerShape(0.dp),
                            color = Color(0xFFf1faee).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFcccccc))
                        )
                    ) { snappedDateTime -> taskDeadline = snappedDateTime }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                ExposedDropdownMenuBox(
                    expanded = priorityDropdown,
                    onExpandedChange = { priorityDropdown = !priorityDropdown }) {
                    TextField(
                        label = { Text("Priority") },
                        value = TaskPriority.getByValue(taskPriority).name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityDropdown) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = priorityDropdown,
                        onDismissRequest = { priorityDropdown = false })
                    {
                        TaskPriority.values().forEach { priority ->
                            DropdownMenuItem(
                                text = { Text(priority.name) },
                                onClick = {
                                    taskPriority = priority.ordinal
                                    priorityDropdown = false
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
                    val deadline: Date? = when (taskHasDeadline) {
                        true -> Date.from(taskDeadline.atZone(ZoneId.systemDefault()).toInstant())
                        false -> null
                    }
                    if (taskTitle.isNotBlank()) {
                        onSubmit(
                            task?.copy(
                                title = taskTitle,
                                description = taskDescription,
                                priority = taskPriority,
                                dueTo = deadline,
                                categoryId = taskCategory,
                                updatedOn = Date()
                            ) ?: Task(
                                title = taskTitle,
                                description = taskDescription,
                                priority = taskPriority,
                                dueTo = deadline,
                                categoryId = taskCategory
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
    todoTask: Task,
    onTaskDeleted: (Task) -> Unit,
    onDismiss: () -> Unit
) = DeleteDialog(
    task = todoTask,
    label = "Delete ToDo",
    onSubmit = onTaskDeleted,
    onDismiss = onDismiss
)


@Composable
fun DeleteDialog(
    task: Task?,
    label: String,
    onSubmit: (Task) -> Unit,
    onDismiss: () -> Unit
) {
    var taskTitle by remember { mutableStateOf(task?.title ?: "") }
    var taskDescription by remember { mutableStateOf(task?.description ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            Text(text = "Are you sure you want to delete  \"${task?.title ?: "this"}\"  task?")
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        onSubmit(
                            task?.copy(title = taskTitle, description = taskDescription)
                                ?: Task(title = taskTitle, description = taskDescription)
                        )
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

