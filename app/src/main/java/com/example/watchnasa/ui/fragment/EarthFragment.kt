package com.example.watchnasa.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.watchnasa.BuildConfig
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentEarthBinding
import com.example.watchnasa.repository.dto.EpicResponseData
import com.example.watchnasa.viewmodel.EpicState
import com.example.watchnasa.viewmodel.EpicViewModel
import java.util.*

class EarthFragment: Fragment() {

    private val viewModel: EpicViewModel by lazy {
        ViewModelProvider(this)[EpicViewModel::class.java]
    }

    private var _binding: FragmentEarthBinding? = null
    private val binding: FragmentEarthBinding
    get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEarthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val observer = Observer<EpicState> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getEpicImageFromServer()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(state: EpicState) = with(binding) {
        when(state) {
            is EpicState.Loading -> earthProgressBar.visibility = View.VISIBLE
            is EpicState.Success -> {
                earthPhotoView.load(imageUrlFromData(state.epicData))
                earthProgressBar.visibility = View.GONE
            }
            is EpicState.Error -> {
                earthProgressBar.visibility = View.GONE
                showErrorDialog()
            }
        }
    }

    private fun imageUrlFromData(epicData: EpicResponseData): String {
        val imageName = epicData.image + ".png"
        val imageDate = epicData.date
            .replace("-", "/")
            .substringBefore(" ", "")
        return "https://api.nasa.gov/EPIC/archive/natural/" +
                imageDate + "/png/" + imageName +
                "?api_key=" + BuildConfig.NASA_API_KEY
    }

    // метод отображения диалога с ошибкой загрузки контента
    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.loading_error)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setPositiveButton(R.string.retry) { _, _ -> viewModel.getEpicImageFromServer() }
            .setNeutralButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    companion object {
        fun newInstance() = EarthFragment()
    }
}