package com.fajar.pratamalaundry_admin.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fajar.pratamalaundry_admin.R
import com.fajar.pratamalaundry_admin.model.response.RulesKomplainResponse

class RulesKomplainAdapter(
    private val results: ArrayList<RulesKomplainResponse.DataRulesKomplain>
) : RecyclerView.Adapter<RulesKomplainAdapter.ViewHolderRulesKomplain>() {

    private lateinit var onItemClickListener: OnItemClickListener
    private lateinit var onEditClickListener: OnEditClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RulesKomplainAdapter.ViewHolderRulesKomplain {
        return ViewHolderRulesKomplain(
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_rules, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RulesKomplainAdapter.ViewHolderRulesKomplain, position: Int) {
        val result = results[position]
        holder.apply {
            id_rules.text = result.idRulesKomplain
            aturan.text = result.aturan

            btn_delete.setOnClickListener {
                onItemClickListener.onItemClick(position)
            }

            btn_edit.setOnClickListener {
                onEditClickListener.onEditClick(position)
            }
        }
    }

    override fun getItemCount(): Int = results.size

    inner class ViewHolderRulesKomplain(view: View) : RecyclerView.ViewHolder(view) {
        val id_rules = view.findViewById<TextView>(R.id.tv_id_rules)
        val aturan = view.findViewById<TextView>(R.id.tv_rules)
        val btn_delete = view.findViewById<ImageView>(R.id.btn_delete_rules)
        val btn_edit = view.findViewById<ImageView>(R.id.btn_edit_rules)
    }

    fun setData(data: List<RulesKomplainResponse.DataRulesKomplain>) {
        results.clear()
        results.addAll(data)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): RulesKomplainResponse.DataRulesKomplain = results[position]

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