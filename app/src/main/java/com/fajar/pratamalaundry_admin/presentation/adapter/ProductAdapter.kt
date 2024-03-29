package com.fajar.pratamalaundry_admin.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.model.response.ProductResponse

class ProductAdapter(
    private val results: ArrayList<ProductResponse.Product>
) : RecyclerView.Adapter<ProductAdapter.ViewHolderProduct>() {

    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var onEditClickListener: OnEditClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderProduct {
        return ViewHolderProduct(
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_product, parent, false)
        )
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolderProduct, position: Int) {
        val result = results[position]
        holder.apply {
            result.id_kategori
            kategori.text = result.jenis_kategori
            nama_produk.text = result.nama_produk
            durasi.text = result.durasi
            harga_produk.text = result.harga_produk
            satuan.text = result.satuan



            btn_delete.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }

            btn_edit.setOnClickListener {
                onEditClickListener.onEditClick(position)
            }
        }
    }

    inner class ViewHolderProduct(view: View) : RecyclerView.ViewHolder(view) {
        val kategori: TextView = view.findViewById(R.id.tv_category)
        val nama_produk: TextView = view.findViewById(R.id.tv_produk)
        val durasi: TextView = view.findViewById(R.id.tv_durasi)
        val harga_produk: TextView = view.findViewById(R.id.tv_harga)
        val satuan: TextView = view.findViewById(R.id.tv_satuan)
        val btn_delete: ImageView = view.findViewById(R.id.btn_delete)
        val btn_edit: ImageView = view.findViewById(R.id.btn_edit)
    }

    fun setData(data: List<ProductResponse.Product>) {
        results.clear()
        results.addAll(data)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ProductResponse.Product {
        return results[position]
    }

    fun removeItem(id_produk: String) {
        val iterator = results.iterator()
        while (iterator.hasNext()) {
            val product = iterator.next()
            if (product.id_product == id_produk) {
                iterator.remove()
                notifyItemRemoved(results.indexOf(product))
                break
            }
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    fun setOnEditClickListener(listener: OnEditClickListener) {
        this.onEditClickListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnEditClickListener {
        fun onEditClick(position: Int)
    }
}
