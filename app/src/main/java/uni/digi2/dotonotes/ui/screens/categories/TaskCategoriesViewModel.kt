package uni.digi2.dotonotes.ui.screens.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uni.digi2.dotonotes.data.categories.Category
import uni.digi2.dotonotes.data.categories.ICategoriesDao

class TaskCategoriesViewModel(private val categoriesDao: ICategoriesDao) : ViewModel() {
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            getCategories(user.uid)
            viewModelScope.launch {
                categoriesDao.observeCategoriesRealtime(user.uid)
                    .collect {
                        _categories.value = it
                    }
            }
        }
    }

    private fun getCategories(userId: String) {
        viewModelScope.launch {
            _categories.value = categoriesDao.getCategories(userId)
        }
    }

    fun stopObservation() = viewModelScope.launch { categoriesDao.stopObservation() }

    fun addCategory(userId: String, category: Category) {
        viewModelScope.launch {
            categoriesDao.addCategory(userId, category)
            getCategories(userId)
        }
    }

    fun updateCategory(userId: String, category: Category) {
        viewModelScope.launch {
            categoriesDao.updateCategory(userId, category)
            getCategories(userId)
        }
    }

    fun deleteCategory(userId: String, categoryId: String) {
        viewModelScope.launch {
            categoriesDao.deleteCategory(userId, categoryId)
            getCategories(userId)
        }
    }

}