package com.uniandes.abcall.view.adapters.binding

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.uniandes.abcall.R
import java.text.SimpleDateFormat
import java.util.Locale
import java.sql.Timestamp

@BindingAdapter("timestampToDate")
fun TextView.setTimestampToDate(timestamp: Timestamp?) {
    timestamp?.let {
        // Formato deseado: dd-MM-yyyy
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val formattedDate = formatter.format(timestamp)

        // Establecer el texto con el formato "fecha de creacion: dd-MM-yyyy"
        text = context.getString(R.string.creation_date_incident, formattedDate)
    }
}