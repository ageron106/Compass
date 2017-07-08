package com.example.dmitry.compass.model

import com.example.dmitry.compass.core.LocationPoint
import com.example.dmitry.compass.network.dto.BarDto

/**
 * Created by dmitry on 6/12/17.
 */
class BarModel(val dto: BarDto) {
    val priceLevel = dto.price_level
    val rating = dto.rating
    val name = dto.name
    val location: LocationPoint

    init {
        val lat = dto.geometry?.location?.lat
        val lng = dto.geometry?.location?.lng
        location = LocationPoint(lat!!, lng!!)
    }

    fun getDistance(locationPoint: LocationPoint): Int {
        return location.getDistance(locationPoint).toInt()
    }
}