package com.example.watchnasa.ui.fragment.apod

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.watchnasa.R
import com.example.watchnasa.databinding.BottomNavigationLayoutBinding
import com.example.watchnasa.ui.fragment.settings.SettingsFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottNavDrawingFragment: BottomSheetDialogFragment() {

    private var _binding: BottomNavigationLayoutBinding? = null
    private val binding: BottomNavigationLayoutBinding
    get() {
        return _binding!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomNavigationLayoutBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.navigationView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.action_settings -> {
                    // запускаем фрагмент с настройками
                    parentFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.container, SettingsFragment.newInstance(), "")
                        .commit()
                }
                R.id.action_info -> {
                    Toast.makeText(context, R.string.info, Toast.LENGTH_SHORT).show()
                }
            }
            dismiss()
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}