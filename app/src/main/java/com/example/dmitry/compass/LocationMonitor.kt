package com.example.dmitry.compass

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.example.dmitry.compass.core.IMonitor
import com.example.dmitry.compass.core.LocationPoint
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by dmitry on 7/8/17.
 */
class LocationMonitor(activity: Activity) : IMonitor {
    val pointFound: BehaviorSubject<LocationPoint> = BehaviorSubject.create()
    val locationManager = activity.getSystemService(Activity.LOCATION_SERVICE) as LocationManager
    val locationListener: LocationListener = object : LocationListener {
        override fun onProviderDisabled(p0: String?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onLocationChanged(p0: Location?) {
            pointFound.onNext(LocationPoint(p0!!.latitude, p0.longitude))
        }
    }
    @SuppressLint("MissingPermission")
    override fun start(){
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
        locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, locationListener)
    }

    override fun stop(){
        locationManager.removeUpdates(locationListener);
    }
    fun  onPointFound(): BehaviorSubject<LocationPoint> {return pointFound}
}