package com.softwarepharaoh.img2txt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private var historyList = listOf<History>().toMutableList()

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val currentHistory = historyList[position]

        holder.textView.text = HtmlCompat.fromHtml(
            currentHistory.text,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )

        holder.imageView.setImageURI(currentHistory.imageUrl.toUri())
    }

    override fun getItemCount() = historyList.size

    fun updateData(histories: List<History>) {
        historyList.clear()
        historyList.addAll(histories)
        notifyDataSetChanged()
    }
}
