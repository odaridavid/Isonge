package com.github.odaridavid.isonge.driver

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.github.odaridavid.isonge.BaseActivity
import com.github.odaridavid.isonge.R
import com.github.odaridavid.isonge.databinding.ActivityDriverDashboardBinding
import com.github.odaridavid.isonge.location.ILocationObserver
import com.github.odaridavid.isonge.location.fragment.PermissionsRationaleFragment
import com.github.odaridavid.isonge.location.manager.LocationManager
import com.github.odaridavid.isonge.location.model.LastKnownCoordinates
import com.github.odaridavid.isonge.remove
import com.github.odaridavid.isonge.show
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

internal class DriverDashboardActivity : BaseActivity(), OnMapReadyCallback, ILocationObserver {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityDriverDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.driver_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        handleForegroundLocationPermissions()
    }

    override fun showLastKnownLocation() {
        val sharedPref = getSharedPreferences(Constants.SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val locationProvider = LocationManager(baseContext, this, sharedPref, permHandler)
        locationProvider.getCurrentLocation()
    }

    override fun showRationale() {
        val fragTrans = supportFragmentManager.beginTransaction()
        val fragment =
            PermissionsRationaleFragment
                .newInstance(
                    R.string.title_fine_location_permission,
                    R.string.description_fine_location_permission,
                    this
                )
        fragTrans.add(R.id.permissions_fragment, fragment)
        fragTrans.commit()
        binding.permissionsFragment.show()
    }

    override fun removeRationale() {
        binding.permissionsFragment.remove()
    }

    override fun onLocationChange(lastKnownCoordinates: LastKnownCoordinates) {
        Log.i("Driver Dashboard", "$lastKnownCoordinates")
        val currentLocation = LatLng(lastKnownCoordinates.latitude, lastKnownCoordinates.longitude)
        if (::map.isInitialized) {
            map.setMinZoomPreference(12.0f)
            map.animateCamera(CameraUpdateFactory.newLatLng(currentLocation))
            map.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
        }
    }

    override fun onLocationChangeError(exception: Exception) {
        Log.e("Driver Dashboard", "Error Getting Location Update :$exception")
    }

}