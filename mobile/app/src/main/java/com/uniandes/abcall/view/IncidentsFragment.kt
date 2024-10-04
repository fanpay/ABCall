package com.uniandes.abcall.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uniandes.abcall.R

import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.uniandes.abcall.databinding.FragmentIncidentsBinding
import com.uniandes.abcall.view.adapters.IncidentAdapter
import com.uniandes.abcall.viewmodel.IncidentViewModel

class IncidentsFragment : Fragment() {

    private lateinit var binding: FragmentIncidentsBinding
    private lateinit var incidentAdapter: IncidentAdapter
    private val incidentViewModel: IncidentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIncidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar RecyclerView
        incidentAdapter = IncidentAdapter(emptyList())
        binding.incidentsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.incidentsRv.adapter = incidentAdapter

        // Observar cambios en los incidentes desde el ViewModel
        incidentViewModel.incidents.observe(viewLifecycleOwner) { incidents ->
            incidentAdapter.setIncidents(incidents)
        }

        // Si necesitas un indicador de progreso, podrías agregarlo
        // Mostrar el ProgressBar mientras sincroniza
        incidentViewModel.incidents.observe(viewLifecycleOwner) { incidents ->
            if (incidents.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}