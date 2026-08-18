package com.android.basiccleanarchitecture.core.result

sealed interface Result<out T, out E : Throwable> {
    data class Success<out T>(val data: T) : Result<T, Nothing>
    data class Failure<out E : Throwable>(val error: E) : Result<Nothing, E>

    inline fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (E) -> R
    ): R = when (this) {
        is Success -> onSuccess(data)
        is Failure -> onFailure(error)
    }
}
