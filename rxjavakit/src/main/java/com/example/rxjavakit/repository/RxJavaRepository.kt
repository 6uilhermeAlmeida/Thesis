package com.example.rxjavakit.repository

import com.example.kitprotocol.repository.KitRepository

class RxJavaRepository : KitRepository{
    override val message: String
        get() = "Hello from RxJava"

}