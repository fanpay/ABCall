package com.uniandes.abcall.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale


object LocaleUtils {
    const val LANG_ES: String = "es"
    fun setLocale(context: Context, language: String?) {
        val locale = language?.let { Locale(it) }
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.setLocale(locale)

        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
    }
}
