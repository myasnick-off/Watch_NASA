package com.example.watchnasa.ui.fragment.sun

import android.annotation.SuppressLint
import android.graphics.Color
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

class SolarRecyclerAdapter : RecyclerView.Adapter<SolarViewHolder>(), ItemTouchHelperAdapter {

    private var solarData: MutableList<SolarFlareResponseData> = mutableListOf()
    private lateinit var itemListener: SolarFragment.SolarItemClickListener

    // метод передачи списка данных в адаптер
    fun setData(data: MutableList<SolarFlareResponseData>) {
        solarData.clear()
        solarData = data
        notifyDataSetChanged()
    }

    // метод передачи listener'а для элементов списка в адаптер
    fun setItemListener(listener: SolarFragment.SolarItemClickListener) {
        itemListener = listener
    }

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

    inner class DataViewHolder(view: View) : SolarViewHolder(view), ItemTouchHelperViewHolder {

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
                    moveItemUp(this)
                }
                // инициализируем слушатель при нажатии на кнпку "Вниз"
                solarItemDownButton.setOnClickListener {
                    moveItemDown(this)
                }
            }
        }

        // метод перемещения элемента списка на одну позицию вверх
        private fun moveItemUp(binding: ItemSolarFlareDataBinding) {
            solarData.removeAt(layoutPosition).apply {
                solarData.add(layoutPosition - 1, this)
            }
            // если элемент будет перемещен на самый верх, скрываем у него кнопку "Вверх"
            if (layoutPosition == 1) {
                binding.solarItemUpButton.hide()
            }
            // если наверх перемещается самый последний элемент, отображаем у него кнопку "Вниз"
            if (layoutPosition == itemCount - 1) {
                binding.solarItemDownButton.show()
            }
            notifyItemMoved(layoutPosition, layoutPosition - 1)
        }

        // метод перемещения элемента списка на одну позицию вниз
        private fun moveItemDown(binding: ItemSolarFlareDataBinding) {
            solarData.removeAt(layoutPosition).apply {
                solarData.add(layoutPosition + 1, this)
            }
            // если элемент будет перемещен в самый низ, скрываем у него кнопку "Вниз"
            if (layoutPosition == itemCount - 2) {
                binding.solarItemDownButton.hide()
            }
            // если вниз перемещается самый верхний элемент, отображаем у него кнопку "Вверх"
            if (layoutPosition == 0) {
                binding.solarItemUpButton.show()
            }
            notifyItemMoved(layoutPosition, layoutPosition + 1)
        }

        override fun onItemSelected() {
            itemView.alpha = 0.5f
        }

        @SuppressLint("ResourceAsColor")
        override fun onItemCleared() {
            itemView.alpha = 1f
        }
    }

    inner class TitleViewHolder(view: View) : SolarViewHolder(view) {

        override fun bind(data: SolarFlareResponseData) {
            ItemSolarFlareTitleBinding.bind(itemView).apply {
                timeTitleTextView.text = data.beginTime
            }
        }
    }

    override fun onItemMove(from: Int, to: Int) {
        solarData.removeAt(from).apply {
            solarData.add(to, this)
        }
        notifyItemMoved(from, to)
    }

    override fun onItemDismiss(position: Int) {
        solarData.removeAt(position)
        notifyItemRemoved(position)
    }
}