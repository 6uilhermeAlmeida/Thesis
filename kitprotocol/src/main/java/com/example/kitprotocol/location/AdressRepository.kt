package com.example.kitprotocol.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.Locale

class AddressRepository(context: Context, val mock: Boolean) {

    private val geoCoder = Geocoder(context)

    fun getAddresses(
        longitude: Double,
        latitude: Double,
        maxResults: Int,
        block: (List<Address>?, Throwable?) -> Unit
    ) {
        try {
            val mockAddress = Address(Locale.US).apply { countryCode = "US" }
            block(if (mock) listOf(mockAddress) else geoCoder.getFromLocation(latitude, longitude, maxResults), null)
        } catch (t: Throwable) {
            block(null, t)
        }
    }
}