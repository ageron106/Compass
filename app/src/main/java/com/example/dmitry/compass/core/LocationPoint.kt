package com.example.dmitry.compass.core

import android.location.Location

/**
 * Created by dmitry on 6/11/17.
 */
class LocationPoint(var lat: Double = 0.0, var lng: Double = 0.0) {
    val location: Location= Location("")
    init {
        location.latitude = lat
        location.longitude = lng
    }

    fun getDistance(point: LocationPoint) : Float{
        return point.location.distanceTo(location)
    }

}