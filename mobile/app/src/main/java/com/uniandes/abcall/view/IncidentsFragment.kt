package com.uniandes.abcall.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.uniandes.abcall.R
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.databinding.FragmentIncidentsBinding
import com.uniandes.abcall.view.adapters.IncidentAdapter
import com.uniandes.abcall.viewmodel.IncidentViewModel
import com.uniandes.abcall.viewmodel.IncidentViewModelFactory

class IncidentsFragment : Fragment() {

    private lateinit var binding: FragmentIncidentsBinding
    private lateinit var incidentAdapter: IncidentAdapter
    private lateinit var incidentViewModel: IncidentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIncidentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getString("USER_ID")

        (activity as? HomeActivity)?.showNoDataSplash(false,"")
        (activity as? HomeActivity)?.showErrorLayout(false, "")

        if (!userId.isNullOrEmpty()) {

            (activity as? HomeActivity)?.showErrorLayout(false,"")

            val factory = IncidentViewModelFactory(requireActivity().application, userId)
            incidentViewModel = ViewModelProvider(this, factory)[IncidentViewModel::class.java]

            incidentAdapter = IncidentAdapter(emptyList()) { incident ->
                navigateToIncidentDetail(incident)
            }
            binding.incidentsRv.layoutManager = LinearLayoutManager(requireContext())
            binding.incidentsRv.adapter = incidentAdapter

            // Observar cambios en los incidentes desde el ViewModel
            incidentViewModel.incidents.observe(viewLifecycleOwner) { incidents ->
                if (incidents != null && incidents.isNotEmpty()) {
                    binding.progressBar.visibility = View.GONE
                    (activity as? HomeActivity)?.showNoDataSplash(false, "")
                    (activity as? HomeActivity)?.showErrorLayout(false, "")
                    binding.incidentsRv.visibility = View.VISIBLE
                    incidentAdapter.setIncidents(incidents)
                } else {
                    binding.progressBar.visibility = View.GONE
                    (activity as? HomeActivity)?.showErrorLayout(false, "")
                    (activity as? HomeActivity)?.showNoDataSplash(true, resources.getString(R.string.not_data_found))
                    binding.incidentsRv.visibility = View.GONE
                }
            }

            // Sincronizar los incidentes
            incidentViewModel.syncIncidents(userId)
        } else {
            (activity as? HomeActivity)?.showErrorLayout(true,resources.getString(R.string.error_user_id_null))
            Log.e("IncidentsFragment", "El userId es nulo")
        }
    }

    private fun navigateToIncidentDetail(incident: Incident) {
        val action = IncidentsFragmentDirections.actionIncidentsFragmentToIncidentDetailFragment(incident)
        findNavController().navigate(action)
    }
}