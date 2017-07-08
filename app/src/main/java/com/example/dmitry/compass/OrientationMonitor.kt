package com.example.dmitry.compass

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.dmitry.compass.core.IMonitor
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by dmitry on 7/8/17.
 */
class OrientationMonitor(activity: Activity) : IMonitor {
    val sensorChanged: BehaviorSubject<SensorEvent> = BehaviorSubject.create()
    val sensorManager: SensorManager by lazy {
        activity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
//             get the angle around the z-axis rotated
            sensorChanged.onNext(event)
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // not in use
        }
    }
    @SuppressLint("MissingPermission")
    override fun start(){
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME)
    }

    override fun stop(){
        sensorManager.unregisterListener(sensorListener)
    }

    fun  onPointFound(): BehaviorSubject<SensorEvent> {return sensorChanged}

}