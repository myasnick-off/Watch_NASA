package com.example.watchnasa.ui.fragment.settings

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import com.example.watchnasa.R
import com.example.watchnasa.databinding.FragmentSettingsBinding
import com.example.watchnasa.ui.*
import com.example.watchnasa.ui.fragment.apod.APODFragment
import com.google.android.material.tabs.TabLayout

class SettingsFragment: Fragment(), BackPressedMonitor {

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
        val context: Context = ContextThemeWrapper(activity, mainActivity.getSavedTheme())
        val localInflater = inflater.cloneInContext(context)
        _binding = FragmentSettingsBinding.inflate(localInflater, container, false)
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

        // инициализация настроек размера текста в зависимости от сохраненного значения
        textSizeSettingsInit()


        // обработчик переключения табов (меняем тему при переключении)
        settingsTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let { applySelectedTheme(tab.position) }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // обработчик изменения положения слайдера
        settingsSlider.addOnChangeListener { _, value, _ ->
            // сохраняем выбранное значение размера текста в настройках приложения
            setSelectedTextSize(value)
            // применяем выбранное значение размера текста к примеру под слайдером
            textSizeSettingsApply(value)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    // выбираем тему приложения в зависимости от выбранного таба
    // и применяем ее к текущему фрагменту
    private fun applySelectedTheme(position: Int) {
        var themeId = 0
        when(position) {
            MOON_THEME -> themeId = R.style.MoonStyle
            MARS_THEME -> themeId = R.style.MarsStyle
            SPACE_THEME -> themeId = R.style.SpaceStyle
        }
        // сохраняем выбранную тему в настройках приложения
        mainActivity.setSelectedTheme(themeId)
        // перезапускаем текущий фрагмент уже с новой темой
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, newInstance())
            .commit()
    }

    // при нажатии на системную кнопку "назад" пересоздаем активити,
    // тем самым применяя выбранную тему ко всему приложению
    // и возвращаемся к основному (APOD) фрагменту
    override fun onBackPressed(): Boolean {
        mainActivity.recreate()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, APODFragment.newInstance())
            .commit()
        return true
    }

    // метод инициализации настроек размера текста в зависимости от сохраненного значения
    private fun textSizeSettingsInit() = with(binding) {
        val relativeTextSize = getSavedTextSize()
        settingsSlider.value = relativeTextSize
        textSizeSettingsApply(relativeTextSize)
    }

    // метод применения размера текста к примеру под слайдером
    private fun textSizeSettingsApply(textSize: Float) = with(binding) {
        val spannableTitle = SpannableString(getString(R.string.title)).apply {
            setSpan(RelativeSizeSpan(textSize), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val spannableContent = SpannableString(getString(R.string.content)).apply {
            setSpan(RelativeSizeSpan(textSize), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        settingsTitleTextView.text = spannableTitle
        settingsContentTextView.text = spannableContent
    }

    // метод сохранения выбранного разера теста
    private fun setSelectedTextSize(textSize: Float) {
        val sharedPref =
            requireActivity().getSharedPreferences(KEY_PREF, AppCompatActivity.MODE_PRIVATE)
        sharedPref.edit().apply {
            putFloat(KEY_TEXT_SIZE, textSize)
            apply()
        }
    }

    // метод загрузки сохраненного размера теста
    private fun getSavedTextSize(): Float {
        val sharedPref =
            requireActivity().getSharedPreferences(KEY_PREF, AppCompatActivity.MODE_PRIVATE)
        return sharedPref.getFloat(KEY_TEXT_SIZE, 1.5f)
    }


    companion object {
        private const val MOON_THEME = 0
        private const val MARS_THEME = 1
        private const val SPACE_THEME = 2

        fun newInstance() = SettingsFragment()
    }
}