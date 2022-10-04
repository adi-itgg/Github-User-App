package me.phantomx.githubuserapp.sealed

sealed class ApiResponse {
    class Success<T>(val data: T): ApiResponse()
    class Message(val title: String, val content: String): ApiResponse()
    class Error(val message: String): ApiResponse()
}
