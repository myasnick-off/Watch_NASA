package com.example.watchnasa.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentSolarBinding
import com.example.watchnasa.repository.dto.SolarFlareResponseData
import com.example.watchnasa.viewmodel.SolarDataSate
import com.example.watchnasa.viewmodel.SolarViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SolarFragment : Fragment() {

    private val viewModel: SolarViewModel by lazy {
        ViewModelProvider(this)[SolarViewModel::class.java]
    }

    private var _binding: FragmentSolarBinding? = null
    private val binding: FragmentSolarBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSolarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val observer = Observer<SolarDataSate> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getSolarFlareDataFromServer(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(dataSate: SolarDataSate) = with(binding) {
        when (dataSate) {
            is SolarDataSate.Loading -> solarProgressBar.visibility = View.VISIBLE
            is SolarDataSate.Success -> {
                showSolarData(dataSate.solarData)
                solarProgressBar.visibility = View.GONE
            }
            is SolarDataSate.Error -> {
                solarProgressBar.visibility = View.GONE
                showErrorDialog()
            }
        }
    }

    private fun showSolarData(solarData: List<SolarFlareResponseData>) = with(binding) {
        // упаковываем данные о солнечной вспышке в понятную для ListView-адаптера структуру
        val adapterList: ArrayList<Map<String, String>> = arrayListOf()
        for (i in solarData.indices) {
            val map: HashMap<String, String> = hashMapOf()
            map[ATTRIBUTE_START_TIME] = getString(R.string.start_time) + solarData[i].beginTime
            map[ATTRIBUTE_PEAK_TIME] = getString(R.string.peak_time) + solarData[i].peakTime
            map[ATTRIBUTE_END_TIME] = getString(R.string.end_time) + solarData[i].endTime
            map[ATTRIBUTE_INTENSITY] = getString(R.string.intensity) + solarData[i].classType
            map[ATTRIBUTE_REGION] = getString(R.string.region) + solarData[i].sourceLocation
            adapterList.add(map)
        }
        // создаем массив имен атрибутов, из которых будут читаться данные
        val from = arrayOf(
            ATTRIBUTE_START_TIME,
            ATTRIBUTE_PEAK_TIME,
            ATTRIBUTE_END_TIME,
            ATTRIBUTE_INTENSITY,
            ATTRIBUTE_REGION
        )
        // создаем массив ID View-компонентов, в которые будут вставлять данные
        val to = intArrayOf(
            R.id.start_time_text_view,
            R.id.peak_time_text_view,
            R.id.end_time_text_view,
            R.id.intensity_text_view,
            R.id.region_text_view
        )
        // создаем и передаем адаптер для ListView
        val adapter =
            SimpleAdapter(requireContext(), adapterList, R.layout.item_solar_flare_data, from, to)
        solarDataListView.adapter = adapter
        // обрабатываем нажатие на элемент списка
        solarDataListView.setOnItemClickListener { _, _, position, _ ->
            // открываем сайт с более подробной информацией по ссылке из solarData
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(solarData[position].link)
            })
        }
    }

    // метод отображения диалога с ошибкой загрузки контента
    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.loading_error)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setPositiveButton(R.string.retry) { _, _ -> viewModel.getSolarFlareDataFromServer(Date()) }
            .setNeutralButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    companion object {
        private const val ATTRIBUTE_START_TIME = "START_TIME"
        private const val ATTRIBUTE_PEAK_TIME = "PEAK_TIME"
        private const val ATTRIBUTE_END_TIME = "END_TIME"
        private const val ATTRIBUTE_INTENSITY = "INTENSITY"
        private const val ATTRIBUTE_REGION = "REGION"

        fun newInstance() = SolarFragment()
    }
}