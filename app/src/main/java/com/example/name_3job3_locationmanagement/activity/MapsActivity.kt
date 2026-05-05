package com.example.name_3job3_locationmanagement.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.name_3job3_locationmanagement.R
import com.example.name_3job3_locationmanagement.databinding.ActivityMapsBinding
import com.example.name_3job3_locationmanagement.repo.UserRepository
import com.example.name_3job3_locationmanagement.viewmodle.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private val repo = UserRepository()

    private lateinit var map: GoogleMap
    private val viewModel by viewModels<MapsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return MapsViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Find the map fragment using the correct R class
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment

        mapFragment?.getMapAsync(this) ?: run {
            Toast.makeText(this, "Map not found", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val showAll = intent.getBooleanExtra("showAll", false)
        val userId = intent.getStringExtra("uid")

        if (showAll) {
            viewModel.loadAllUsers()
        } else if (userId != null) {
            viewModel.loadSingleUser(userId)
        }

        observeData()
    }

    private fun observeData() {
        viewModel.user.observe(this) { user ->
            user?.let {
                if (it.latitude != null && it.longitude != null) {
                    val pos = LatLng(it.latitude, it.longitude)

                    val color = BitmapDescriptorFactory.HUE_BLUE

                    map.clear()
                    map.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(it.username.ifEmpty { it.email })
                            .icon(BitmapDescriptorFactory.defaultMarker(color))
                    )

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
                }
            }
        }

        viewModel.userList.observe(this) { list ->
            map.clear()

            list.forEach {
                if (it.latitude != null && it.longitude != null) {
                    val pos = LatLng(it.latitude, it.longitude)
                    val color = BitmapDescriptorFactory.HUE_GREEN

                    map.addMarker(
                        MarkerOptions()
                            .position(pos)
                            .title(it.username.ifEmpty { it.email })
                            .icon(BitmapDescriptorFactory.defaultMarker(color))
                    )
                }
            }

            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(23.7548, 90.3765), 15f)
            )
        }
    }
}
