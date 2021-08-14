package com.timermakov.findmyphone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.timermakov.findmyphone.databinding.MarkerInfoContentsBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {
    override fun getInfoWindow(marker: Marker): View? {
        // Return null to indicate that the
        // default window (white bubble) should be used
        return null
    }

    override fun getInfoContents(marker: Marker): View? {
        val child = marker.tag as? Child ?: return null
        val binding = MarkerInfoContentsBinding.inflate(LayoutInflater.from(context))

        binding.textViewTitle.text = child.name
        binding.textViewAddress.text = child.address
        binding.textViewRating.text = child.content
        return binding.root
    }
}