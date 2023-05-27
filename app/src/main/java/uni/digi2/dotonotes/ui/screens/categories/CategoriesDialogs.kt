package uni.digi2.dotonotes.ui.screens.categories

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uni.digi2.dotonotes.data.categories.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoriesDialog(
    onCategoryCreated: (Category) -> Unit,
    onDismiss: () -> Unit
) = CategoryDialog(
    category = null,
    label = "Create Category",
    onSubmit = onCategoryCreated,
    onDismiss = onDismiss
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateCategoriesDialog(
    category: Category,
    onTaskUpdated: (Category) -> Unit,
    onDismiss: () -> Unit
) = CategoryDialog(
    category = category,
    label = "Edit Category",
    onSubmit = onTaskUpdated,
    onDismiss = onDismiss
)


@ExperimentalMaterial3Api
@Composable
fun CategoryDialog(
    category: Category?,
    label: String,
    onSubmit: (Category) -> Unit,
    onDismiss: () -> Unit
) {
    var categoryName by remember { mutableStateOf(category?.name ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(label) },
        text = {
            Column {
                TextField(
                    value = categoryName,
                    onValueChange = { newValue -> categoryName = newValue },
                    label = { Text("Category Title") }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (categoryName.isNotBlank()) {
                        onSubmit(
                            category?.copy(
                                name = categoryName
                            ) ?: Category(
                                name = categoryName
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