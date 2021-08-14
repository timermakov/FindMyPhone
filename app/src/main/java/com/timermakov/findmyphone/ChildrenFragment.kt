package com.timermakov.findmyphone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.timermakov.findmyphone.databinding.FragmentOrdersBinding

class ChildrenFragment : Fragment() {
    lateinit var binding: FragmentOrdersBinding
    lateinit var adapter: ChildrenAdapter
    private val children: List<Child> by lazy {
        ChildrenReader(requireContext()).read()!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChildrenAdapter(children)
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(context)
    }
}