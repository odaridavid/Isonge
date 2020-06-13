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
package com.github.odaridavid.isonge.location.permissions

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.odaridavid.isonge.location.ILocationPermissionsHandler

@RequiresApi(Build.VERSION_CODES.Q)
class BackgroundLocationPermissionHandler(
    private val context: Activity
) : ILocationPermissionsHandler {

    override fun hasPermissions(): Boolean {
        return ContextCompat
            .checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermissions() {
        ActivityCompat.requestPermissions(context, arrayOf(permission), RQ_CODE)
    }

    override fun shouldShowRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(context, permission)
    }

    companion object {
        private const val permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
        private const val RQ_CODE = 100
    }

}