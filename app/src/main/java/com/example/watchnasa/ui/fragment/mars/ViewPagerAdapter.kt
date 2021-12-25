package com.example.watchnasa.ui.fragment.mars

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.watchnasa.repository.dto.RoverResponseData

// адаптер для ViewPager
// кроме необходимых параметров в конструктор передаем список с фотоданными марса за выбранную дату
class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val photoDataList: List<RoverResponseData>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount() = photoDataList.size

    override fun createFragment(position: Int): Fragment {
        // для каждого таба создаем экземпляр фрагмента MarsPhotoFragment
        // в качестве аргументов передаем позицию таба и список с фотоданными
        return MarsPhotoFragment.newInstance(position, photoDataList)
    }
}