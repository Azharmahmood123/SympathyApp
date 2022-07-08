package com.astadevs.sympathyapp.base

/**
 * Generic class for holding success response, error response and loading status
 */

sealed class Resource<out T> {
    object Loading: Resource<Nothing>()
    data class Success<out T>(val data: T?) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}