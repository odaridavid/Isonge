package com.github.odaridavid.isonge

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

internal abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (versionFrom(Build.VERSION_CODES.M)) {
            window.decorView.systemUiVisibility = setSystemIconsVisibilityOnWhite()
            setSystemBarsToWhite()
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
}