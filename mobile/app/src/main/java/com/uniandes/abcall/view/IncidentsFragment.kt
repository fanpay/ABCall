package com.uniandes.abcall.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.incidents_list)

        val userId = arguments?.getString("USER_ID")

        binding.progressBar.visibility = View.VISIBLE
        binding.incidentsRv.visibility = View.GONE

        (activity as? HomeActivity)?.showNoDataSplash(false,"")
        (activity as? HomeActivity)?.showErrorLayout(false, "")

        if (!userId.isNullOrEmpty()) {

            val factory = IncidentViewModelFactory(requireActivity().application, userId)
            incidentViewModel = ViewModelProvider(this, factory)[IncidentViewModel::class.java]

            incidentAdapter = IncidentAdapter(emptyList()) { incident ->
                navigateToIncidentDetail(incident)
            }
            binding.incidentsRv.layoutManager = LinearLayoutManager(requireContext())
            binding.incidentsRv.adapter = incidentAdapter

            incidentViewModel.loadingState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    IncidentViewModel.LoadingState.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.incidentsRv.visibility = View.GONE
                        (activity as? HomeActivity)?.showNoDataSplash(false, "")
                        (activity as? HomeActivity)?.showErrorLayout(false, "")
                    }
                    IncidentViewModel.LoadingState.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        incidentViewModel.incidents.value?.let { incidents ->
                            if (incidents.isNotEmpty()) {
                                binding.incidentsRv.visibility = View.VISIBLE
                                incidentAdapter.setIncidents(incidents)
                            } else {
                                (activity as? HomeActivity)?.showNoDataSplash(true, getString(R.string.not_data_found))
                            }
                        }
                    }
                    IncidentViewModel.LoadingState.ERROR, null -> {
                        binding.progressBar.visibility = View.GONE
                        (activity as? HomeActivity)?.showErrorLayout(true, getString(R.string.error_loading_data))
                    }
                }
            }

            incidentViewModel.incidents.observe(viewLifecycleOwner) { incidents ->
                if (incidentViewModel.loadingState.value == IncidentViewModel.LoadingState.SUCCESS) {
                    if (incidents.isNotEmpty()) {
                        binding.incidentsRv.visibility = View.VISIBLE
                        incidentAdapter.setIncidents(incidents)
                        (activity as? HomeActivity)?.showNoDataSplash(false, "")
                    } else {
                        binding.incidentsRv.visibility = View.GONE
                        (activity as? HomeActivity)?.showNoDataSplash(true, getString(R.string.not_data_found))
                    }
                }
            }

            incidentViewModel.syncIncidents(userId)
        } else {
            Log.e("IncidentsFragment", "El userId es nulo")
        }
    }

    private fun navigateToIncidentDetail(incident: Incident) {
        val action = IncidentsFragmentDirections.actionIncidentsFragmentToIncidentDetailFragment(incident)
        findNavController().navigate(action)
    }
}