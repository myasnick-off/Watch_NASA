package com.example.watchnasa.ui.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentMarsBinding
import com.example.watchnasa.repository.dto.MarsResponseData
import com.example.watchnasa.ui.MainActivity
import com.example.watchnasa.viewmodel.MarsDataState
import com.example.watchnasa.viewmodel.MarsViewModel
import java.util.*

class MarsFragment : Fragment() {

    private val viewModel: MarsViewModel by lazy {
        ViewModelProvider(this)[MarsViewModel::class.java]
    }

    private var _binding: FragmentMarsBinding? = null
    private val binding: FragmentMarsBinding
        get() = _binding!!

    // переменная текущей даты
    private var calendar = Calendar.getInstance()
    // флаг запуска процесса поиска ближайшей даты с наличием фото
    private var isNearestDate = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appbarInit()

        val observer = Observer<MarsDataState> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getMarsPhotoFromServer(calendar.time)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_planets_tool_bar, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_calendar -> {
                DatePickerDialog(
                    requireContext(),
                    { _, year, month, day ->
                        calendar.set(year, month, day)
                        viewModel.getMarsPhotoFromServer(calendar.time)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(state: MarsDataState) = with(binding) {
        when (state) {
            is MarsDataState.Loading -> marsProgressBar.visibility = View.VISIBLE
            is MarsDataState.Success -> {
                handleDataFromServer(state.marsData)
                marsProgressBar.visibility = View.GONE
            }
            is MarsDataState.Error -> {
                marsProgressBar.visibility = View.GONE
                showErrorDialog()
            }
        }
    }


    private fun handleDataFromServer(marsData: MarsResponseData) {
        if (marsData.photos.isNotEmpty()) {
            if (isNearestDate) {
                isNearestDate = false
                Toast.makeText(context, R.string.nearest_date_message, Toast.LENGTH_LONG).show()
            }
            showData(marsData)
        } else {
            isNearestDate = true
            calendar.add(Calendar.DAY_OF_MONTH, -1)
            viewModel.getMarsPhotoFromServer(calendar.time)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showData(marsData: MarsResponseData) = with(binding) {
        val photoData = marsData.photos.last()
        marsDataCard.marsRoverTextView.text = "${getString(R.string.rover_name)} Curiosity"
        marsDataCard.marsCameraTypeTextView.text =
            "${getString(R.string.camera_type)} ${photoData.camera.fullName}"
        marsDataCard.marsPhotoDateTextView.text =
            "${getString(R.string.earth_date)} ${photoData.earthDate}"
        marsPhotoView.load(photoData.imgSrc)
    }

    private fun appbarInit() {
        val context = context as MainActivity
        context.setSupportActionBar(binding.marsToolBar)
        setHasOptionsMenu(true)
    }

    // метод отображения диалога с ошибкой загрузки контента
    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.loading_error)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setPositiveButton(R.string.retry) { _, _ -> viewModel.getMarsPhotoFromServer(calendar.time) }
            .setNeutralButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    companion object {
        fun newInstance() = MarsFragment()
    }
}