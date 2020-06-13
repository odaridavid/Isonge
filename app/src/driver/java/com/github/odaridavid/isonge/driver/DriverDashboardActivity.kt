package com.github.odaridavid.isonge.driver

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import com.github.odaridavid.isonge.BaseActivity
import com.github.odaridavid.isonge.InjectorUtils
import com.github.odaridavid.isonge.R
import com.github.odaridavid.isonge.ViewUtils.remove
import com.github.odaridavid.isonge.ViewUtils.show
import com.github.odaridavid.isonge.databinding.ActivityDriverDashboardBinding
import com.github.odaridavid.isonge.location.ILocationObserver
import com.github.odaridavid.isonge.location.fragment.LocationPermissionsRationaleFragment
import com.github.odaridavid.isonge.location.manager.LocationManager
import com.github.odaridavid.isonge.location.model.LastKnownCoordinates
import com.github.odaridavid.isonge.location.utils.LocationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

//TODO Notify user if location off after granting permission
internal class DriverDashboardActivity : BaseActivity(), OnMapReadyCallback, ILocationObserver {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityDriverDashboardBinding
    private lateinit var locationManager: LocationManager
    private val sharedPref: SharedPreferences by lazy {
        InjectorUtils.getSharedPreferences(this, Constants.SHARED_PREF_KEY)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.driver_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationManager = LocationManager(this, this, sharedPref, permHandler)
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

    override fun onResume() {
        super.onResume()
        LocationUtils.isLocationEnabled.observe(this, Observer { isEnabled ->
            if (isEnabled) {
                showLastKnownLocation()
            }
        })
    }

    override fun showLastKnownLocation() {
        locationManager.getCurrentLocation()
    }

    override fun showRationale() {
        val fragTrans = supportFragmentManager.beginTransaction()
        val fragment =
            LocationPermissionsRationaleFragment
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
        val currentLocation = LatLng(lastKnownCoordinates.latitude, lastKnownCoordinates.longitude)
        if (::map.isInitialized) {
            map.clear()
            map.setMinZoomPreference(12.0f)
            map.animateCamera(CameraUpdateFactory.newLatLng(currentLocation))
            map.addMarker(MarkerOptions().position(currentLocation).title("Current Location"))
        }
    }

    override fun onLocationChangeError(exception: Exception) {
        Log.e("Driver Dashboard", "Error Getting Location Update :$exception")
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.unsubscribeFromLocationUpdates()
    }

}