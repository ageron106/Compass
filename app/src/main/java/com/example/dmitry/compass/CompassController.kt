package com.example.dmitry.compass

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.SensorEvent
import android.support.v13.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.example.dmitry.compass.core.LocationPoint
import com.example.dmitry.compass.model.BarModel
import com.example.dmitry.compass.network.GooglePlacesRequests
import com.example.dmitry.compass.network.dto.BarsDto
import com.example.dmitry.compass.opengl.MyGLSurfaceView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Created by dmitry on 7/8/17.
 */
class CompassController : Controller() {


    lateinit var inflater: LayoutInflater
    lateinit var container: ViewGroup
    val key = "AIzaSyA5rbx7kV-wcHYLqef3BpWTf8YiiVc6GF8"
    var bestBar: BarModel? = null
    private var mGLView: MyGLSurfaceView? = null
    private var currentDegreeZ = 0f
    private var currentDelta = 0f
    var barName: TextView? = null
    var barInfoController = BarInfoController()

    var compassView: FrameLayout? = null
    val orientationMonitor: OrientationMonitor by lazy {
        OrientationMonitor(activity!!)
    }
    val locationMonitor: LocationMonitor by lazy {
        LocationMonitor(activity!!)
    }

    private val PERMISSION_REQUEST_CODE_LOCATION = 1
    @SuppressLint("MissingPermission")
    override fun onCreateView(_inflater: LayoutInflater, _container: ViewGroup): View {
        inflater = _inflater
        container = _container
        return createView();
    }

    private fun startLocationManager() {
        locationMonitor.start()
        locationMonitor
                .onPointFound()
                .subscribe({ point ->
                    run {
                        barInfoController.lastFondLocation = point
                        searchPlaces(point)
                    }
                })
    }

    private fun startOrientationManager() {
        orientationMonitor.start()
        orientationMonitor
                .onOrientationChanged()
                .subscribe({ event -> redrawCompass(event) })
    }

    private fun redrawCompass(event: SensorEvent): Boolean? {
        val values = event.values
        var degreeZ = Math.round(values[0]).toFloat()
        var degreeX = Math.round(values[1]).toFloat()
        var degreeY = Math.round(values[2]).toFloat()
        currentDegreeZ = 180 + degreeZ + currentDelta
        return mGLView?.rotateTo(180 + degreeX, -180 - degreeY, currentDegreeZ)
    }

    override fun onDestroyView(view: View) {

        locationMonitor.stop()
    }

    private fun searchPlaces(point: LocationPoint) {
        val gson = GsonBuilder()
                .setLenient()
                .create()
        val retrofit = Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/") //Базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create(gson)) //Конвертер, необходимый для преобразования JSON'а в объекты
                .build()
        val googlePlacesRequests = retrofit.create(GooglePlacesRequests::class.java)
        val value: Callback<BarsDto> = object : Callback<BarsDto> {
            override fun onResponse(call: Call<BarsDto>, response: Response<BarsDto>) {
                val bars = response.body().results.map { dto -> BarModel(dto) }
                if (bars.isEmpty()) return
                bestBar = bars
                        .sortedWith(compareByDescending<BarModel> { it.rating }
                                .thenBy { it.priceLevel })
                        .get(0)
                setHeader(bestBar?.name)
                changeDirection(point, bestBar?.location)
                barName?.setOnClickListener {
                    if (barInfoController.isDestroyed) {
                        barInfoController = BarInfoController()
                    }
                    barInfoController.bestBar = bestBar
                    router.pushController(RouterTransaction.with(barInfoController))
                }
            }

            override fun onFailure(call: Call<BarsDto>, t: Throwable) {
                print(t)
            }
        }

        googlePlacesRequests
                .searchPlaces(String.format("%s,%s", point?.lat, point?.lng), "bar", 1000, true, key)
                .enqueue(value)
    }

    private fun setHeader(header: String?) {
        barName?.text = header
    }

    private fun changeDirection(fromLocation: LocationPoint, toLocation: LocationPoint?) {
        val from = fromLocation
        val to = toLocation ?: return
        val y = (from.lat - to.lat)
        val x = (from.lng - to.lng)
        var delta = 0
        var degree = 0f
        when {
            x >= 0 && y >= 0 -> delta = 90
            x >= 0 && y < 0 -> delta = 0
            x < 0 && y >= 0 -> delta = 180
            x < 0 && y < 0 -> delta = 270
        }
        when {
            x >= 0 && y >= 0 -> degree = Math.abs(Math.toDegrees(Math.acos(Math.abs(x) / Math.sqrt(y * y + x * x))).toFloat())
            x >= 0 && y < 0 -> degree = Math.abs(Math.toDegrees(Math.acos(Math.abs(y) / Math.sqrt(y * y + x * x))).toFloat())
            x < 0 && y >= 0 -> degree = Math.abs(Math.toDegrees(Math.acos(Math.abs(y) / Math.sqrt(y * y + x * x))).toFloat())
            x < 0 && y < 0 -> degree = Math.abs(Math.toDegrees(Math.acos(Math.abs(x) / Math.sqrt(y * y + x * x))).toFloat())
        }
        currentDelta = degree + delta
    }

    fun requestPermission(strPermission: String, perCode: Int, _a: Activity) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(_a, strPermission)) {
            Toast.makeText(applicationContext, "GPS permission allows us to access location data. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show()
        } else {

            ActivityCompat.requestPermissions(_a, arrayOf(strPermission), perCode)
        }
    }

    fun checkPermission(strPermission: String, _a: Activity): Boolean {
        val result = ContextCompat.checkSelfPermission(_a, strPermission)
        if (result == PackageManager.PERMISSION_GRANTED) {

            return true

        } else {

            return false

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            PERMISSION_REQUEST_CODE_LOCATION -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                createView()

            } else {

                Toast.makeText(applicationContext, "Permission Denied, You cannot access location data.", Toast.LENGTH_LONG).show()

            }
        }
    }

    private fun createView(): View {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, activity!!)) {
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, activity!!);
        }
        val view = inflater.inflate(R.layout.view_compass, container, false)
        compassView = view!!.findViewById(R.id.compass)
        barName = view!!.findViewById(R.id.tvHeading)

        mGLView = MyGLSurfaceView(activity)
        compassView?.addView(mGLView)

        startOrientationManager()
        startLocationManager()
        return view
    }


}