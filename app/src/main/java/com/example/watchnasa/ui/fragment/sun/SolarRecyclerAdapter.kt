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
import com.example.watchnasa.utils.hide
import com.example.watchnasa.utils.show

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
                // заполняем все поля элемента списка данными из data
                val context = itemView.context
                startTimeTextView.text = "${context.getString(R.string.start_time)} ${data.beginTime}"
                peakTimeTextView.text = "${context.getString(R.string.peak_time)} ${data.peakTime}"
                endTimeTextView.text = "${context.getString(R.string.end_time)} ${data.endTime}"
                intensityTextView.text = "${context.getString(R.string.intensity)} ${data.classType}"
                regionTextView.text = "${context.getString(R.string.region)} ${data.sourceLocation}"
                // если элемент самый верхний в списке, скрываем у него кнопку "Вверх"
                if (layoutPosition == 0) {
                    solarItemUpButton.hide()
                }
                // если элемент самый нижний в списке, скрываем у него кнопку "Вниз"
                if (layoutPosition == itemCount - 1) {
                    solarItemDownButton.hide()
                }

                // инициализируем слушатель при нажатии на элемент списка
                itemView.setOnClickListener {
                    itemListener.onItemClicked(layoutPosition)
                }

                // инициализируем слушатель при нажатии на кнопку удаления
                solarItemRemoveButton.setOnClickListener {
                    solarData.removeAt(layoutPosition)
                    notifyItemRemoved(layoutPosition)
                }

                // инициализируем слушатель при нажатии на кнпку "Вверх"
                solarItemUpButton.setOnClickListener {
                    solarData.removeAt(layoutPosition).apply {
                        solarData.add(layoutPosition - 1, this)
                    }
                    // если элемент будет перемещен на самый верх, скрываем у него кнопку "Вверх"
                    if (layoutPosition == 1) {
                        solarItemUpButton.hide()
                    }
                    // если наверх перемещается самый последний элемент, отображаем у него кнопку "Вниз"
                    if (layoutPosition == itemCount - 1) {
                        solarItemDownButton.show()
                    }
                    notifyItemMoved(layoutPosition, layoutPosition - 1)
                }

                // инициализируем слушатель при нажатии на кнпку "Вниз"
                solarItemDownButton.setOnClickListener {
                    solarData.removeAt(layoutPosition).apply {
                        solarData.add(layoutPosition + 1, this)
                    }

                    // если элемент будет перемещен в самый низ, скрываем у него кнопку "Вниз"
                    if (layoutPosition == itemCount - 2) {
                        solarItemDownButton.hide()
                    }
                    // если вниз перемещается самый верхний элемент, отображаем у него кнопку "Вверх"
                    if (layoutPosition == 0) {
                        solarItemUpButton.show()
                    }
                    notifyItemMoved(layoutPosition, layoutPosition + 1)
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