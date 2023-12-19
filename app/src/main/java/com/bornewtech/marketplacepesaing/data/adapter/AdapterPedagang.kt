package com.bornewtech.marketplacepesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.firestoreDb.Pedagang

class AdapterPedagang(
    private var pedagangList: List<Pedagang>,
    private val onItemClick: (Pedagang) -> Unit
) : RecyclerView.Adapter<AdapterPedagang.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_pedagang, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pedagang = pedagangList[position]
        holder.bind(pedagang)
        holder.itemView.setOnClickListener {
            onItemClick(pedagang)
        }
    }

    override fun getItemCount(): Int {
        return pedagangList.size
    }

    // Add this function to update data in the adapter
    fun updateData(newPedagangList: List<Pedagang>) {
        pedagangList = newPedagangList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(pedagang: Pedagang) {
            itemView.findViewById<TextView>(R.id.namaPedagang).text = pedagang.namaLengkap
            itemView.findViewById<TextView>(R.id.statusTerkini).text = pedagang.alamatLengkap
            itemView.findViewById<TextView>(R.id.lokasiTerkini).text = pedagang.noHpAktif
            // You can add more bindings here based on your layout
        }
    }
}