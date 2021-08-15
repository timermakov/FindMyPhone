package com.timermakov.findmyphone

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Context.TELEPHONY_SERVICE
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import android.util.Log
import com.timermakov.findmyphone.retrofit.body.MetricsBody

@SuppressLint("MissingPermission")
class Metrics(var context: Context) {
    var metrics = mutableMapOf<String, Any>(
        "cell_id" to 0,
        "lac" to 0,
        "android_id" to 0,
        "latitude" to 0.0,
        "longitude" to 0.0,
        "rsrp" to 0,
        "rsrq" to 0,
        "rssnr" to 0,
        "imsi" to ""
    )

    init {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val location: Location? = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            User.latitude = location.latitude
            User.longitude = location.longitude
        }
        val locationListener = LocationListener {
            User.latitude = it.latitude
            User.longitude = it.longitude
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 10000,
            1f, locationListener
        )
    }

    @SuppressLint("MissingPermission", "NewApi", "HardwareIds")
    fun getMetrics(): MetricsBody {
        val telephonyManager = context.getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        val gsmCellLocation = telephonyManager.cellLocation as GsmCellLocation

        metrics["cell_id"] = gsmCellLocation.cid
        metrics["lac"] = gsmCellLocation.lac
        metrics["android_id"] = Settings.Secure.getString(
            context.applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )



        val cellInfoList = telephonyManager.allCellInfo
        val rsrp = arrayListOf<Int>()
        val rsrq = arrayListOf<Int>()
        val rssnr = arrayListOf<Int>()
        for (cellInfo in cellInfoList) {
            if (cellInfo is CellInfoLte) {
                rsrp.add(cellInfo.cellSignalStrength.rsrp)
                rsrq.add(cellInfo.cellSignalStrength.rsrq)
                rssnr.add(cellInfo.cellSignalStrength.rssnr)
            }
        }
        metrics["rsrp"] = rsrp.average().toInt()
        metrics["rsrq"] = rsrq.average().toInt()
        metrics["rssnr"] = rssnr.average().toInt()

        // metrics["imsi"] = manager.getSubscriberId()

        Log.d("METRICS", metrics.toString())

        return MetricsBody(
            (System.currentTimeMillis()/1000).toString(),
            metrics["latitude"] as Double,
            metrics["longitude"] as Double,
            metrics["cell_id"] as Int,
            metrics["android_id"] as String,
            metrics["lac"] as Int,
            metrics["rsrp"] as Int,
            metrics["rsrq"] as Int,
            metrics["rssnr"] as Int,
            metrics["imsi"] as String
        )
    }
}