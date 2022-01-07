package com.example.watchnasa.ui.fragment.sun

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.watchnasa.repository.dto.SolarFlareResponseData

abstract class SolarViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(data: SolarFlareResponseData)
}