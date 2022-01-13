package com.example.watchnasa.ui.fragment.sun

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import coil.load
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentSolarBinding
import com.example.watchnasa.repository.dto.SolarFlareResponseData
import com.example.watchnasa.ui.MainActivity
import com.example.watchnasa.utils.getSavedTextSize
import com.example.watchnasa.utils.hide
import com.example.watchnasa.utils.show
import com.example.watchnasa.viewmodel.ADD_KEY
import com.example.watchnasa.viewmodel.SET_KEY
import com.example.watchnasa.viewmodel.SolarDataSate
import com.example.watchnasa.viewmodel.SolarViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class SolarFragment : Fragment() {

    private val viewModel: SolarViewModel by lazy {
        ViewModelProvider(this)[SolarViewModel::class.java]
    }

    private var _binding: FragmentSolarBinding? = null
    private val binding: FragmentSolarBinding
        get() = _binding!!

    private var startDate = Date(MaterialDatePicker.thisMonthInUtcMilliseconds())
    private var endDate = Date(MaterialDatePicker.todayInUtcMilliseconds())
    private lateinit var adapter: SolarRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSolarBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        appbarInit()
        // выводим временной период выборки на экран
        showDateRange()
        // загружаем картинку в toolbar
        solarAppbarImage.load(getString(R.string.image_url))

        val observer = Observer<SolarDataSate> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getSolarFlareDataFromServer(startDate, endDate, SET_KEY)

        // создаем и присваиваем адаптер для RecyclerView
        adapter = SolarRecyclerAdapter(getSavedTextSize(requireActivity()))
        solarDataRecyclerView.adapter = adapter
        ItemTouchHelper(ItemTouchHelperCallback(adapter)).attachToRecyclerView(solarDataRecyclerView)

        // открываем календарь при нажатии на кнопку FAB для загрузки новых данных
        solarFab.setOnClickListener {
            showCalendarDialog(SET_KEY)
        }

        // открываем календарь при нажатии на кнопку FAB для добавления данных к уже загруженным
        solarAddFab.setOnClickListener {
            showCalendarDialog(ADD_KEY)
        }

        // анимация при скроллинге ListView
        solarDataRecyclerView.setOnScrollChangeListener { _, _, _, _, _ ->
            val params = solarDataRecyclerView.layoutParams as CoordinatorLayout.LayoutParams
            // постепенно убираем верхний отступ у ListView при его скроллинге вниз
            if (solarDataRecyclerView.canScrollVertically(+1) && params.topMargin >= 0) {
                params.topMargin -= 2
                solarDataRecyclerView.layoutParams = params
            }
            // возвращаем верхний отступ у ListView при завершении скролинга вверх
            if (!solarDataRecyclerView.canScrollVertically(-1)) {
                params.topMargin = 60
                solarDataRecyclerView.layoutParams = params
            }
            // добавляем тень у AppBar, когда элеиенты ListView при скроллинге заезжают под него
            solarAppBar.isSelected = solarDataRecyclerView.canScrollVertically(-1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_solar_tool_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_intensity_asc_sort -> viewModel.sortByIntensityAsc()
            R.id.action_intensity_dsc_sort -> {viewModel.sortByIntensityDsc()}
            R.id.action_date_sort -> {viewModel.sortByDate()}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun appbarInit() {
        val context = context as MainActivity
        context.setSupportActionBar(binding.solarToolBar)
        setHasOptionsMenu(true)
    }

    private fun renderData(dataSate: SolarDataSate) = with(binding) {
        when (dataSate) {
            is SolarDataSate.Loading -> solarProgressBar.show()
            is SolarDataSate.Success -> {
                showSolarData(dataSate.solarData)
                solarProgressBar.hide()
            }
            is SolarDataSate.Error -> {
                solarProgressBar.hide()
                showWarningDialog()
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun showDateRange() = with(binding) {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
        solarDateRangeTextView.text =
            "${getString(R.string.date_range)} ${dateFormatter.format(startDate)} - ${dateFormatter.format(endDate)}"
    }

    private fun showCalendarDialog(actionKey: Int) {
        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText(R.string.select_date_range)
            .setSelection(
                Pair(
                    MaterialDatePicker.thisMonthInUtcMilliseconds(),
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
            )
            .build()
        dateRangePicker.addOnPositiveButtonClickListener {
            dateRangePicker.selection?.let {
                val start = Date(dateRangePicker.selection!!.first)
                val end = Date(dateRangePicker.selection!!.second)
                viewModel.getSolarFlareDataFromServer(start, end, actionKey)
                if (start < startDate || actionKey == SET_KEY)
                    startDate = start
                if (end > endDate || actionKey == SET_KEY)
                    endDate = end
                showDateRange()
            }
        }
        dateRangePicker.show(parentFragmentManager, "")
    }

    private fun showSolarData(solarData: List<SolarFlareResponseData>) = with(binding) {
        // реализуем listener для элементов RecyclerView
        val itemClickListener = object : SolarItemClickListener {
            override fun onItemClicked(itemPosition: Int) {
                startActivity(Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(solarData[itemPosition].link)
                })
            }
        }
        // передаем в адаптер новые список с данными и listener
        adapter.setItems(solarData as MutableList)
        adapter.setItemListener(itemClickListener)
    }

    private fun showWarningDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.no_data_for_selected_period)
            .setMessage(R.string.choose_another_selected_period)
            .setIcon(R.drawable.ic_baseline_warning_24)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    // интерфейс обработки нажатия на элемент RecyclerView
    interface SolarItemClickListener {
        fun onItemClicked(itemPosition: Int)
    }

    companion object {
        fun newInstance() = SolarFragment()
    }
}