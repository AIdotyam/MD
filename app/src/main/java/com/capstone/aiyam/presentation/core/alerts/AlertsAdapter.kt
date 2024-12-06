package com.capstone.aiyam.presentation.core.alerts

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.aiyam.R
import com.capstone.aiyam.databinding.ItemAlertHistoryBinding
import com.capstone.aiyam.databinding.ItemHeaderBinding
import com.capstone.aiyam.domain.model.Alerts
import com.capstone.aiyam.utils.getMimeTypeFromUrl
import com.capstone.aiyam.utils.getRandomDead
import com.capstone.aiyam.utils.parseDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlertsAdapter(
    val onClick: (Alerts) -> Unit
) : ListAdapter<AlertsDisplayItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    inner class AlertViewHolder(
        val context: Context,
        val binding: ItemAlertHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(alerts: Alerts) {
            binding.apply {
                tvMessage.text = getRandomDead()
                tvTimestamp.text = alerts.createdAt.parseDateTime()

                val mediaUrl = alerts.mediaUrl
                val mimeType = mediaUrl.getMimeTypeFromUrl()

                if (mimeType != null && mimeType.startsWith("video")) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val thumbnail = getVideoThumbnail(mediaUrl)
                        Glide.with(context)
                            .load(thumbnail)
                            .error(R.drawable.video)
                            .placeholder(R.drawable.video)
                            .into(iconWarning)
                    }
                } else {
                    Glide.with(context)
                        .load(mediaUrl)
                        .error(R.drawable.image)
                        .placeholder(R.drawable.image)
                        .into(iconWarning)
                }

                btnDetail.setOnClickListener { onClick(alerts) }
            }
        }

        private suspend fun getVideoThumbnail(videoUrl: String): Bitmap? {
            return withContext(Dispatchers.IO) {
                try {
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(videoUrl, HashMap<String, String>())
                    retriever.getFrameAtTime(1000, MediaMetadataRetriever.OPTION_CLOSEST)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
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
                AlertViewHolder(context, binding)
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
                    oldItem is AlertsDisplayItem.Item && newItem is AlertsDisplayItem.Item -> oldItem.alerts == newItem.alerts
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: AlertsDisplayItem, newItem: AlertsDisplayItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
