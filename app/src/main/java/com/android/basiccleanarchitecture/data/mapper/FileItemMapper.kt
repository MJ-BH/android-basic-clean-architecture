package com.android.basiccleanarchitecture.data.mapper

import com.android.basiccleanarchitecture.data.dto.FileItemDto
import com.android.basiccleanarchitecture.domain.model.FileItem
import com.android.basiccleanarchitecture.domain.model.FileItemType

class FileItemMapper {
    fun mapToDomain(dto: FileItemDto): FileItem {
        return FileItem(
            id = dto.id,
            name = dto.name,
            type = parseType(dto.type),
            sizeInBytes = dto.sizeInBytes,
            lastModified = dto.lastModified,
            parentId = dto.parentId
        )
    }

    fun mapToDomainList(dtos: List<FileItemDto>): List<FileItem> {
        return dtos.map { mapToDomain(it) }
    }

    private fun parseType(typeStr: String): FileItemType {
        return when (typeStr.lowercase()) {
            "folder" -> FileItemType.FOLDER
            "pdf" -> FileItemType.PDF
            "image" -> FileItemType.IMAGE
            "archive" -> FileItemType.ARCHIVE
            else -> FileItemType.DOCUMENT
        }
    }
}
