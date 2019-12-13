package com.example.coroutineskit.repository

import com.example.kitprotocol.repository.KitRepository

class CoroutinesRepository : KitRepository{
    override val message: String
        get() = "Hello from Coroutines"

}