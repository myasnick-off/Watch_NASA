package com.example.watchnasa.ui.fragment.sun

import android.annotation.SuppressLint
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.watchnasa.R
import com.example.watchnasa.databinding.ItemSolarFlareDataBinding
import com.example.watchnasa.databinding.ItemSolarFlareTitleBinding
import com.example.watchnasa.repository.dto.SolarFlareResponseData

private const val TIME_TITLE = "time_title"
private const val CLASS_TITLE = "class_title"
private const val TIME_TITLE_TYPE = 0
private const val CLASS_TITLE_TYPE = 1
private const val DATA_TYPE = 2

class SolarRecyclerAdapter(private val textSize: Float) : RecyclerView.Adapter<SolarViewHolder>(),
    ItemTouchHelperAdapter {

    private var solarData: MutableList<SolarFlareResponseData> = mutableListOf()
    private lateinit var itemListener: SolarFragment.SolarItemClickListener

    // метод передачи списка данных в адаптер
    fun setItems(newItems: List<SolarFlareResponseData>) {
        val result = DiffUtil.calculateDiff(DiffUtilCallback(solarData, newItems))
        result.dispatchUpdatesTo(this)
        solarData.clear()
        solarData.addAll(newItems)
    }

    fun getItems(): List<SolarFlareResponseData> {
        return solarData
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
        return when (solarData[position].flrID) {
            TIME_TITLE -> TIME_TITLE_TYPE
            CLASS_TITLE -> CLASS_TITLE_TYPE
            else -> DATA_TYPE
        }
    }

    override fun onBindViewHolder(holder: SolarViewHolder, position: Int) {
        holder.bind(solarData[position])
    }

    override fun onBindViewHolder(holder: SolarViewHolder, position: Int, payloads: MutableList<Any>) {
        // если изменений внутри элементов списка не нашлось (payloads пустой)
        if (payloads.isEmpty()) {
            // запускаем обычный onBindViewHolder
            super.onBindViewHolder(holder, position, payloads)
        } else {    // иначе применяем изменения
            // получаем переменную, содержащую самые старые и самые новые данные элемента
            val combinedChange = createCombinedPayload(payloads as List<Change<SolarFlareResponseData>>)
            val oldData = combinedChange.oldData
            val newData = combinedChange.newData

            // если элемент списка относится к TitleViewHolder
            if (holder is TitleViewHolder) {
                // сразу обновляем его единственное поле
                val binding = ItemSolarFlareTitleBinding.bind(holder.itemView)
                binding.timeTitleTextView.text = newData.beginTime
            }
            // если элемент списка относится к DataViewHolder
            else {
                // через условия проверяем какое поле элемента изменилось и перезаписываем его
                val binding = ItemSolarFlareDataBinding.bind(holder.itemView)
                if (oldData.beginTime != newData.beginTime) {
                    binding.startTimeTextView.text = newData.beginTime
                }
                if (oldData.peakTime != newData.peakTime) {
                    binding.peakTimeTextView.text = newData.peakTime
                }
                if (oldData.endTime != newData.endTime) {
                    binding.endTimeTextView.text = newData.endTime
                }
                if (oldData.classType != newData.classType) {
                    binding.intensityTextView.text = newData.classType
                }
                if (oldData.sourceLocation != newData.sourceLocation) {
                    binding.regionTextView.text = newData.sourceLocation
                }
            }
        }
    }

    override fun getItemCount() = solarData.size

    inner class DataViewHolder(view: View) : SolarViewHolder(view), ItemTouchHelperViewHolder {

        @SuppressLint("SetTextI18n")
        override fun bind(data: SolarFlareResponseData) {
            val context = itemView.context
            ItemSolarFlareDataBinding.bind(itemView).apply {
                // форматируем и заполняем все поля элемента списка данными из data
                startTimeTextView.text = spanText(data.beginTime, textSize)
                peakTimeTextView.text = spanText(data.peakTime, textSize)
                endTimeTextView.text = spanText(data.endTime, textSize)
                val intensityText = spanText(data.classType, textSize)
                // задаем цвет фона значения интенсивности, если оно относится к классу M или X
                if (intensityText.contains('M')) {
                    val color = ContextCompat.getColor(context, R.color.orange)
                    intensityText.setSpan(BackgroundColorSpan(color), 0, intensityText.length, 0)
                }
                if (intensityText.contains('X')) {
                    val color = ContextCompat.getColor(context, R.color.red)
                    intensityText.setSpan(BackgroundColorSpan(color), 0, intensityText.length, 0)
                }
                intensityTextView.text = intensityText
                regionTextView.text = spanText(data.sourceLocation, textSize)

                // форматируем текст заголовков
                startTimeTitle.text = spanTitle(context.getString(R.string.start_time), textSize)
                peakTimeTitle.text = spanTitle(context.getString(R.string.peak_time), textSize)
                endTimeTitle.text = spanTitle(context.getString(R.string.end_time), textSize)
                regionTitle.text = spanTitle(context.getString(R.string.region), textSize)

                // форматируем текст заголовка "Интенсивность"
                val intensityTitle = spanTitle(context.getString(R.string.intensity), textSize)
                // делаем текст заголовка "Интенсивность" кликабельным
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        itemListener.onIntensityTextClicked()
                    }
                }
                intensityTitle.setSpan(clickableSpan, 0, intensityTitle.length, 0)
                this.intensityTitle.movementMethod = LinkMovementMethod.getInstance()
                this.intensityTitle.text = intensityTitle

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
                    moveItemUp()
                }
                // инициализируем слушатель при нажатии на кнпку "Вниз"
                solarItemDownButton.setOnClickListener {
                    moveItemDown()
                }
            }
        }

        // метод перемещения элемента списка на одну позицию вверх
        private fun moveItemUp() {
            solarData.removeAt(layoutPosition).apply {
                if (layoutPosition > 0) {
                    solarData.add(layoutPosition - 1, this)
                    notifyItemMoved(layoutPosition, layoutPosition - 1)
                } else {
                    // если элемент находится в самом верху, перетаскиваем его в самый конец
                    solarData.add(this)
                    notifyItemMoved(layoutPosition, itemCount - 1)
                }
            }
        }

        // метод перемещения элемента списка на одну позицию вниз
        private fun moveItemDown() {
            solarData.removeAt(layoutPosition).apply {
                if (layoutPosition < itemCount) {
                    solarData.add(layoutPosition + 1, this)
                    notifyItemMoved(layoutPosition, layoutPosition + 1)
                } else {
                    // если элемент находится в самом низу, перетаскиваем его на самый верх
                    solarData.add(0, this)
                    notifyItemMoved(layoutPosition, 0)
                }
            }
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
                // если элемент списка является заголовком даты в текстовое поле вносим заголовок даты
                if (getItemViewType(layoutPosition) == TIME_TITLE_TYPE) {
                    timeTitleTextView.text = spanText(data.beginTime, textSize)
                }
                // если элемент списка является заголовком класса в текстовое поле вносим заголовок класса
                if (getItemViewType(layoutPosition) == CLASS_TITLE_TYPE) {
                    timeTitleTextView.text = spanText(data.classType, textSize)
                }
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        solarData.removeAt(fromPosition).apply {
            solarData.add(toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        solarData.removeAt(position)
        notifyItemRemoved(position)
    }
}