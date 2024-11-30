package com.capstone.aiyam.presentation.core.classificationhistory

import com.capstone.aiyam.domain.model.Classification

sealed class ClassificationHistoryDisplayItem  {
    data class Header(val date: String) : ClassificationHistoryDisplayItem()
    data class Item(val classification: Classification) : ClassificationHistoryDisplayItem()
}
