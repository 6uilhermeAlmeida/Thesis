package com.example.thesis.config

sealed class Library {
    object Coroutines : Library()
    object RxJava : Library()
}