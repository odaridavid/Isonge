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
import android.util.Log
import com.github.odaridavid.isonge.location.ILocationManager
import com.github.odaridavid.isonge.location.ILocationObserver
import com.github.odaridavid.isonge.location.ILocationPermissionsHandler
import com.github.odaridavid.isonge.location.model.LastKnownCoordinates
import com.github.odaridavid.isonge.location.permissions.ForegroundLocationPermissionsHandler
import com.github.odaridavid.isonge.location.prefs.LocationPreferences
import com.google.android.gms.location.LocationServices


class LocationManager(
    private val context: Context,
    private val locationObserver: ILocationObserver,
    private val sharedPreferences: SharedPreferences,
    private val locationPermissionsHandler: ILocationPermissionsHandler
) : ILocationManager {

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation() {
        val fusedLocationProvider = LocationServices.getFusedLocationProviderClient(context)
        if (!locationPermissionsHandler.hasPermissions()) {
            locationPermissionsHandler.requestPermissions()
            return
        }
        fusedLocationProvider.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.run {
                    handleFusedLocationSuccess(location)
                }
            }
            .addOnFailureListener { e ->
                handleFusedLocationFailure(e)
            }
    }

    override fun getLastSavedLocation() {
        val coordinates = LocationPreferences(sharedPreferences).getLastKnownCoordinates()
        locationObserver.onLocationChange(coordinates)
        Log.i("Location Provider", "Last Known Saved Coordinates : ${coordinates}")
    }

    private fun handleFusedLocationSuccess(location: Location) {
        val coordinates = LastKnownCoordinates(location.latitude, location.longitude)
        locationObserver.onLocationChange(coordinates)
        LocationPreferences(sharedPreferences).setLastKnownCoordinates(coordinates)
        Log.i("Location Provider", "Last Known Coordinates : ${coordinates}")
    }

    private fun handleFusedLocationFailure(e: Exception) {
        locationObserver.onLocationChangeError(e)
        Log.e("Location Provider", "Getting Last Known Coordinates Failed ${e.message}")
    }

}