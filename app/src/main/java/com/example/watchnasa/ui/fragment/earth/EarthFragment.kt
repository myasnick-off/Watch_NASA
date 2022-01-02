package com.example.watchnasa.ui.fragment.earth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.watchnasa.BuildConfig
import com.example.watchnasa.databinding.FragmentEarthBinding
import com.example.watchnasa.repository.dto.EpicResponseData
import com.example.watchnasa.utils.hide
import com.example.watchnasa.utils.show
import com.example.watchnasa.utils.showErrorDialog
import com.example.watchnasa.viewmodel.EpicState
import com.example.watchnasa.viewmodel.EpicViewModel

class EarthFragment : Fragment() {

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
        when (state) {
            is EpicState.Loading -> earthProgressBar.show()
            is EpicState.Success -> {
                showData(state.epicData)
                earthProgressBar.hide()
            }
            is EpicState.Error -> {
                earthProgressBar.hide()
                showErrorDialog(requireContext()) { _, _ -> viewModel.getEpicImageFromServer() }
            }
        }
    }

    private fun showData(epicData: EpicResponseData) = with(binding) {
        earthPhotoView.load(imageUrlFromData(epicData))
        earthDataCard.photoTitleTextView.text = epicData.caption
        earthDataCard.photoDateTextView.text = epicData.date
    }

    private fun imageUrlFromData(epicData: EpicResponseData): String {
        val imageName = "${epicData.image}.png"
        val imageDate = epicData.date
            .replace("-", "/")
            .substringBefore(" ", "")
        return "https://api.nasa.gov/EPIC/archive/natural/${imageDate}/png/${imageName}?api_key=${BuildConfig.NASA_API_KEY}"
    }

    companion object {
        fun newInstance() = EarthFragment()
    }
}