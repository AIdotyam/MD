package com.capstone.aiyam.presentation.core.classificationhistory

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
import com.capstone.aiyam.databinding.ItemClassificationHistoryBinding
import com.capstone.aiyam.databinding.ItemHeaderBinding
import com.capstone.aiyam.domain.model.Classification
import com.capstone.aiyam.utils.parseDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ClassificationHistoryAdapter(
    val onClick: (Classification) -> Unit
) : ListAdapter<ClassificationHistoryDisplayItem, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    class ClassificationHistoryViewHolder(
        val context: Context,
        val binding: ItemClassificationHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(classification: Classification) {
            binding.apply {
                tvTimestamp.text = classification.createdAt.parseDateTime()

                val mediaUrl = classification.mediaUrl
                if (mediaUrl.isNotEmpty()) {
                    if (mediaUrl.endsWith(".mp4") || mediaUrl.endsWith(".mov") || mediaUrl.endsWith(".avi") || mediaUrl.endsWith(".mkv")) {
                        CoroutineScope(Dispatchers.Main).launch {
                            val thumbnail = getVideoThumbnail(mediaUrl)
                            Glide.with(context)
                                .load(thumbnail)
                                .centerCrop()
                                .error(R.drawable.video)
                                .placeholder(R.drawable.video)
                                .into(ivHistory)
                        }
                    } else {
                        Glide.with(context)
                            .load(mediaUrl)
                            .centerCrop()
                            .error(R.drawable.video)
                            .placeholder(R.drawable.image)
                            .into(ivHistory)
                    }
                } else {
                    Glide.with(context)
                        .load(R.drawable.image)
                        .into(ivHistory)
                }
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
        fun bind(header: ClassificationHistoryDisplayItem.Header) {
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
                val binding = ItemClassificationHistoryBinding.inflate(LayoutInflater.from(context), parent, false)
                ClassificationHistoryViewHolder(context, binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ClassificationHistoryDisplayItem.Header -> (holder as HeaderViewHolder).bind(item)
            is ClassificationHistoryDisplayItem.Item -> {
                (holder as ClassificationHistoryViewHolder).apply {
                    bind(item.classification)

                    itemView.setOnClickListener{
                        onClick(item.classification)
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ClassificationHistoryDisplayItem.Header -> VIEW_TYPE_HEADER
            is ClassificationHistoryDisplayItem.Item -> VIEW_TYPE_ITEM
        }
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ClassificationHistoryDisplayItem>() {
            override fun areItemsTheSame(oldItem: ClassificationHistoryDisplayItem, newItem: ClassificationHistoryDisplayItem): Boolean {
                return when {
                    oldItem is ClassificationHistoryDisplayItem.Header && newItem is ClassificationHistoryDisplayItem.Header -> oldItem.date == newItem.date
                    oldItem is ClassificationHistoryDisplayItem.Item && newItem is ClassificationHistoryDisplayItem.Item -> oldItem.classification == newItem.classification
                    else -> false
                }
            }

            override fun areContentsTheSame(oldItem: ClassificationHistoryDisplayItem, newItem: ClassificationHistoryDisplayItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
