package com.shridhar.institutegrievancemanagementapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.shridhar.institutegrievancemanagementapp.R
import com.shridhar.institutegrievancemanagementapp.databinding.FragmentComplaintBinding

class ComplaintFragment : Fragment() {

    private lateinit var binding: FragmentComplaintBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_complaint, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.raiseTicket.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, RaiseTicketFragment())
                ?.addToBackStack(null)
                ?.commit()
        }

        binding.checkStatus.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.fragment_container, CheckTicketStatusFragment())
                ?.addToBackStack(null)
                ?.commit()
        }
    }
}