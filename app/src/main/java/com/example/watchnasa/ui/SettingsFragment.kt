package com.example.watchnasa.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentSettingsBinding
import com.google.android.material.tabs.TabLayout

class SettingsFragment: Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
    get() {
        return _binding!!
    }
    private lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // передаем MainActivity в переменную
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        // инициализация элементов TabLayout в зависимости от сохраненной темы приложения
        when(mainActivity.getSavedTheme()) {
            R.style.MoonStyle -> settingsTabLayout.getTabAt(MOON_THEME)?.select()
            R.style.MarsStyle -> settingsTabLayout.getTabAt(MARS_THEME)?.select()
            R.style.SpaceStyle -> settingsTabLayout.getTabAt(SPACE_THEME)?.select()
        }

        // обработчик переключения табов (меняем тему при переключении)
        settingsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { selectTheme(tab.position) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // метод выбора темы приложения в зависимости от выбранного таба
    private fun selectTheme(position: Int) {
        when(position) {
            MOON_THEME -> {
                mainActivity.setNewTheme(R.style.MoonStyle)
                mainActivity.recreate()
            }
            MARS_THEME -> {
                mainActivity.setNewTheme(R.style.MarsStyle)
                mainActivity.recreate()
            }
            SPACE_THEME -> {
                mainActivity.setNewTheme(R.style.SpaceStyle)
                mainActivity.recreate()
            }
        }
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}