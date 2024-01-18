package com.bornewtech.marketplacepesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.firestoreDb.Transaction
import com.bornewtech.marketplacepesaing.ui.transaksi.Transaksi

class AdapterTransaksi(private val transaksiList: List<Transaction>) :
    RecyclerView.Adapter<AdapterTransaksi.TransaksiViewHolder>() {

    class TransaksiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaBarangTransaksi: TextView = itemView.findViewById(R.id.namaBarangTransaksi)
        val statusTransaksi: TextView = itemView.findViewById(R.id.statusTransaksi)
        val jumlahBarangPerTransaksi: TextView = itemView.findViewById(R.id.jumlahBarangPerTransaksi)
        val pedagangId: TextView = itemView.findViewById(R.id.pedagangId)
        val pembeliId: TextView = itemView.findViewById(R.id.pembeliId)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaksiViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_transaksi, parent, false)
        return TransaksiViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransaksiViewHolder, position: Int) {
        val transaksi = transaksiList[position]

        // Set nilai view dengan data transaksi
        holder.namaBarangTransaksi.text = transaksi.namaBarang ?: "Nama Barang Tidak Tersedia"
        holder.statusTransaksi.text = transaksi.status ?: "Status Tidak Tersedia"
        holder.jumlahBarangPerTransaksi.text = transaksi.jumlahBarang?.toString() ?: "0"
        holder.pedagangId.text = transaksi.pedagangId ?: "Pedagang ID Tidak Tersedia"
        holder.pembeliId.text = transaksi.pembeliId ?: "Pembeli ID Tidak Tersedia"
    }

    override fun getItemCount(): Int {
        return transaksiList.size
    }
}

