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
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.runBlocking
import uni.digi2.dotonotes.R
import java.util.Date
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
                    text = stringResource(id = R.string.task_description) + ": ",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.title)  + ": "+ task.title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.description) + ": " + task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.priority) + ": "+ TaskPriority.getByValue(task.priority).toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.category) +": ${
                        taskCategory.let { id ->
                            categories.firstOrNull { category -> category.id == id }
                        }?.name ?: "None"
                    }",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.created_on) + ": " + task.createdOn.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.checked_on) +": ${task.checkedOn?.toString() ?: ""}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.due_to) + ": ${task.dueTo?.toString() ?: ""}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )
                Text(
                    text = stringResource(id = R.string.is_completed) + ": " + task.completed.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp)
                )

            }
        }
    )

}


        
