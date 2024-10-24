package com.uniandes.abcall.view

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

        setupActionBarTitle()
        showLoadingState()
        setupFloatingActionButton()

        val userId = arguments?.getString("USER_ID")

        if (!userId.isNullOrEmpty()) {

            val factory = IncidentViewModelFactory(requireActivity().application, userId)
            incidentViewModel = ViewModelProvider(this, factory)[IncidentViewModel::class.java]

            setupRecyclerView()

            incidentViewModel.loadingState.observe(viewLifecycleOwner) { state ->
                when (state) {
                    IncidentViewModel.LoadingState.LOADING -> showLoadingState()
                    IncidentViewModel.LoadingState.SUCCESS -> hideLoadingState()
                    IncidentViewModel.LoadingState.ERROR, null -> showErrorState()
                }
            }

            observeIncidents()

            incidentViewModel.syncIncidents(userId)
        } else {
            Log.e("IncidentsFragment", "El userId es nulo")
        }
    }

    private fun navigateToIncidentDetail(incident: Incident) {
        val action = IncidentsFragmentDirections.actionIncidentsFragmentToIncidentDetailFragment(incident)
        findNavController().navigate(action)
    }

    private fun setupActionBarTitle() {
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.incidents_list)
    }

    private fun setupFloatingActionButton() {
        binding.floatingAddIncident.setOnClickListener {
            showDialogOptions()
        }
    }

    private fun setupRecyclerView() {
        incidentAdapter = IncidentAdapter(emptyList()) { incident ->
            navigateToIncidentDetail(incident)
        }
        binding.incidentsRv.layoutManager = LinearLayoutManager(requireContext())
        binding.incidentsRv.adapter = incidentAdapter
    }

    private fun showLoadingState() {
        binding.progressBar.visibility = View.VISIBLE
        binding.incidentsRv.visibility = View.GONE
        (activity as? HomeActivity)?.showNoDataSplash(false, "")
        (activity as? HomeActivity)?.showErrorLayout(false, "")
    }

    private fun hideLoadingState() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorState() {
        binding.progressBar.visibility = View.GONE
        (activity as? HomeActivity)?.showErrorLayout(true, getString(R.string.error_loading_data))
    }

    private fun observeIncidents() {
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
    }

    private fun showDialogOptions() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null)
        val builder = AlertDialog.Builder(requireContext()).setView(dialogView)

        val dialog = builder.create()

        val btnNo = dialogView.findViewById<Button>(R.id.btn_no)
        val btnYes = dialogView.findViewById<Button>(R.id.btn_yes)

        btnNo.setOnClickListener {
            dialog.dismiss()
            navigateToCreateIncidentForm()
        }

        btnYes.setOnClickListener {
            dialog.dismiss()
            navigateToChatbot()
        }

        dialog.show()
    }



    private fun navigateToCreateIncidentForm() {
        val action = IncidentsFragmentDirections.actionIncidentsFragmentToIncidentCreateFragment()
        findNavController().navigate(action)
    }

    private fun navigateToChatbot() {
        val action = IncidentsFragmentDirections.actionIncidentsFragmentToChatbotFragment()
        findNavController().navigate(action)
    }


}