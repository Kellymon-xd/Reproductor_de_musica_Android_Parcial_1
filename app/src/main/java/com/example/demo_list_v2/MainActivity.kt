package com.example.demo_list_v2

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat

/** Integrantes:
 * KELLY BEITIA, 8-1023-152 (COORDINADORA)
 * JORGE SARMIENTO, 3-757-1758
 * LEONARDO CASTRO, 8-1032-1264
 * MARIAM HARRIS 1-756-2331
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val switch = findViewById<SwitchCompat>(R.id.switchDarkMode)

        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        switch.isChecked = isDarkMode

        switch.setOnCheckedChangeListener { _, isChecked ->
            val mode = if (isChecked) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }

            AppCompatDelegate.setDefaultNightMode(mode)
        }

        // Solo se carga el PlaylistFragment la 1era vez que se crea la actividad
        // savedInstanceState != null el FragmentManager restaurará el fragmento anterior
        //esto para evitar que al virar el celular en horizontal en la vista de song detail se rompa y nos mande a la vista e lista de canciones
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlaylistFragment())
                .commit()
        }
    }
}