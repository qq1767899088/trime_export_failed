package com.osfans.trime.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.osfans.trime.R
import com.osfans.trime.core.Rime
import com.osfans.trime.data.AppPrefs
import com.osfans.trime.ime.core.Trime
import com.osfans.trime.ui.components.SoundPickerDialog
import com.osfans.trime.ui.main.MainViewModel

class KeyboardFragment :
    PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val viewModel: MainViewModel by activityViewModels()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.keyboard_preference)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val trime = Trime.getServiceOrNull()
        val prefs = AppPrefs.defaultInstance()
        prefs.sync()
        when (key) {
            "keyboard__key_long_press_timeout",
            "keyboard__key_repeat_interval",
            "keyboard__show_key_popup" -> {
                trime?.resetKeyboard()
            }
            "keyboard__show_window" -> {
                trime?.resetCandidate()
            }
            "keyboard__inline_preedit", "keyboard__soft_cursor" -> {
                trime?.loadConfig()
            }
            "keyboard__show_switches" -> {
                Rime.setShowSwitches(prefs.keyboard.switchesEnabled)
            }
            "keyboard__candidate_page_size" -> {
                Rime.applySchemaChange()
            }
        }
    }
    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return when (preference?.key) {
            "keyboard__key_sound_package" -> {
                SoundPickerDialog(requireContext()).show()
                true
            }
            else -> super.onPreferenceTreeClick(preference)
        }
    }
    override fun onResume() {
        super.onResume()
        viewModel.setToolbarTitle(getString(R.string.pref_keyboard))
        viewModel.disableTopOptionsMenu()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
