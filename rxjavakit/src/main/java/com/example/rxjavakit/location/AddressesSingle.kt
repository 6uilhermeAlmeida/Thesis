package com.example.rxjavakit.location

import android.location.Address
import android.location.Location
import com.example.kitprotocol.location.AddressRepository
import io.reactivex.Single

internal fun getAddresses(addressRepository: AddressRepository, location: Location, maxResults: Int) =
    Single.create<List<Address>> { emitter ->
        addressRepository.getAddresses(location.longitude, location.latitude, maxResults) { addresses, throwable ->
            if (addresses != null) {
                emitter.onSuccess(addresses)
            } else {
                val exception = checkNotNull(throwable) { "If the result is null, the exception cannot be." }
                emitter.onError(exception)
            }
        }
    }