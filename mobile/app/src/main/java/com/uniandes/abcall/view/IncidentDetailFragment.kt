package com.uniandes.abcall.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import com.uniandes.abcall.R
import com.uniandes.abcall.data.model.Incident
import com.uniandes.abcall.databinding.FragmentIncidentDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale


class IncidentDetailFragment : Fragment() {
    private val args: IncidentDetailFragmentArgs by navArgs()

    private var _binding: FragmentIncidentDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIncidentDetailBinding.inflate(inflater, container, false)

        val bar = (activity as? AppCompatActivity)?.supportActionBar
        bar?.title = getString(R.string.title_detail_incident)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val incident: Incident = args.incident

        // Cargamos los datos en la vista
        binding.apply {
            incidentImage.setImageResource(R.drawable.incident)

            incidentSubject.text = incident.subject

            incidentStatus.text = incident.status
            incidentID.text = incident.id.toString()

            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = formatter.format(incident.creationDate)


            incidentCreationDate.text = formattedDate

            incidentDescription.text = incident.description

            // Mostrar la respuesta del agente, si existe
            if (incident.solution.isNullOrEmpty()) {
                agentResponseLabel.visibility = View.GONE
                agentResponse.visibility = View.GONE
            } else {
                agentResponse.text = incident.solution
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}