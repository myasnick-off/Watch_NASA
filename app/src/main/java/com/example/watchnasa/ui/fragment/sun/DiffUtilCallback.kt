package com.example.watchnasa.ui.fragment.sun

import androidx.recyclerview.widget.DiffUtil
import com.example.watchnasa.repository.dto.SolarFlareResponseData

class DiffUtilCallback(
    private var oldItems: List<SolarFlareResponseData>,
    private var newItems: List<SolarFlareResponseData>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].flrID == newItems[newItemPosition].flrID
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
         return oldItem == newItem
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldItems[oldItemPosition]
        val newItem = newItems[newItemPosition]
        return Change(oldItem, newItem)
    }
}