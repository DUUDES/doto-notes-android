package uni.digi2.dotonotes.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import uni.digi2.dotonotes.data.categories.CategoriesDao
import uni.digi2.dotonotes.data.categories.TaskCategory
import uni.digi2.dotonotes.ui.screens.tasks.CreateTaskDialog
import uni.digi2.dotonotes.ui.screens.tasks.TodoTaskItem
import uni.digi2.dotonotes.ui.screens.tasks.UpdateTaskDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesListScreen(viewModel: TaskCategoriesViewModel = TaskCategoriesViewModel(CategoriesDao())) {
    val categories by viewModel.categories.collectAsState()

    val showCreateDialog = remember { mutableStateOf(false) }
    val showEditDialog = remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Categories List", style = MaterialTheme.typography.headlineLarge)
                },
                modifier = Modifier.background(color = MaterialTheme.colorScheme.primary)
            )
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
            it.calculateBottomPadding()
            Column {
                Spacer(modifier = Modifier.height(48.dp))
                LazyColumn {
                    items(categories) { category ->
                        TaskCategoryItem(
                            category = category,
                            onCategoryUpdate = { updatedCategory ->
                                auth.currentUser?.let { user ->
                                    viewModel.updateCategory(user.uid, updatedCategory)
                                }
                            },
                            showEditDialog = { showEditDialog.value = category.id },
                            onCategoryDelete = { deletedCategory ->
                                auth.currentUser?.let { user ->
                                    viewModel.deleteCategory(user.uid, deletedCategory.id)
                                }
                            }
                        )
                    }
                }
            }

            if (showCreateDialog.value) {
                CreateCategoriesDialog(
                    onCategoryCreated = { category ->
                        auth.currentUser?.let { user ->
                            viewModel.addCategory(
                                user.uid,
                                category
                            )
                        }
                    },
                    onDismiss = { showCreateDialog.value = false }
                )
            } else if (showEditDialog.value != "" && categories.any { category -> category.id == showEditDialog.value }) {
                UpdateCategoriesDialog(
                    categories.first { category -> category.id == showEditDialog.value },
                    onTaskUpdated = { category ->
                        auth.currentUser?.let { user ->
                            viewModel.updateCategory(
                                user.uid,
                                category
                            )
                        }
                    },
                    onDismiss = { showEditDialog.value = "" }
                )
            }
        }


    )

}