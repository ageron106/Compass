package com.example.dmitry.compass.network

import com.example.dmitry.compass.network.dto.BarsDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by dmitry on 6/11/17.
 */
interface GooglePlacesRequests {
//    https://maps.googleapis.com/maps/api/place/nearbysearch/json
// ?location=55.741453,37.628418
// &type=bar&radius=50
// &opennow=true
// &key=AIzaSyA5rbx7kV-wcHYLqef3BpWTf8YiiVc6GF8


//    https://maps.googleapis.com/maps/api/place/photo?maxwidth=400
    // &photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU
    // &key=AIzaSyA5rbx7kV-wcHYLqef3BpWTf8YiiVc6GF8
    @GET("/maps/api/place/nearbysearch/json")
    fun searchPlaces(
        @Query("location") location: String,
        @Query("type") type: String,
        @Query("radius") radius: Int,
        @Query("opennow") opennow: Boolean,
        @Query("key") key: String): Call<BarsDto>
}