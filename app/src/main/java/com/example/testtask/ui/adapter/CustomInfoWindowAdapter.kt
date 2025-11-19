package com.example.testtask.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.testtask.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {

    private val view: View =
        LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    override fun getInfoWindow(marker: Marker): View {
        render(marker, view)
        return view
    }

    override fun getInfoContents(marker: Marker): View {
        render(marker, view)
        return view
    }

    private fun render(marker: Marker, view: View) {
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvSnippet = view.findViewById<TextView>(R.id.tvSnippet)

        tvTitle.text = marker.title ?: ""
        tvSnippet.text = marker.snippet ?: ""
    }
}