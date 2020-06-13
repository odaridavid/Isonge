/**
 *
 * Copyright 2020 David Odari
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *            http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 **/
package com.github.odaridavid.isonge.location.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.github.odaridavid.isonge.location.ILocationClient
import com.github.odaridavid.isonge.location.ILocationManager
import com.github.odaridavid.isonge.location.ILocationObserver
import com.github.odaridavid.isonge.location.ILocationPermissionsHandler
import com.github.odaridavid.isonge.location.model.LastKnownCoordinates
import com.github.odaridavid.isonge.location.prefs.LocationPreferences
import com.google.android.gms.location.*


class LocationManager(
    private val context: Context,
    private val locationObserver: ILocationObserver,
    private val sharedPreferences: SharedPreferences,
    private val locationPermissionsHandler: ILocationPermissionsHandler
) : ILocationManager, ILocationClient {

    private val fusedLocationProvider: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationReslt: LocationResult?) {
            locationReslt?.run {
                onLocationReceived(lastLocation)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation() {
        checkPermissions()
        fusedLocationProvider.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.run {
                    onLocationReceived(location)
                } ?: subscribeToLocationUpdates()
            }
            .addOnFailureListener { e ->
                onLocationFailure(e)
            }
    }


    override fun getLastSavedLocation() {
        val coordinates = LocationPreferences(sharedPreferences).getLastKnownCoordinates()
        coordinates?.run {
            locationObserver.onLocationChange(coordinates)
        }
    }

    @SuppressLint("MissingPermission")
    override fun subscribeToLocationUpdates() {
        checkPermissions()
        val request = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProvider.requestLocationUpdates(request, locationCallback, null)
    }

    override fun unsubscribeFromLocationUpdates() {
        fusedLocationProvider.removeLocationUpdates(locationCallback)
    }

    private fun onLocationReceived(location: Location) {
        val coordinates = LastKnownCoordinates(location.latitude, location.longitude)
        locationObserver.onLocationChange(coordinates)
        LocationPreferences(sharedPreferences).setLastKnownCoordinates(coordinates)
    }

    private fun onLocationFailure(e: Exception) {
        locationObserver.onLocationChangeError(e)
    }

    private fun checkPermissions() {
        if (!locationPermissionsHandler.hasPermissions()) {
            locationPermissionsHandler.requestPermissions()
            return
        }
    }


}