package com.example.dmitry.compass.network.dto

/**
 * Created by dmitry on 6/11/17.
 */
class BarDto {
    var geometry: GeometryDto? = null
    var name: String? = null
    var rating: Float? = null
    var price_level: Int? = null
    var photos: ArrayList<PhotosDto> = ArrayList()

}
