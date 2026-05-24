package com.example.kwikq.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kwikq.R
import com.example.kwikq.network.models.QueueItem

class QueueAdapter(
    private val items: MutableList<QueueItem>,
    private val listener: OnQueueActionListener? = null
) : RecyclerView.Adapter<QueueAdapter.QueueViewHolder>() {

    private var enabled: Boolean = true

    fun setEnabled(v: Boolean) {
        enabled = v
        notifyDataSetChanged()
    }

    class QueueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvText: TextView = view.findViewById(R.id.tvQueueText)
        val btnRemove: Button = view.findViewById(R.id.btnRemoveFromQueue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_queue, parent, false)
        return QueueViewHolder(view)
    }

    override fun onBindViewHolder(holder: QueueViewHolder, position: Int) {
        val q = items[position]
        holder.tvText.text = "Book ${q.bookId} — Position: ${q.position}"
        holder.btnRemove.isEnabled = enabled
        holder.btnRemove.alpha = if (enabled) 1.0f else 0.5f
        holder.itemView.alpha = if (enabled) 1.0f else 0.6f
        holder.btnRemove.setOnClickListener {
            if (enabled) {
                listener?.onRemove(q) ?: run {
                    Toast.makeText(holder.itemView.context, "Remove action", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<QueueItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun remove(item: QueueItem) {
        val idx = items.indexOfFirst { it.id == item.id }
        if (idx >= 0) {
            items.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }

    interface OnQueueActionListener {
        fun onRemove(item: QueueItem)
    }
}
