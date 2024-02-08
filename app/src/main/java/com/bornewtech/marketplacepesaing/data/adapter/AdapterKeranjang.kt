package com.bornewtech.marketplacepesaing.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.firestoreDb.CartItem
import com.bumptech.glide.Glide

class AdapterKeranjang(
    private val cartItems: MutableList<CartItem>,
    private val onItemClick: (CartItem) -> Unit = {},
    private val onIncrementClick: (CartItem, Int) -> Unit = { _, _ -> },
    private val onDecrementClick: (CartItem, Int) -> Unit = { _, _ -> }
) : RecyclerView.Adapter<AdapterKeranjang.CartViewHolder>() {

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pedagangId: TextView = itemView.findViewById(R.id.pedagangId)
        val productName: TextView = itemView.findViewById(R.id.namaBarangCart)
        val quantity: TextView = itemView.findViewById(R.id.kuantitasBarangCart)
        val price: TextView = itemView.findViewById(R.id.hargaBarangCart)
        val productImage: ImageView = itemView.findViewById(R.id.imageCartProduct) // Tambahkan ImageView untuk menampilkan gambar produk
        val plusButton: ImageView = itemView.findViewById(R.id.imagePlus)
        val minusButton: ImageView = itemView.findViewById(R.id.imageMinus)
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

        // Load image using Glide
        currentItem.imageUrl?.let { imageUrl ->
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.image_baseline) // Placeholder image jika gambar belum selesai dimuat
                .into(holder.productImage)
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }

        holder.plusButton.setOnClickListener {
            onIncrementClick(currentItem, position)
        }

        holder.minusButton.setOnClickListener {
            onDecrementClick(currentItem, position)
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

    // Tambahkan fungsi untuk menghapus item dari RecyclerView
    fun removeItem(position: Int) {
        if (position in 0 until itemCount) {
            cartItems.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}