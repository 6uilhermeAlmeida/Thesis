package com.example.kitprotocol.location

import android.content.Context
import android.location.Address
import android.location.Geocoder

class AddressRepository(context: Context) {

    private val geoCoder = Geocoder(context)

    fun getAddresses(
        longitude: Double,
        latitude: Double,
        maxResults: Int,
        block: (List<Address>?, Throwable?) -> Unit
    ) {
        try {
            val addresses = geoCoder.getFromLocation(latitude, longitude, maxResults)
            block(addresses, null)
        } catch (t: Throwable) {
            block(null, t)
        }
    }
}