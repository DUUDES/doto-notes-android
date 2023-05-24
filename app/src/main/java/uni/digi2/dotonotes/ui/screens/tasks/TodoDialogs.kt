package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import uni.digi2.dotonotes.data.tasks.TodoTask
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.TimeZone

fun createTomorrowDateWithTime(): LocalDateTime {
    val currentTime = LocalDateTime.now()
    return currentTime.plus(1, ChronoUnit.DAYS)
}


@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class)
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


@ExperimentalMaterial3Api
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
    var taskDeadline by remember { mutableStateOf(createTomorrowDateWithTime()) }

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
                ){ snappedDateTime -> taskDeadline = snappedDateTime }
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
                                priority = taskPriority,
                                dueTo = Date.from(taskDeadline.atZone(ZoneId.systemDefault()).toInstant())
                            ) ?: TodoTask(
                                title = taskTitle,
                                description = taskDescription,
                                priority = taskPriority,
                                dueTo = Date.from(taskDeadline.atZone(ZoneId.systemDefault()).toInstant())
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
