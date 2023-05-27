package uni.digi2.dotonotes.ui.screens.tasks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(viewModel: TodoViewModel) {
    val task = viewModel.selectedTask.value!!
    val taskCategory by remember { mutableStateOf(task.categoryId) }
    val categories =
        runBlocking { viewModel.getCategories(FirebaseAuth.getInstance().currentUser!!.uid) }

    Scaffold(
        content = {
            it.calculateBottomPadding()
            Column {
                Text(
                    text = "Task description",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Title: " + task.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Description: " + task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Priority: " + TaskPriority.getByValue(task.priority).toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Category: ${
                        taskCategory.let { id ->
                            categories.firstOrNull { category -> category.id == id }
                        }?.name ?: "None"
                    }",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Created on: " + task.createdOn.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Checked on: ${task.checkedOn?.toString() ?: ""}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Due to: ${task.dueTo?.toString() ?: ""}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = "Is completed: " + task.completed.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

            }
        }
    )

}


        
