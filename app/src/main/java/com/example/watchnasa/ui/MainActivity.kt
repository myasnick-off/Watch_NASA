package com.example.watchnasa.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.example.watchnasa.R

const val MOON_THEME = 0
const val MARS_THEME = 1
const val SPACE_THEME = 2

private const val KEY_PREF = "app_settings"
private const val KEY_THEME = "current_theme"

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

    // метод сохранения выбранной темы в настройках приложения
    fun setNewTheme(themeId: Int) {
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
}