package com.example.asynctaskkit.repository

import com.example.kitprotocol.repository.KitRepository

class AsyncTaskRepository : KitRepository{
    override val message: String
        get() = "Hello from AsyncTasks"

}