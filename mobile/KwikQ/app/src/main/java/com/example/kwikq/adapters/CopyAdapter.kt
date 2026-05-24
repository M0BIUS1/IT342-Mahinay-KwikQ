package com.example.kwikq.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kwikq.R
import com.example.kwikq.network.models.BookCopyItem

class CopyAdapter(
    private val items: MutableList<BookCopyItem>,
    private val listener: OnCopyActionListener
) : RecyclerView.Adapter<CopyAdapter.CopyViewHolder>() {

    private var enabled: Boolean = true

    fun setEnabled(v: Boolean) {
        enabled = v
        notifyDataSetChanged()
    }

    class CopyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv: TextView = view.findViewById(R.id.tvCopyText)
        val btnBorrow: Button = view.findViewById(R.id.btnBorrowCopy)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CopyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book_copy, parent, false)
        return CopyViewHolder(view)
    }

    override fun onBindViewHolder(holder: CopyViewHolder, position: Int) {
        val c = items[position]
        holder.tv.text = "${c.copyCode} — ${c.status}"
        val canBorrow = enabled && c.status.equals("AVAILABLE", ignoreCase = true)
        holder.btnBorrow.isEnabled = canBorrow
        holder.btnBorrow.alpha = if (canBorrow) 1.0f else 0.5f
        holder.itemView.alpha = if (enabled) 1.0f else 0.6f
        holder.btnBorrow.setOnClickListener {
            if (enabled) listener.onBorrow(c)
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<BookCopyItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    interface OnCopyActionListener {
        fun onBorrow(item: BookCopyItem)
    }
}
