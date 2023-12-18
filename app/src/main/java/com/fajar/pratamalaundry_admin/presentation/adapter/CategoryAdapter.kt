package com.fajar.pratamalaundry_admin.presentation.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.model.response.GetCategoryResponse

class CategoryAdapter(
    private val results: ArrayList<GetCategoryResponse.DataCategory>
) : RecyclerView.Adapter<CategoryAdapter.ViewHolderCategory>() {

    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var onEditClickListener: OnEditClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            CategoryAdapter.ViewHolderCategory {
        return ViewHolderCategory(
            View.inflate(parent.context, R.layout.item_row_category, null)
        )
    }

    override fun onBindViewHolder(holder: CategoryAdapter.ViewHolderCategory, position: Int) {
        val result = results[position]
        holder.apply {
            id_kategori.text = result.id_kategori
            jenis_kategori.text = result.jenis_kategori

            btn_delete.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }

            btn_edit.setOnClickListener {
                onEditClickListener.onEditClick(position)
            }
        }
    }

    override fun getItemCount(): Int = results.size

    inner class ViewHolderCategory(view: View) : RecyclerView.ViewHolder(view) {
        val id_kategori = view.findViewById<TextView>(R.id.tv_id_category)
        val jenis_kategori = view.findViewById<TextView>(R.id.tv_category)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete_category)
        val btn_edit = view.findViewById<ImageView>(R.id.btn_edit_category)
    }

    fun setData(data: List<GetCategoryResponse.DataCategory>) {
        results.clear()
        results.addAll(data)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): GetCategoryResponse.DataCategory = results[position]

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