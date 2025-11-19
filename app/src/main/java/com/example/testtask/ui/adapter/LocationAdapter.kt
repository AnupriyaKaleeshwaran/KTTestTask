package com.example.testtask.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.testtask.data.model.Location
import com.example.testtask.databinding.ItemLocationBinding

class LocationAdapter(
    private val onItemClick: (Location) -> Unit
) : RecyclerView.Adapter<LocationAdapter.Holder>() {

    private val list = ArrayList<Location>()

    fun submitList(newList: List<Location>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    inner class Holder(val binding: ItemLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Location) {
            binding.tvTimestamp.text =
                java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a")
                    .format(java.util.Date(item.timestamp))

            binding.tvLatLng.text = "Lat: ${item.latitude}, Lng: ${item.longitude}"

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemLocationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return Holder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(list[position])
    }
}
