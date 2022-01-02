package com.example.watchnasa.ui.fragment.earth

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import coil.load
import com.example.watchnasa.BuildConfig
import com.example.watchnasa.databinding.FragmentEarthBinding
import com.example.watchnasa.repository.dto.EpicResponseData
import com.example.watchnasa.utils.DURATION_500
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
    // флаг состояния масшатба фото Земли
    private var isZoomed = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEarthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        val observer = Observer<EpicState> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getEpicImageFromServer()

        // задаем анимацию (увеличение фото и скрытие описания) по нажатию на изображение
        earthPhotoView.setOnClickListener {
            isZoomed = !isZoomed
            val transitionSet = TransitionSet().apply {
                addTransition(ChangeBounds())
                addTransition(ChangeImageTransform())
                addTransition(Fade())
                duration = DURATION_500
            }
            TransitionManager.beginDelayedTransition(earthContainer, transitionSet)
            // приводим параметры картинки к праметрам контейнера, чтобы добраться до ее высоты
            val params = earthPhotoView.layoutParams as ConstraintLayout.LayoutParams

            if (isZoomed) {
                params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                earthPhotoView.scaleType = ImageView.ScaleType.CENTER_CROP
                earthDataCard.root.hide()
            } else {
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                earthPhotoView.scaleType = ImageView.ScaleType.FIT_CENTER
                earthDataCard.root.show()
            }
            earthPhotoView.layoutParams = params
        }
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