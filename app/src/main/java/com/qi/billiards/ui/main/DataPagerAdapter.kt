package com.qi.billiards.ui.main

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class DataPagerAdapter(
    private val fragments: List<PlayerFragment>,
    childFragmentManager: FragmentManager
) : FragmentPagerAdapter(childFragmentManager) {

    override fun getItem(position: Int) = fragments[position]

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = fragments[position].key

}
