package com.umer.networklogger

data class PostModel(
    val title: String,
    val body: String,
    val userId: Int,
    val id: Int? = null,

    )