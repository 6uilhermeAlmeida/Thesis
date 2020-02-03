package com.example.coroutineskit.location

import android.location.Address
import android.location.Location
import com.example.kitprotocol.location.AddressRepository
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun getAddressesSuspending(
    addressRepository: AddressRepository, location: Location, maxResults: Int
) = suspendCoroutine<List<Address>> { cont ->

    addressRepository.getAddresses(location.longitude, location.latitude, maxResults) { addresses, throwable ->
        addresses?.let { cont.resume(it) }
            ?: throwable?.let { cont.resumeWithException(it) }
            ?: cont.resumeWithException(IllegalStateException("At least one value must be not null"))
    }
}