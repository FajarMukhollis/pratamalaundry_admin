package com.fajar.pratamalaundry_admin.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.model.response.TransactionResponse

class TransactionAdapter(private val results: ArrayList<TransactionResponse.Data>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolderTransaction>() {

    private lateinit var onDeleteClickListener: OnDeleteClickListener
    private lateinit var onEditClickListener: OnEditClickListener
    private lateinit var onItemClickListener: OnItemClickListener

    inner class ViewHolderTransaction(view: View) : RecyclerView.ViewHolder(view) {
        val tv_no_pesanan = view.findViewById<TextView>(R.id.tv_no_pesanan)
        val tv_date_order = view.findViewById<TextView>(R.id.tv_date_order)
        val name_customer = view.findViewById<TextView>(R.id.tv_name_customer)
        val tv_total_price = view.findViewById<TextView>(R.id.tv_total_price)
        val tv_status_barang = view.findViewById<TextView>(R.id.tv_status_barang)
        val tv_status_bayar = view.findViewById<TextView>(R.id.tv_status_bayar)
        val btn_update = view.findViewById<ImageView>(R.id.btn_update)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(position)
                }
            }

            btn_delete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDeleteClickListener.onDeleteClick(position)
                }
            }

            btn_update.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onEditClickListener.onEditClick(position)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolderTransaction, position: Int) {
        val result = results[position]
        holder.apply {
            tv_no_pesanan.text = result.no_pesanan
            tv_date_order.text = result.tgl_order
            name_customer.text = result.nama_pelanggan
            tv_total_price.text = result.total_harga
            tv_status_barang.text = result.status_barang
            tv_status_bayar.text = result.status_bayar
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTransaction {
        return ViewHolderTransaction(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_row_transaction, parent, false)
        )
    }

    override fun getItemCount(): Int = results.size

    fun setData(data: List<TransactionResponse.Data>) {
        results.clear()
        results.addAll(data)
        notifyDataSetChanged()
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        this.onDeleteClickListener = listener
    }

    fun setOnEditClickListener(listener: OnEditClickListener) {
        this.onEditClickListener = listener
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }

    interface OnEditClickListener {
        fun onEditClick(position: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun getItem(position: Int): TransactionResponse.Data {
        return results[position]
    }
}