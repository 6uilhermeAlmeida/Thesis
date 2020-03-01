package com.example.rxjavakit.location

import android.location.Address
import android.location.Location
import com.example.kitprotocol.location.AddressRepository
import io.reactivex.Single

internal fun getAddresses(addressRepository: AddressRepository, location: Location, maxResults: Int) =
    Single.create<List<Address>> { emitter ->
        addressRepository.getAddresses(location.longitude, location.latitude, maxResults) { addresses, throwable ->
            addresses?.let { emitter.onSuccess(it) }
                ?: throwable?.let { emitter.onError(it) }
                ?: emitter.onError(IllegalStateException("At least one value must be not null"))
        }
    }