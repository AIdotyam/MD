package com.capstone.aiyam.presentation.core.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.capstone.aiyam.presentation.core.alerts.AlertsFragment
import com.capstone.aiyam.presentation.core.classificationhistory.ClassificationHistoryFragment

class HistoryPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = AlertsFragment()
            1 -> fragment = ClassificationHistoryFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}
