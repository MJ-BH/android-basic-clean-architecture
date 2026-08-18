package com.android.basiccleanarchitecture.data.api

import com.android.basiccleanarchitecture.core.result.Result
import com.android.basiccleanarchitecture.data.dto.FileItemDto
import kotlinx.coroutines.delay

class FakeExplorerApi {
    private val mockDatabase = mutableListOf(
        FileItemDto("f1", "Documents", "folder", 0, "2026-08-16T10:00:00Z", null),
        FileItemDto("f2", "Android_Design_Tokens", "folder", 0, "2026-08-15T14:30:00Z", null),
        FileItemDto("f3", "Kotlin_Clean_Architecture.pdf", "pdf", 3200000, "2026-08-17T09:15:00Z", null),
        FileItemDto("f4", "Koin_DI_Setup.doc", "document", 180000, "2026-08-17T11:20:00Z", null),
        FileItemDto("f1_1", "Jetpack_Compose_v2.pdf", "pdf", 4500000, "2026-08-18T01:00:00Z", "f1"),
        FileItemDto("f1_2", "Ktor_Auth_Interceptor.doc", "document", 220000, "2026-08-18T01:10:00Z", "f1")
    )

    suspend fun fetchItems(folderId: String?): Result<List<FileItemDto>, Throwable> {
        delay(400)
        return try {
            val items = mockDatabase.filter { it.parentId == folderId }
            Result.Success(items)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun createFolder(name: String, parentId: String?): Result<FileItemDto, Throwable> {
        delay(300)
        return try {
            val newFolder = FileItemDto(
                id = "folder_${System.currentTimeMillis()}",
                name = name,
                type = "folder",
                sizeInBytes = 0,
                lastModified = "2026-08-18T02:00:00Z",
                parentId = parentId
            )
            mockDatabase.add(newFolder)
            Result.Success(newFolder)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun deleteItem(id: String): Result<Boolean, Throwable> {
        delay(250)
        return try {
            mockDatabase.removeAll { it.id == id || it.parentId == id }
            Result.Success(true)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}
