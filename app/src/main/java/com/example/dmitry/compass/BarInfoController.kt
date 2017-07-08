package com.example.dmitry.compass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bluelinelabs.conductor.Controller
import com.squareup.picasso.Picasso

/**
 * Created by dmitry on 7/8/17.
 */
class BarInfoController() : Controller() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.view_bar_info, container, false)
        val  barImage: ImageView? = view.findViewById(R.id.bar_image)
        var pictureUrl = "https://cdn-tp1.mozu.com/9046-11441/cms/11441/files/0a1a12bf-8d95-4f40-baab-ff1e1039d071?max=300"

        Picasso.with(activity).load(pictureUrl).into(barImage)
        return view
    }
}