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
    private val solarData: MutableList<SolarFlareResponseData>,
    private val itemListener: SolarFragment.SolarItemClickListener
) :
    RecyclerView.Adapter<SolarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolarViewHolder {
        return if (viewType == DATA_TYPE) {
            val bindingViewHolder =
                ItemSolarFlareDataBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataViewHolder(bindingViewHolder.root)
        } else {
            val bindingViewHolder =
                ItemSolarFlareTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            TitleViewHolder(bindingViewHolder.root)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (solarData[position].flrID == "time_title") {
            return TITLE_TYPE
        }
        return DATA_TYPE
    }

    override fun onBindViewHolder(holder: SolarViewHolder, position: Int) {
        holder.bind(solarData[position])
    }

    override fun getItemCount() = solarData.size

    inner class DataViewHolder(view: View) : SolarViewHolder(view) {

        @SuppressLint("SetTextI18n")
        override fun bind(data: SolarFlareResponseData) {
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

                solarItemRemoveButton.setOnClickListener {
                    solarData.removeAt(layoutPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    inner class TitleViewHolder(view: View) : SolarViewHolder(view) {

        override fun bind(data: SolarFlareResponseData) {
            ItemSolarFlareTitleBinding.bind(itemView).apply {
                timeTitleTextView.text = data.beginTime
            }
        }
    }
}