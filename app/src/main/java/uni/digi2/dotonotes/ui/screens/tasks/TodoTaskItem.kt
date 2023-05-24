package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uni.digi2.dotonotes.data.tasks.TodoTask
import java.util.Date

enum class TaskPriority(val priority: Int) {
    None(100),
    High(1),
    Medium(2),
    Low(3),;

    companion object {
        private val VALUES = values()
        fun getByValue(value: Int) = VALUES.firstOrNull { it.priority == value } ?: None
    }
}

enum class TaskPriorityColor(val rgb: Color, val priority: TaskPriority) {
        Gray(Color.LightGray.copy(alpha = 0.35f), TaskPriority.None),
        Red(Color.Red.copy(alpha = 0.35f), TaskPriority.High),
        Yellow(Color.Yellow.copy(alpha = 0.35f), TaskPriority.Medium),
        Green(Color.Green.copy(alpha = 0.35f), TaskPriority.Low);

        companion object {
        private val VALUES = TaskPriorityColor.values()
        fun getByPriority(value: TaskPriority): TaskPriorityColor =
            VALUES.firstOrNull { it.priority == value } ?: Gray
    }
}

@Composable
fun TodoTaskItem(
    task: TodoTask,
    onTaskUpdate: (TodoTask) -> Unit,
    showDeleteDialog: () -> Unit,
    showEditDialog: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .size(16.dp)
                .border(width = 1.2.dp, color = Color.DarkGray, shape = CircleShape)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(color = TaskPriorityColor.getByPriority(TaskPriority.getByValue(task.priority)).rgb)
        )
        Checkbox(
            modifier = Modifier.scale(scale = 1.2f),
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
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = showEditDialog
        ) {
            Icon(
                Icons.Default.Edit,
                modifier = Modifier.size(32.dp),
                contentDescription = "Edit Task"
            )
        }
        IconButton(
            onClick = showDeleteDialog
        ) {
            Icon(
                Icons.Default.Delete,
                modifier = Modifier.size(32.dp),
                contentDescription = "Delete Task"
            )
        }
    }
}

