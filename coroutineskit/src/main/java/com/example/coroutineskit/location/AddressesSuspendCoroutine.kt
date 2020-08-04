package com.example.coroutineskit.location

import android.location.Address
import android.location.Location
import com.example.kitprotocol.location.AddressRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal suspend fun getAddresses(addressRepository: AddressRepository, location: Location, maxResults: Int) =
    suspendCancellableCoroutine<List<Address>> { continuation ->
        addressRepository.getAddresses(location.longitude, location.latitude, maxResults) { addresses, throwable ->
            if (addresses != null) {
                continuation.resume(addresses)
            } else {
                val exception = checkNotNull(throwable) { "If the result is null, the exception cannot be." }
                continuation.resumeWithException(exception)
            }
        }
    }