package com.android.basiccleanarchitecture.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FileItemDto(
    val id: String,
    val name: String,
    val type: String,
    val sizeInBytes: Long,
    val lastModified: String,
    val parentId: String? = null
)
