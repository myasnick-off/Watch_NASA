package com.example.watchnasa.ui.fragment.sun

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.watchnasa.R
import com.example.watchnasa.databinding.ItemSolarFlareDataBinding
import com.example.watchnasa.databinding.ItemSolarFlareTitleBinding
import com.example.watchnasa.repository.dto.SolarFlareResponseData

private const val TITLE_TYPE = 0
private const val DATA_TYPE = 1

class SolarRecyclerAdapter(
    private val solarData: List<SolarFlareResponseData>,
    private val itemListener: SolarFragment.SolarItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == DATA_TYPE) {
            val bindingViewHolder =
                ItemSolarFlareDataBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            SolarViewHolder(bindingViewHolder.root)
        } else {
            val bindingViewHolder =
                ItemSolarFlareTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            TitleViewHolder(bindingViewHolder.root)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (solarData[position].flrID == "time_title") {
            return TITLE_TYPE
        }
        return DATA_TYPE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == DATA_TYPE) {
            (holder as SolarViewHolder).bind(solarData[position])
        } else {
            (holder as TitleViewHolder).bind(solarData[position])
        }
    }

    override fun getItemCount() = solarData.size

    inner class SolarViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bind(data: SolarFlareResponseData) {
            ItemSolarFlareDataBinding.bind(itemView).apply {
                val context = itemView.context
                startTimeTextView.text = "${context.getString(R.string.start_time)} ${data.beginTime}"
                peakTimeTextView.text = "${context.getString(R.string.peak_time)} ${data.peakTime}"
                endTimeTextView.text = "${context.getString(R.string.end_time)} ${data.endTime}"
                intensityTextView.text = "${context.getString(R.string.intensity)} ${data.classType}"
                regionTextView.text = "${context.getString(R.string.region)} ${data.sourceLocation}"

                itemView.setOnClickListener {
                    itemListener.onItemClicked(layoutPosition)
                }
            }
        }
    }

    class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(data: SolarFlareResponseData) {
            ItemSolarFlareTitleBinding.bind(itemView).apply {
                timeTitleTextView.text = data.beginTime
            }
        }
    }
}