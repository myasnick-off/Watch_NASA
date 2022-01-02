package com.example.watchnasa.ui.fragment.mars

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentMarsStartBinding
import com.example.watchnasa.repository.dto.MarsResponseData
import com.example.watchnasa.ui.KEY_ROVER_ICON
import com.example.watchnasa.ui.KEY_PREF
import com.example.watchnasa.ui.KEY_ROVER_NAME
import com.example.watchnasa.ui.MainActivity
import com.example.watchnasa.utils.hide
import com.example.watchnasa.utils.show
import com.example.watchnasa.utils.showErrorDialog
import com.example.watchnasa.viewmodel.MarsDataState
import com.example.watchnasa.viewmodel.MarsViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

class MarsFragment : Fragment() {

    private val viewModel: MarsViewModel by lazy {
        ViewModelProvider(this)[MarsViewModel::class.java]
    }

    private var _binding: FragmentMarsStartBinding? = null
    private val binding: FragmentMarsStartBinding
        get() = _binding!!

    // переменная начальной даты поиска фотоданных
    private var calendar = Calendar.getInstance()
    // флаг запуска процесса поиска ближайшей даты с наличием фото
    private var isNearestDate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarsStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        appbarInit()
        roverDataInit()

        val observer = Observer<MarsDataState> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getMarsPhotoFromServer(getSavedRoverName(), calendar.time)

        marsRoverCuriosityFab.setOnClickListener {
            applyRoverChanges(R.drawable.ic_rover_curiosity, R.string.rover_curiosity)
        }
        marsRoverOpportunityFab.setOnClickListener {
            applyRoverChanges(R.drawable.ic_rover_opportunity, R.string.rover_opportunity)
        }
        marsRoverSpiritFab.setOnClickListener {
            applyRoverChanges(R.drawable.ic_rover_spirit, R.string.rover_spirit)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_planets_tool_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // по нажатию кнопки меню запускаем календарь для выбора даты интересующих фото
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_calendar -> showCalendarDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(state: MarsDataState) = with(binding) {
        when (state) {
            is MarsDataState.Loading -> marsProgressBar.show()
            is MarsDataState.Success -> {
                handleDataFromServer(state.marsData)
                marsProgressBar.hide()
            }
            is MarsDataState.Error -> {
                marsProgressBar.hide()
                showErrorDialog(requireContext())
                { _, _ -> viewModel.getMarsPhotoFromServer(getSavedRoverName(), calendar.time) }
            }
        }
    }

    // метод обработки данных с сервера
    private fun handleDataFromServer(marsData: MarsResponseData) {
        // если список с фотоданными не пустой, заполняем этими фотоданными ViewPager
        if (marsData.photos.isNotEmpty()) {
            // если фотоданные оказались не в выбранной дате, а нашлись в ближайшей, сообщаем об этом
            if (isNearestDate) {
                isNearestDate = false
                Toast.makeText(context, R.string.nearest_date_message, Toast.LENGTH_LONG).show()
            }
            createViewPager(marsData)
        } else {
            // если по выбранной дате список с фотоданными пустой,
                // запускаем поиск ближайшей даты с наличием фотоданных
            isNearestDate = true
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            viewModel.getMarsPhotoFromServer(getSavedRoverName(), calendar.time)
        }
    }

    // метод создания ViewPager'а для заполнения фотоданными
    private fun createViewPager(marsData: MarsResponseData) = with(binding) {
        val photoDataList = marsData.photos
        // настраиваем TabLayout
        marsTabLayout.tabMode = TabLayout.MODE_FIXED
        // создаем и присваиваем адаптер ViewPager'у
        val adapter = ViewPagerAdapter(childFragmentManager, lifecycle, photoDataList)
        marsViewPager.adapter = adapter
        // связываем TabLayout и ViewPager вместе
        TabLayoutMediator(marsTabLayout, marsViewPager) { _, _ -> }.attach()
    }

    private fun appbarInit() {
        val context = context as MainActivity
        context.setSupportActionBar(binding.marsToolBar)
        setHasOptionsMenu(true)
    }

    private fun showCalendarDialog() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                calendar.set(year, month, day)
                viewModel.getMarsPhotoFromServer(getSavedRoverName(), calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun roverDataInit() = with(binding) {
        // загружаем иконку выбранного ранее марсохода, а также инициализируем дату
        val roverIconId = getSavedRoverIcon()
        if (roverIconId != -1) {
            marsRoverChooseFab.setImageResource(getSavedRoverIcon())
            dateInit(roverIconId)
        } else {
            // если запуск приложения производится впервые, то по умолчанию выставляем иконку марсохода curiosity
            marsRoverChooseFab.setImageResource(R.drawable.ic_rover_curiosity)
        }
    }

    // сохраняем имя и иконку выбранного марсохода и перезапрашиваем его фотоданные
    private fun applyRoverChanges(roverIconId: Int, roverNameId: Int) {
        setSelectedRover(roverIconId, roverNameId)
        binding.marsRoverChooseFab.setImageResource(roverIconId)
        dateInit(roverIconId)
        viewModel.getMarsPhotoFromServer(getSavedRoverName(), calendar.time)
    }

    // метод инициализации даты поиска данных в зависимости от выбранного марсохода
    private fun dateInit(roverIconId: Int) {
        when(roverIconId) {
            // если выбранный марсоход opportunity или spirit, инициализируем ближайшую дату их последних снимков
            R.drawable.ic_rover_opportunity -> calendar.set(2018, 5, 5)
            R.drawable.ic_rover_spirit -> calendar.set(2010, 2, 21)
            // для марсохода curiosity подойдет сегодняшняя дата
            else -> calendar = Calendar.getInstance()
        }
    }

    // метод сохранения имени и иконки выбранного марсохода
    private fun setSelectedRover(iconId: Int, stringId: Int) {
        val sharedPref = requireActivity().getSharedPreferences(KEY_PREF, AppCompatActivity.MODE_PRIVATE)
        sharedPref.edit().apply {
            putInt(KEY_ROVER_ICON, iconId)
            putInt(KEY_ROVER_NAME, stringId)
            apply()
        }
    }

    // метод загрузки сохраненной иконки марсохода
    private fun getSavedRoverIcon(): Int {
        val sharedPref = requireActivity().getSharedPreferences(KEY_PREF, AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getInt(KEY_ROVER_ICON, -1)
    }

    // метод загрузки сохраненного названия марсохода
    private fun getSavedRoverName(): Int {
        val sharedPref = requireActivity().getSharedPreferences(KEY_PREF, AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getInt(KEY_ROVER_NAME, -1)
    }

    companion object {
        fun newInstance() = MarsFragment()
    }
}