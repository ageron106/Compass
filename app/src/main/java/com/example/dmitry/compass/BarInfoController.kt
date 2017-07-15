package com.example.dmitry.compass

import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bluelinelabs.conductor.Controller
import com.example.dmitry.compass.core.LocationPoint
import com.example.dmitry.compass.model.BarModel
import com.squareup.picasso.Picasso

/**
 * Created by dmitry on 7/8/17.
 */
class BarInfoController : Controller() {
    var bestBar: BarModel? = null

    val key = "AIzaSyA5rbx7kV-wcHYLqef3BpWTf8YiiVc6GF8"

    var lastFondLocation: LocationPoint = LocationPoint(0.0, 0.0)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.view_bar_info, container, false)
        val barImage: ImageView? = view.findViewById(R.id.bar_image)
        val barCost: TextView? = view.findViewById(R.id.bar_cost)
        val barTitle: TextView? = view.findViewById(R.id.bar_title)
        val barDistance: TextView? = view.findViewById(R.id.bar_distance)
        val photoReference = bestBar?.getPhotoReference()
        var url = "https://cdn-tp1.mozu.com/9046-11441/cms/11441/files/0a1a12bf-8d95-4f40-baab-ff1e1039d071?max=300"
        if (!photoReference.isNullOrEmpty())
            url = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400" + "&photoreference=" + photoReference + "&key=" + key
        Picasso.with(activity).load(url).into(barImage)


        val priceLevel = bestBar?.priceLevel
        if (priceLevel == null) {
            barCost?.visibility = GONE
        } else {
            barCost?.text = priceLevel.toString()
        }
        barTitle?.text = bestBar?.name
        barDistance?.text = getDistance(bestBar!!.location, lastFondLocation)



        return view
    }

    fun getDistance(location: LocationPoint, lastFondLocation: LocationPoint): String? {
        val locationA = Location("point A")

        locationA.setLatitude(location.lat)
        locationA.setLongitude(location.lng)

        val locationB = Location("point B")

        locationB.setLatitude(lastFondLocation.lat)
        locationB.setLongitude(lastFondLocation.lng)

        return locationA.distanceTo(locationB).toString()

    }
}