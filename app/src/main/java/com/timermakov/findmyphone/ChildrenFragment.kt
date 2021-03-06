package com.timermakov.findmyphone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.timermakov.findmyphone.databinding.FragmentChildrenBinding

class ChildrenFragment : Fragment() {
    lateinit var binding: FragmentChildrenBinding
    lateinit var adapter: ChildrenAdapter
    private val children: List<Child> by lazy {
        ChildrenReader(requireContext()).read()!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChildrenBinding.inflate(inflater, container, false)

        /*
        binding.children.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        */

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChildrenAdapter(children)
        binding.rv.adapter = adapter
        binding.rv.layoutManager = LinearLayoutManager(context)
    }
}