package com.capstone.aiyam.presentation.core.alerts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.aiyam.databinding.ItemAlertHistoryBinding
import com.capstone.aiyam.databinding.ItemHeaderBinding
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.utils.parseDateTime

class AlertsAdapter(
    val onClick: (Alerts) -> Unit
) : ListAdapter<AlertsDisplayItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    inner class AlertViewHolder(
        val binding: ItemAlertHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alerts: Alerts){ binding.apply {
            tvMessage.text = alerts.category
            tvTimestamp.text = alerts.createdAt.parseDateTime()
            btnDetail.setOnClickListener { onClick(alerts) }
        }}
    }

    class HeaderViewHolder(
        val binding: ItemHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: AlertsDisplayItem.Header) {
            binding.headerTextView.text = header.date
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val context = parent.context
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemHeaderBinding.inflate(LayoutInflater.from(context), parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_ITEM -> {
                val binding = ItemAlertHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
                AlertViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is AlertsDisplayItem.Header -> (holder as HeaderViewHolder).bind(item)
            is AlertsDisplayItem.Item -> (holder as AlertViewHolder).bind(item.alerts)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AlertsDisplayItem.Header -> VIEW_TYPE_HEADER
            is AlertsDisplayItem.Item -> VIEW_TYPE_ITEM
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<AlertsDisplayItem>() {
            override fun areItemsTheSame(oldItem: AlertsDisplayItem, newItem: AlertsDisplayItem): Boolean {
                return when {
                    oldItem is AlertsDisplayItem.Header && newItem is AlertsDisplayItem.Header -> oldItem.date == newItem.date
                    oldItem is AlertsDisplayItem.Item && newItem is AlertsDisplayItem.Item -> oldItem.alerts.id == newItem.alerts.id
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: AlertsDisplayItem, newItem: AlertsDisplayItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
