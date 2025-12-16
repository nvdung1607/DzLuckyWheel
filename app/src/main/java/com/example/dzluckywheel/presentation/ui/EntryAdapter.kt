package com.example.dzluckywheel.presentation.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dzluckywheel.R
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.model.EntryType

class EntryAdapter(private var entries: List<Entry>) :
    RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    class EntryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEntry: TextView? = view.findViewById(R.id.tvEntry)
        val ivEntry: ImageView? = view.findViewById(R.id.ivEntry)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_entry, parent, false)
        return EntryViewHolder(view)
    }

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val entry = entries[position]
        if (entry.type == EntryType.TEXT) {
            holder.tvEntry?.visibility = View.VISIBLE
            holder.ivEntry?.visibility = View.GONE
            holder.tvEntry?.text = entry.value
        } else {
            holder.tvEntry?.visibility = View.GONE
            holder.ivEntry?.visibility = View.VISIBLE
            holder.ivEntry?.setImageURI(Uri.parse(entry.value))
        }
    }

    override fun getItemCount(): Int = entries.size

    fun updateData(newEntries: List<Entry>) {
        entries = newEntries
        notifyDataSetChanged()
    }
}
