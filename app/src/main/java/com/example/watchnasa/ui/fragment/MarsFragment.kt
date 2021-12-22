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
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentMarsBinding
import com.example.watchnasa.viewmodel.MarsDataState
import com.example.watchnasa.viewmodel.MarsViewModel
import java.util.*

class MarsFragment: Fragment() {

    private val viewModel: MarsViewModel by lazy {
        ViewModelProvider(this)[MarsViewModel::class.java]
    }

    private var _binding: FragmentMarsBinding? = null
    private val binding: FragmentMarsBinding
    get() = _binding!!

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
        val observer = Observer<MarsDataState> { renderData(it) }
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)
        viewModel.getMarsPhotoFromServer(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun renderData(state: MarsDataState) = with(binding) {
        when(state) {
            is MarsDataState.Loading -> marsProgressBar.visibility = View.VISIBLE
            is MarsDataState.Success -> {
                marsPhotoView.load(state.marsData.photos[0].imgSrc)
                marsProgressBar.visibility = View.GONE
            }
            is MarsDataState.Error -> {
                marsProgressBar.visibility = View.GONE
                showErrorDialog()
            }
        }
    }

    // метод отображения диалога с ошибкой загрузки контента
    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.loading_error)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setPositiveButton(R.string.retry) { _, _ -> viewModel.getMarsPhotoFromServer(Date()) }
            .setNeutralButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    companion object {
        fun newInstance() = MarsFragment()
    }
}