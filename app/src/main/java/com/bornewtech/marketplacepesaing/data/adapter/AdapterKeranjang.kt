package com.bornewtech.marketplacepesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem

class AdapterKeranjang(
    private val cartItems: MutableList<CartItem>,
    private val onItemClick: (CartItem) -> Unit = {},
    private val onIncrementClick: (CartItem) -> Unit = {},
    private val onDecrementClick: (CartItem) -> Unit = {}
) : RecyclerView.Adapter<AdapterKeranjang.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pedagangId: TextView = itemView.findViewById(R.id.pedagangId)
        val productName: TextView = itemView.findViewById(R.id.namaBarangCart)
        val quantity: TextView = itemView.findViewById(R.id.kuantitasBarangCart)
        val price: TextView = itemView.findViewById(R.id.hargaBarangCart)
        val plusButton: ImageView = itemView.findViewById(R.id.imagePlus)
        val minusButton: ImageView = itemView.findViewById(R.id.imageMinus)

        // Perbaiki deklarasi pedagangId di dalam CartViewHolder
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_cartproduk, parent, false)
        return CartViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = cartItems[position]

        // Bind data to the view
        holder.pedagangId.text = "Pedagang ID: ${currentItem.pedagangId}"
        holder.productName.text = currentItem.productName
        holder.quantity.text = "Jumlah: ${currentItem.productQuantity}" // Menggunakan properti productQuantity
        holder.price.text = "Harga per Barang: Rp ${currentItem.productPrice}"

        // Tambahkan log untuk memeriksa nilai quantity
//        Log.d("AdapterKeranjang", "Quantity: ${currentItem.productQuantity}")

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }

        holder.plusButton.setOnClickListener {
            onIncrementClick(currentItem)
        }

        // Handle minus button click
        holder.minusButton.setOnClickListener {
            onDecrementClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    // Update data method (you can call this method to update the adapter's data)
    fun updateData(newCartItems: List<CartItem>) {
        cartItems.clear()
        cartItems.addAll(newCartItems)
        notifyDataSetChanged()
    }
}
