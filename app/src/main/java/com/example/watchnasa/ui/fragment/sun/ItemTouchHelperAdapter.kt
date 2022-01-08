package com.example.watchnasa.ui.fragment.sun

interface ItemTouchHelperAdapter {
    fun onItemMove(from: Int, to: Int)
    fun onItemDismiss(position: Int)
}

interface ItemTouchHelperViewHolder {
    fun onItemSelected()
    fun onItemCleared()
}