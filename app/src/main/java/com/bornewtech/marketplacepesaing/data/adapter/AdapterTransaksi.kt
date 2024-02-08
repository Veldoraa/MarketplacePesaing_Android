package com.bornewtech.marketplacepesaing.data.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bornewtech.marketplacepesaing.databinding.ListTransaksiBinding
import com.bumptech.glide.Glide

class AdapterTransaksi(private val cartItems: List<CartItem>) :
    RecyclerView.Adapter<AdapterTransaksi.ViewHolder>() {

    class ViewHolder(private val binding: ListTransaksiBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            // Bind data to views
            binding.namaBarangTransaksi.text = cartItem.productName
            binding.statusTransaksi.text = "Rp ${cartItem.productPrice},00"
            binding.jumlahBarangPerTransaksi.text = "Qty: ${cartItem.productQuantity}"

            // Load image using Glide
            Glide.with(binding.root)
                .load(cartItem.imageUrl)
                .placeholder(R.drawable.image_baseline) // Placeholder image while loading
                .into(binding.imageCartProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ListTransaksiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
