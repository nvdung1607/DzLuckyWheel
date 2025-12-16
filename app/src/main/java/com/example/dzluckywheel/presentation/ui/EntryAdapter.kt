package com.example.dzluckywheel.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dzluckywheel.R
import com.example.dzluckywheel.data.model.Entry

class EntryAdapter(private var entries: List<Entry>) :
    RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEntry: TextView = view.findViewById(R.id.tvEntry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        holder.tvEntry.text = entries[position].value
    }

    override fun getItemCount(): Int = entries.size

    fun updateData(newEntries: List<Entry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}
