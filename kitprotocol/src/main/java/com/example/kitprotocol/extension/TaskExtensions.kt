package com.example.kitprotocol.extension

import com.google.android.gms.tasks.Task
import io.reactivex.Single
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun <T> Task<T?>.suspend(): T = suspendCoroutine { continuation ->

    addOnSuccessListener { result ->
        result?.let { continuation.resume(it) } ?: continuation.resumeWithException(IllegalArgumentException())
    }

    addOnFailureListener { continuation.resumeWithException(it) }
}

fun <T> Task<T?>.single(): Single<T> = Single.create<T> { emitter ->

    addOnSuccessListener { result ->
        result?.let { emitter.onSuccess(it) } ?: emitter.onError(IllegalArgumentException())
    }

    addOnFailureListener { emitter.onError(it) }
}
