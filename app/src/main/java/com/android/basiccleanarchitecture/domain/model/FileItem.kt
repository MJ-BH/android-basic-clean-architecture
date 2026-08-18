package com.android.basiccleanarchitecture.domain.model

enum class FileItemType {
    FOLDER, DOCUMENT, IMAGE, PDF, ARCHIVE
}

data class FileItem(
    val id: String,
    val name: String,
    val type: FileItemType,
    val sizeInBytes: Long,
    val lastModified: String,
    val parentId: String? = null
) {
    val isFolder: Boolean get() = type == FileItemType.FOLDER
}
