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
package com.github.odaridavid.isonge.location.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.github.odaridavid.isonge.location.utils.LocationUtils

class LocationSettingChangedBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action?.equals(LocationManager.MODE_CHANGED_ACTION) == true) {
            val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (LocationManagerCompat.isLocationEnabled(lm))
                LocationUtils.setIsLocationEnabled(true)
            else
                LocationUtils.setIsLocationEnabled(false)
        }
    }

    companion object {
        fun register(context: Context) {
            val lscBr = LocationSettingChangedBroadcastReceiver()
            val intentFilter = IntentFilter().apply {
                addAction(LocationManager.MODE_CHANGED_ACTION)
            }
            context.registerReceiver(lscBr, intentFilter)
        }
    }

}