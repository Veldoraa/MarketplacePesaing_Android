package com.bornewtech.marketplacepesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bornewtech.marketplacepesaing.data.firestoreDb.Transaction
import com.bornewtech.marketplacepesaing.databinding.ListTransaksiBinding

class AdapterTransaksi(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<AdapterTransaksi.ViewHolder>() {

    class ViewHolder(private val binding: ListTransaksiBinding) : RecyclerView.ViewHolder(binding.root) {
        // Deklarasikan view di sini (jika perlu)
        fun bind(cartItem: CartItem, ) {
            binding.namaBarangTransaksi.text = cartItem.productName
            binding.statusTransaksi.text = "Rp ${cartItem.productPrice},00"
            binding.jumlahBarangPerTransaksi.text = "Qty: ${cartItem.productQuantity}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = cartItems[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }
}
