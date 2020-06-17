package com.github.odaridavid.isonge

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.odaridavid.isonge.PermissionUtils.permissionGranted
import com.github.odaridavid.isonge.SdkUtils.versionFrom
import com.github.odaridavid.isonge.location.ILocationPermissionRationaleListener
import com.github.odaridavid.isonge.location.ILocationPermissionsHandler
import com.github.odaridavid.isonge.location.permissions.ForegroundLocationPermissionsHandler
import com.github.odaridavid.isonge.location.utils.LocationUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder


internal abstract class BaseActivity : AppCompatActivity(), ILocationPermissionRationaleListener {

    val permHandler: ILocationPermissionsHandler by lazy {
        ForegroundLocationPermissionsHandler(this)
    }
    private var locationSettingsDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (versionFrom(Build.VERSION_CODES.M)) {
            window.decorView.systemUiVisibility = setSystemIconsVisibilityOnWhite()
            setSystemBarsToWhite()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!LocationUtils.isLocationEnabled(this)) {
            showLocationDisabledDialog()
        }
        LocationUtils.isLocationEnabled.observe(this) { isEnabled ->
            if (isEnabled) {
                hideLocationDisabledDialog()
                showLastKnownLocation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setSystemIconsVisibilityOnWhite(): Int =
        if (versionFrom(Build.VERSION_CODES.O))
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        else
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


    @RequiresApi(Build.VERSION_CODES.M)
    private fun setSystemBarsToWhite() {
        window.statusBarColor = getColor(android.R.color.background_light)
        window.navigationBarColor = getColor(android.R.color.background_light)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ForegroundLocationPermissionsHandler.RQ_CODE) {
            if ((grantResults.isNotEmpty() && grantResults.permissionGranted())) {
                removeRationale()
                showLastKnownLocation()
            } else showRationale()
        }
    }

    fun handleForegroundLocationPermissions() {
        if (!permHandler.hasPermissions()) {
            if (permHandler.shouldShowRationale())
                showRationale()
            else
                permHandler.requestPermissions()
        } else {
            removeRationale()
            showLastKnownLocation()
        }
    }

    override fun onRationaleAllowPermissionRequest() {
        val permHandler = ForegroundLocationPermissionsHandler(this)
        permHandler.requestPermissions()
    }

    override fun onRationaleDenyPermissionRequest() {
        removeRationale()
        //TODO Provide alternative or show something showing permission is needed without impacting ux
    }

    /**
     * If location setting changes to off will be called
     */
    private fun showLocationDisabledDialog() {
        locationSettingsDialog = MaterialAlertDialogBuilder(this)
            .setCancelable(false)
            .setTitle("Location Disabled")
            .setMessage("Enable location to continue using the app.")
            .show()
    }

    /**
     * If location setting changes to on will be called
     */
    private fun hideLocationDisabledDialog() {
        locationSettingsDialog?.dismiss()
    }

    /**
     * Centers in on users last known location
     */
    abstract fun showLastKnownLocation()

    /**
     * If foreground location permission is not granted show rationale
     */
    abstract fun showRationale()

    /**
     * If foreground location permission is granted remove rationale
     */
    abstract fun removeRationale()

}

