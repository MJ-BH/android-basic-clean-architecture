package com.android.basiccleanarchitecture.data.repository

import com.android.basiccleanarchitecture.core.result.Result
import com.android.basiccleanarchitecture.data.api.FakeExplorerApi
import com.android.basiccleanarchitecture.data.mapper.FileItemMapper
import com.android.basiccleanarchitecture.domain.model.FileItem
import com.android.basiccleanarchitecture.domain.repository.ExplorerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ExplorerRepositoryImpl(
    private val api: FakeExplorerApi,
    private val mapper: FileItemMapper
) : ExplorerRepository {

    override suspend fun getItems(folderId: String?): Result<List<FileItem>, Throwable> {
        return withContext(Dispatchers.IO) {
            when (val result = api.fetchItems(folderId)) {
                is Result.Success -> Result.Success(mapper.mapToDomainList(result.data))
                is Result.Failure -> Result.Failure(result.error)
            }
        }
    }

    override suspend fun createFolder(name: String, parentId: String?): Result<FileItem, Throwable> {
        return withContext(Dispatchers.IO) {
            when (val result = api.createFolder(name, parentId)) {
                is Result.Success -> Result.Success(mapper.mapToDomain(result.data))
                is Result.Failure -> Result.Failure(result.error)
            }
        }
    }

    override suspend fun deleteItem(id: String): Result<Boolean, Throwable> {
        return withContext(Dispatchers.IO) {
            api.deleteItem(id)
        }
    }
}
