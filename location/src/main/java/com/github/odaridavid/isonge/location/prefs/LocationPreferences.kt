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
package com.github.odaridavid.isonge.location.prefs

import android.content.SharedPreferences
import com.github.odaridavid.isonge.location.ILocationPreferences
import com.github.odaridavid.isonge.location.model.LastKnownCoordinates


internal class LocationPreferences(
    private val sharedPreferences: SharedPreferences
) : ILocationPreferences {

    override fun setLastKnownCoordinates(lastKnownCoordinates: LastKnownCoordinates) {
        val editor = sharedPreferences.edit()
        with(editor) {
            putFloat(LATITUDE_KEY, lastKnownCoordinates.latitude.toFloat())
            putFloat(LONGITUDE_KEY, lastKnownCoordinates.longitude.toFloat())
            apply()
        }
    }

    override fun getLastKnownCoordinates(): LastKnownCoordinates? {
        val latitude = sharedPreferences.getFloat(LATITUDE_KEY, DEFAULT_COORDINATES)
        val longitude = sharedPreferences.getFloat(LONGITUDE_KEY, DEFAULT_COORDINATES)
        return if (latitude == DEFAULT_COORDINATES && longitude == DEFAULT_COORDINATES) null
        else LastKnownCoordinates(latitude.toDouble(), longitude.toDouble())
    }

    companion object {
        private const val LATITUDE_KEY = "latitude"
        private const val LONGITUDE_KEY = "longitude"
        const val DEFAULT_COORDINATES = 0.0f
    }

}