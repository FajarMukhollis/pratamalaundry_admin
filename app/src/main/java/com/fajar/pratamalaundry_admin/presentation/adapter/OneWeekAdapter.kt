package com.fajar.pratamalaundry_admin.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.model.response.OneWeeksResponse

class OneWeekAdapter(
    private val results: ArrayList<OneWeeksResponse.OneWeek>
) : RecyclerView.Adapter<OneWeekAdapter.ViewHolderRecapOneWeek>(
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRecapOneWeek {
        return ViewHolderRecapOneWeek(
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_one_week, parent, false)
        )
    }

    override fun getItemCount(): Int = results.size

    override fun onBindViewHolder(holder: ViewHolderRecapOneWeek, position: Int) {
        val result = results[position]
        holder.apply {
            dateOrder.text = result.tgl_order
            nameCustomer.text = result.nama_pelanggan
            nameProduct.text = result.nama_produk
            totalPrice.text = result.total_harga
        }
    }

    inner class ViewHolderRecapOneWeek(view: View) : RecyclerView.ViewHolder(view) {
        val dateOrder = view.findViewById<android.widget.TextView>(R.id.tv_date_order)
        val nameCustomer = view.findViewById<android.widget.TextView>(R.id.tv_name_customer)
        val nameProduct = view.findViewById<android.widget.TextView>(R.id.tv_name_product)
        val totalPrice = view.findViewById<android.widget.TextView>(R.id.tv_total_price)
    }

    fun setData(data: List<OneWeeksResponse.OneWeek>) {
        results.clear()
        results.addAll(data)
        notifyDataSetChanged()
    }

}
