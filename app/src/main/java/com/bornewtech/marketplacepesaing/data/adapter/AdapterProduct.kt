package com.bornewtech.marketplacepesaing.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bornewtech.marketplacepesaing.R
import com.bornewtech.marketplacepesaing.data.firestoreDb.ProductItem
import com.bumptech.glide.Glide

class AdapterProduct(
    private var productList: List<ProductItem>,
    private val itemClickListener: (ProductItem) -> Unit
) : RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {

    class ProductViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val namaBarang: TextView = itemView.findViewById(R.id.namaBarang)
        val stokBarang: TextView = itemView.findViewById(R.id.stokBarang)
        val hargaBarang: TextView = itemView.findViewById(R.id.hargaBarang)
        val gambarBarang: ImageView = itemView.findViewById(R.id.ivGambarRecViewBarang)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
        return ProductViewHolder(itemView).apply {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    itemClickListener(productList[position])
                }
            }
        }
    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
//        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_produk, parent, false)
//        return ProductViewHolder(itemView)
//    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productItem: ProductItem = productList[position]
        holder.namaBarang.text = productItem.produkNama.toString()
        holder.stokBarang.text = productItem.produkStok.toString()
        holder.hargaBarang.text = productItem.produkHarga.toString()

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(productItem.imageUrl)
            .placeholder(R.drawable.image_baseline)
            .into(holder.gambarBarang)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<ProductItem>) {
        productList = newList
        notifyDataSetChanged()
    }
}
