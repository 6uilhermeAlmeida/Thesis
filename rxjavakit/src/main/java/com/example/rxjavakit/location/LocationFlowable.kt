package com.example.rxjavakit.location

import android.location.Location
import android.os.Looper
import com.example.kitprotocol.throwable.LocationProviderNotAvailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable

fun getLocationFlowable(
    locationServiceClient: FusedLocationProviderClient,
    locationRequest: LocationRequest
): Flowable<Location> = Flowable.create({ emitter ->

    var locationCheck = false

    val callback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {

            // Emit the received location
            emitter.onNext(result.locations.first())
        }

        override fun onLocationAvailability(availability: LocationAvailability) {

            // This callback will randomly receive a false signal, for the purpose we only need the initial signal.
            // In case the location is not available when starting the search
            // this will propagate a [LocationProviderNotAvailableException]

            if (!locationCheck) {
                if (!availability.isLocationAvailable) emitter.onError(LocationProviderNotAvailableException())
                locationCheck = true
            }
        }
    }

    locationServiceClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

    emitter.setCancellable { locationServiceClient.removeLocationUpdates(callback) }

}, BackpressureStrategy.LATEST)
