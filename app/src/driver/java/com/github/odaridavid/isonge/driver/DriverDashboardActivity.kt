package com.github.odaridavid.isonge.driver

import android.os.Bundle
import android.view.View
import com.github.odaridavid.isonge.BaseActivity
import com.github.odaridavid.isonge.R
import com.github.odaridavid.isonge.databinding.ActivityDriverDashboardBinding
import com.github.odaridavid.isonge.location.fragment.PermissionsRationaleFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

internal class DriverDashboardActivity : BaseActivity(), OnMapReadyCallback {

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

    override fun showCurrentLocation() {
        val sydney = LatLng(-34.0, 151.0)
        if (::map.isInitialized) {
            map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
            map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        }
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
        binding.permissionsFragment.visibility = View.VISIBLE
    }

    override fun removeRationale() {
        binding.permissionsFragment.visibility = View.GONE
    }

}