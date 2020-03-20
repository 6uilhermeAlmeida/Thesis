package com.example.coroutineskit.location

import android.location.Location
import android.os.Looper
import com.example.kitprotocol.throwable.LocationProviderNotAvailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

internal fun getLocationUpdates(locationClient: FusedLocationProviderClient, locationRequest: LocationRequest) =
    callbackFlow<Location> {

        var locationCheck = false

        val callback = object : LocationCallback() {

            override fun onLocationResult(result: LocationResult) {

                // Emit the received location
                offer(result.locations.first())
            }

            override fun onLocationAvailability(availability: LocationAvailability) {

                // This callback will randomly receive a false signal, for the purpose we only need the initial signal.
                // In case the location is not available when starting the search
                // this will propagate a [LocationProviderNotAvailableException]

                if (!locationCheck) {
                    if (!availability.isLocationAvailable) close(LocationProviderNotAvailableException())
                    locationCheck = true
                }
            }
        }

        locationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

        awaitClose { locationClient.removeLocationUpdates(callback) }
    }.conflate()