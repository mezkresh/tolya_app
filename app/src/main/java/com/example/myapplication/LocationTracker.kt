package com.example.myapplication

import android.app.Service
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog


class LocationTracker(context: Context) : Service(), LocationListener {
    private val con: Context
    var isGPSOn = false
    var isNetWorkEnabled = false
    var isLocationEnabled = false
    var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0
    var locationManager: LocationManager? = null
    var onCampus = false

    private fun checkIfLocationAvailable(): Location? {
        try {
            locationManager = con.getSystemService(LOCATION_SERVICE) as LocationManager
            isGPSOn = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetWorkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSOn && !isNetWorkEnabled) {
                isLocationEnabled = false
                Toast.makeText(con, "No location provider available!", Toast.LENGTH_SHORT).show()
                askToOnLocation()
            }
            if (isGPSOn || isNetWorkEnabled) {
                isLocationEnabled = true
                // if network location is available request location update
                try {
                    if (isNetWorkEnabled) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_FOR_UPDATES,
                            MIN_DISTANCE_TO_REQUEST_LOCATION.toFloat(),
                            this
                        )
                        if (locationManager != null) {
                            location =
                                locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            location?.let {
                                latitude = it.latitude
                                longitude = it.longitude
                            }
                        }
                    }
                    if (isGPSOn) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_FOR_UPDATES,
                            MIN_DISTANCE_TO_REQUEST_LOCATION.toFloat(),
                            this
                        )
                        if (locationManager != null) {
                            location =
                                locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            location?.let {
                                latitude = it.latitude
                                longitude = it.longitude

                            }
                        }
                    }
                } catch (e: SecurityException) {
                }
            }
        } catch (e: Exception) {
        }
        return location
    }

    fun stopUsingLocation() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@LocationTracker)
        }
    }

    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude
    }

    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude
    }

    fun askToOnLocation() {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(con)
        dialog.setTitle("Settings")
        dialog.setMessage("Location disabled, please enable through settings.")
        dialog.setPositiveButton("Settings") { dialog, which ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            con.startActivity(intent)
        }
        dialog.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }
        dialog.show()
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        if (!(location.longitude <= Constants.uoitLongitude + 0.0040 && location.longitude >= Constants.uoitLongitude - 0.0040)) {
            if (!(location.latitude <= Constants.uoitLatitude + 0.0040 && location.latitude >= Constants.uoitLatitude - 0.0040)) {
                Constants.stayedOnCampus = false
                Constants.countDownTimer?.onFinish()
            }
        }
    }

    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val MIN_DISTANCE_TO_REQUEST_LOCATION: Long = 1 // in meters
        private const val MIN_TIME_FOR_UPDATES = (1000 * 1 // 1 sec
                ).toLong()
    }

    init {
        con = context
        checkIfLocationAvailable()
    }
}