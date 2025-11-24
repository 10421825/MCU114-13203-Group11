package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.android.PolyUtil
import com.google.maps.model.TravelMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var geoApiContext: GeoApiContext
    private var currentPolyline: Polyline? = null

    private val taipei101 = LatLng(25.033611, 121.565000)
    private val taipeiMain = LatLng(25.047924, 121.517081)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        findViewById<Button>(R.id.btnDrive).setOnClickListener { drawRoute(TravelMode.DRIVING) }
        findViewById<Button>(R.id.btnWalk).setOnClickListener { drawRoute(TravelMode.WALKING) }
        findViewById<Button>(R.id.btnBike).setOnClickListener { drawRoute(TravelMode.BICYCLING) }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (hasLocationPermission()) {
            map.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                100
            )
        }

        map.addMarker(MarkerOptions().position(taipei101).title("台北 101"))
        map.addMarker(MarkerOptions().position(taipeiMain).title("台北車站"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(25.04, 121.54), 13f))

        val appInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
        val apiKey = appInfo.metaData.getString("com.google.android.geo.API_KEY").orEmpty()
        if (apiKey.isBlank()) {
            Toast.makeText(this, "請先在 Manifest/strings.xml 設定 Google Maps API Key。", Toast.LENGTH_LONG).show()
            return
        }
        geoApiContext = GeoApiContext.Builder().apiKey(apiKey).build()

        drawRoute(TravelMode.WALKING)
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine && coarse
    }

    private fun drawRoute(mode: TravelMode) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = DirectionsApi.newRequest(geoApiContext)
                    .origin(com.google.maps.model.LatLng(taipei101.latitude, taipei101.longitude))
                    .destination(com.google.maps.model.LatLng(taipeiMain.latitude, taipeiMain.longitude))
                    .mode(mode)
                    .alternatives(false)
                    .await()

                withContext(Dispatchers.Main) {
                    if (result.routes.isNotEmpty()) {
                        val route = result.routes[0]
                        val path = PolyUtil.decode(route.overviewPolyline.encodedPath)

                        currentPolyline?.remove()
                        currentPolyline = map.addPolyline(
                            PolylineOptions()
                                .addAll(path)
                                .color(Color.RED)
                                .width(15f)
                        )

                        val boundsBuilder = LatLngBounds.Builder()
                        path.forEach { boundsBuilder.include(it) }
                        val bounds = boundsBuilder.build()
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
                    } else {
                        Toast.makeText(this@MainActivity, "沒有找到可用路線。", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "路線取得失敗：${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100 && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            if (::map.isInitialized) map.isMyLocationEnabled = true
        }
    }
}