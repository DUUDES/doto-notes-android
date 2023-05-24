package uni.digi2.dotonotes.ui.screens.categories

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
import uni.digi2.dotonotes.data.categories.TaskCategory
import uni.digi2.dotonotes.ui.screens.tasks.TaskPriority
import uni.digi2.dotonotes.ui.screens.tasks.TaskPriorityColor
import java.util.Date

@Composable
fun TaskCategoryItem(
    category: TaskCategory,
    onCategoryUpdate: (TaskCategory) -> Unit,
    onCategoryDelete: (TaskCategory) -> Unit,
    showEditDialog: () -> Unit
) {
    Row(
        modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = category.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = showEditDialog
        ) {
            Icon(
                Icons.Default.Edit,
                modifier = Modifier.size(32.dp),
                contentDescription = "Edit Category"
            )
        }
        IconButton(
            onClick = { onCategoryDelete(category) }
        ) {
            Icon(
                Icons.Default.Delete,
                modifier = Modifier.size(32.dp),
                contentDescription = "Delete Category"
            )
        }
    }
}
