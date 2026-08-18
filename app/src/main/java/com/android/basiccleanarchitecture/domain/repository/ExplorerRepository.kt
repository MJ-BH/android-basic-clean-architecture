package com.android.basiccleanarchitecture.domain.repository

import com.android.basiccleanarchitecture.core.result.Result
import com.android.basiccleanarchitecture.domain.model.FileItem

interface ExplorerRepository {
    suspend fun getItems(folderId: String?): Result<List<FileItem>, Throwable>
    suspend fun createFolder(name: String, parentId: String?): Result<FileItem, Throwable>
    suspend fun deleteItem(id: String): Result<Boolean, Throwable>
}
