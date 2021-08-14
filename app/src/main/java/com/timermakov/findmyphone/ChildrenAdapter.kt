package com.timermakov.findmyphone

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.timermakov.findmyphone.databinding.ChildItemBinding

class ChildrenAdapter(data: List<Child>) : RecyclerView.Adapter<ChildrenAdapter.ChildHolder>() {
    var data: List<Child> = data
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class ChildHolder(var binding: ChildItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildHolder {
        val binding = ChildItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ChildHolder(binding)
    }

    override fun onBindViewHolder(holder: ChildHolder, position: Int) {
        with(holder) {
            binding.childAddress.text = data[position].address
            binding.childContent.text = data[position].content
            binding.childName.text = data[position].name
            binding.addressLayout.setOnClickListener {
                binding.root.findNavController()
                    .navigate(
                        R.id.action_childrenFragment_to_mapFragment,
                        bundleOf("child_name" to data[position].name)
                    )
            }
        }
    }

    override fun getItemCount() = data.size
}