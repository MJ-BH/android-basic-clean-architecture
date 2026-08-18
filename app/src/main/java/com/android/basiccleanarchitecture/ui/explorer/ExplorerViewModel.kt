package com.android.basiccleanarchitecture.ui.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.basiccleanarchitecture.domain.model.FileItem
import com.android.basiccleanarchitecture.domain.repository.ExplorerRepository
import com.android.basiccleanarchitecture.ui.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExplorerViewModel(
    private val repository: ExplorerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<FileItem>>>(UiState.Idle)
    val uiState: StateFlow<UiState<List<FileItem>>> = _uiState.asStateFlow()

    private val _currentFolderId = MutableStateFlow<String?>(null)
    val currentFolderId: StateFlow<String?> = _currentFolderId.asStateFlow()

    init {
        loadItems(null)
    }

    fun loadItems(folderId: String?) {
        _currentFolderId.value = folderId
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getItems(folderId).fold(
                onSuccess = { items ->
                    _uiState.value = if (items.isEmpty()) UiState.Empty else UiState.Success(items)
                },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to load files", error)
                }
            )
        }
    }

    fun createFolder(name: String) {
        viewModelScope.launch {
            val parentId = _currentFolderId.value
            repository.createFolder(name, parentId).fold(
                onSuccess = { loadItems(parentId) },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to create folder", error)
                }
            )
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            val parentId = _currentFolderId.value
            repository.deleteItem(id).fold(
                onSuccess = { loadItems(parentId) },
                onFailure = { error ->
                    _uiState.value = UiState.Error(error.message ?: "Failed to delete item", error)
                }
            )
        }
    }
}
