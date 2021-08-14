package com.timermakov.findmyphone

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.timermakov.findmyphone.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MapFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener {
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var binding: FragmentMapBinding
    private lateinit var args: ChildrenFragmentArgs

    private lateinit var geoApiContext: GeoApiContext
    private lateinit var map: GoogleMap
    private lateinit var client: FusedLocationProviderClient

    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

    private val children: List<Child> by lazy {
        ChildrenReader(requireContext()).read()!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)

        client = LocationServices.getFusedLocationProviderClient(requireContext())

        arguments?.let {
            args = ChildrenFragmentArgs.fromBundle(it)
        }

        if (checkPermissions()) {
            mapFragment =
                childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment

            geoApiContext = GeoApiContext.Builder()
                .apiKey(BuildConfig.MAPS_API_KEY)
                .build()

            mapFragment.getMapAsync(this)
        } else
            requestPermissions()

        return binding.root
    }

    override fun onMapReady(gm: GoogleMap) {
        map = gm
        map.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireContext()))
        map.setOnInfoWindowClickListener(this)

        if (checkPermissions()) {
            if (!this::args.isInitialized) {
                client.lastLocation.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    com.google.android.gms.maps.model.LatLng(
                                        lastKnownLocation.latitude,
                                        lastKnownLocation.longitude
                                    ), 10f
                                )
                            )
                        }
                    }
                }
                map.isMyLocationEnabled = true
                addAllMarkers(map)
            } else { // Зум к выбранному заказу
                children.find { it.name == args.childName }?.let {
                    map.addMarker(
                        MarkerOptions()
                            .title(it.name)
                            .position(it.latLng)
                    ).apply { tag = it }
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(it.latLng, 17.0f))
                }
            }
        }
    }

    private fun addAllMarkers(googleMap: GoogleMap) {
        children.forEach { place ->
            googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
            ).apply { tag = place }
        }
    }

    override fun onInfoWindowClick(m: Marker) {
        map.clear()
        addAllMarkers(map)

        if (checkPermissions())
            client.lastLocation.addOnSuccessListener {
                val latLng = LatLng(m.position.latitude, m.position.longitude)
                map.isMyLocationEnabled = true
                GlobalScope.launch(Dispatchers.IO) {
                    val directionResult = DirectionsApi.newRequest(geoApiContext)
                        .origin(LatLng(it.latitude, it.longitude))
                        .destination(latLng).await()

                    //Преобразование итогового пути в набор точек
                    val path = directionResult.routes[0].overviewPolyline.decodePath()

                    //Линия которую будем рисовать
                    val line = PolylineOptions()
                    val latLngBuilder = LatLngBounds.Builder()

                    //Проходимся по всем точкам, добавляем их в Polyline и в LanLngBounds.Builder
                    for (i in path.indices) {
                        line.add(
                            com.google.android.gms.maps.model.LatLng(
                                path[i].lat,
                                path[i].lng
                            )
                        )
                        latLngBuilder.include(
                            com.google.android.gms.maps.model.LatLng(
                                path[i].lat,
                                path[i].lng
                            )
                        )

                        line.width(10f).color(R.color.black)
                        //Выставляем камеру на нужную нам позицию
                        val latLngBounds = latLngBuilder.build()
                        val track = CameraUpdateFactory.newLatLngBounds(
                            latLngBounds, 200, 200, 25
                        )

                        withContext(Dispatchers.Main) {
                            map.addPolyline(line)
                            map.moveCamera(track)
                            m.showInfoWindow()
                        }
                    }
                }
                Toast.makeText(requireContext(), m.title, Toast.LENGTH_SHORT).show()
            }
        else
            requestPermissions()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.type_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        map.mapType =
            when (item.itemId) {
                R.id.normal ->
                    GoogleMap.MAP_TYPE_NORMAL
                R.id.hybrid ->
                    GoogleMap.MAP_TYPE_HYBRID
                R.id.satellite ->
                    GoogleMap.MAP_TYPE_SATELLITE
                R.id.terrain ->
                    GoogleMap.MAP_TYPE_TERRAIN
                else -> GoogleMap.MAP_TYPE_NONE
            }
        return true
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
        )
    }
}