package com.uniandes.abcall.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.databinding.IncidentItemBinding

class IncidentAdapter(private var incidents: List<Incident>) : RecyclerView.Adapter<IncidentAdapter.IncidentViewHolder>() {

    // ViewHolder que contiene el enlace a los datos
    class IncidentViewHolder(private val binding: IncidentItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(incident: Incident) {
            binding.incident = incident
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncidentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = IncidentItemBinding.inflate(inflater, parent, false)
        return IncidentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IncidentViewHolder, position: Int) {
        holder.bind(incidents[position])
    }

    override fun getItemCount(): Int = incidents.size

    // Método para actualizar la lista de incidencias
    fun setIncidents(incidents: List<Incident>) {
        this.incidents = incidents
        notifyDataSetChanged()
    }
}