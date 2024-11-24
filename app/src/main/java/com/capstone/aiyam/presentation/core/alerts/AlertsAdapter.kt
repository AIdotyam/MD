package com.capstone.aiyam.presentation.core.alerts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.aiyam.databinding.ItemAlertHistoryBinding
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.utils.parseDateTime

class AlertsAdapter(
    val onClick: (Alerts) -> Unit
) : ListAdapter<Alerts, AlertsAdapter.MyViewHolder>(DIFF_CALLBACK) {
    inner class MyViewHolder(
        val binding: ItemAlertHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alerts: Alerts){ binding.apply {
            tvMessage.text = alerts.category
            tvTimestamp.text = alerts.createdAt.parseDateTime()
            btnDetail.setOnClickListener { onClick(alerts) }
        }}
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val context = parent.context
        val binding = ItemAlertHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alert = getItem(position)
        holder.bind(alert)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Alerts>() {
            override fun areItemsTheSame(oldItem: Alerts, newItem: Alerts): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Alerts, newItem: Alerts): Boolean {
                return oldItem == newItem
            }
        }
    }
}
