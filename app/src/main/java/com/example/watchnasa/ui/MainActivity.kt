package com.example.watchnasa.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.watchnasa.R
import com.example.watchnasa.ui.fragment.apod.APODFragment

const val KEY_PREF = "app_settings"
const val KEY_THEME = "current_theme"
const val KEY_ROVER_ICON = "current_rover_icon"
const val KEY_ROVER_NAME = "current_rover_name"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // применяем автоматически темную тему
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        // загружаем сохраненную тему из настроек приложения
        setTheme(getSavedTheme())
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, APODFragment.newInstance(), "")
                .commit()
        }
    }

    // обработка события по нажатию системной кнопки "Назад"
    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container)
        if (fragment is BackPressedMonitor) {
            // проверяем если нажатие кнопки "Назад" во фрагменте,
                // унаследованном от BackPressedMonitor вернуло false,
                // то отдаем обработку кнопки системной "Назад" активити
            if (!fragment.onBackPressed()) {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    // метод сохранения выбранной темы в настройках приложения
    fun setSelectedTheme(themeId: Int) {
        val sharedPref = getSharedPreferences(KEY_PREF, MODE_PRIVATE)
        sharedPref.edit().apply {
            putInt(KEY_THEME, themeId)
            apply()
        }
    }

    // метод загрузки сохраненной темы из настроек приложения
    fun getSavedTheme(): Int {
        val sharedPref = getSharedPreferences(KEY_PREF, MODE_PRIVATE)
        return sharedPref.getInt(KEY_THEME, -1)
    }

    companion object {
        fun newInstance() = MainActivity()
    }
}