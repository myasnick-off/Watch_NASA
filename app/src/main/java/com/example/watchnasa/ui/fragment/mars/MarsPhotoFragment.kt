package com.example.watchnasa.ui.fragment.mars

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.*
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.transition.*
import coil.load
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentMarsPhotoBinding
import com.example.watchnasa.repository.dto.PhotoResponseData
import com.example.watchnasa.utils.DURATION_500
import com.example.watchnasa.utils.getSavedTextSize
import com.example.watchnasa.utils.hide
import com.example.watchnasa.utils.show
import kotlin.collections.ArrayList

class MarsPhotoFragment : Fragment() {

    private var _binding: FragmentMarsPhotoBinding? = null
    private val binding: FragmentMarsPhotoBinding
        get() = _binding!!

    private var position = 0
    private var photoDataList: List<PhotoResponseData> = listOf()
    private var isZoomed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt(ARG_POSITION)
            photoDataList =
                it.getParcelableArrayList<PhotoResponseData>(ARG_PHOTO_DATA) as List<PhotoResponseData>
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarsPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)
        showData()

        // задаем анимацию (увеличение фото и скрытие описания) по нажатию на изображение
        marsPhotoView.setOnClickListener{
            isZoomed = !isZoomed
            val transitionSet = TransitionSet().apply {
                addTransition(ChangeBounds())
                addTransition(ChangeImageTransform())
                addTransition(Fade())
                duration = DURATION_500
            }
            TransitionManager.beginDelayedTransition(marsDataContainer, transitionSet)
            val params = marsPhotoView.layoutParams as ConstraintLayout.LayoutParams
            if (isZoomed) {
                params.height = ConstraintLayout.LayoutParams.MATCH_PARENT
                marsPhotoView.scaleType = ImageView.ScaleType.CENTER_CROP
                marsDataCard.root.hide()
            } else {
                params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
                marsPhotoView.scaleType = ImageView.ScaleType.FIT_CENTER
                marsDataCard.root.show()
            }
            marsPhotoView.layoutParams = params
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showData() = with(binding) {
        val photoData = photoDataList[position]

        marsDataCard.marsRoverTextView.text = spanText(R.string.rover_name, photoData.rover.name)
        marsDataCard.marsCameraTypeTextView.text = spanText(R.string.camera_type, photoData.camera.fullName)
        marsDataCard.marsPhotoDateTextView.text = spanText(R.string.earth_date, photoData.earthDate)

        marsPhotoView.load(photoData.imgSrc)
    }

    // метод форматирования текста для фотоданных Марса
    private fun spanText(titleRes: Int, data: String): SpannableStringBuilder {
        val textSize = getSavedTextSize(requireActivity())
        val textColor = ContextCompat.getColor(requireContext(), R.color.explanation_title_color)
        val spannableText = SpannableStringBuilder(getString(titleRes)).apply {
            setSpan(ForegroundColorSpan(textColor), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(RelativeSizeSpan(textSize), 0, this.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
            append(" $data")
        }
        return spannableText
    }

    companion object {
        private const val ARG_POSITION = "arg_position"
        private const val ARG_PHOTO_DATA = "photo_data"

        fun newInstance(position: Int, photoDataList: List<PhotoResponseData>): MarsPhotoFragment {
            val args = Bundle().apply {
                putInt(ARG_POSITION, position)
                putParcelableArrayList(ARG_PHOTO_DATA, photoDataList as ArrayList)
            }
            val fragment = MarsPhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}