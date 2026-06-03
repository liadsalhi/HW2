package com.example.hw1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

/**
 * Fragment 2 – Google Map.
 * Opens with a default view of Israel. Shows a marker only when [panToLocation] is called.
 * Call [panToLocation] (from HighScoresActivity) when the user taps a leaderboard row.
 */
class MapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Attach the Google Maps child fragment programmatically
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
        }

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(31.5, 34.9), 7f))
    }

    /** Called by HighScoresActivity when the user taps a row in the leaderboard. */
    fun panToLocation(lat: Double, lng: Double) {
        val map = googleMap ?: return
        if (lat == 0.0 && lng == 0.0) return
        map.clear()
        map.addMarker(MarkerOptions().position(LatLng(lat, lng)))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 14f))
    }
}