package com.example.watchnasa.ui.fragment.mars

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import coil.load
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentMarsPhotoBinding
import com.example.watchnasa.repository.dto.PhotoResponseData
import kotlin.collections.ArrayList

class MarsPhotoFragment : Fragment() {

    private var _binding: FragmentMarsPhotoBinding? = null
    private val binding: FragmentMarsPhotoBinding
        get() = _binding!!

    private var position = 0
    private var photoDataList: List<PhotoResponseData> = listOf()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun showData() = with(binding) {
        val photoData = photoDataList[position]
        marsDataCard.marsRoverTextView.text = "${getString(R.string.rover_name)} ${photoData.rover.name}"
        marsDataCard.marsCameraTypeTextView.text =
            "${getString(R.string.camera_type)} ${photoData.camera.fullName}"
        marsDataCard.marsPhotoDateTextView.text =
            "${getString(R.string.earth_date)} ${photoData.earthDate}"
        marsPhotoView.load(photoData.imgSrc)
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