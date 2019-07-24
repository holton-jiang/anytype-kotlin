package com.agileburo.anytype.feature_desktop.utils

import androidx.recyclerview.widget.DiffUtil
import com.agileburo.anytype.feature_desktop.mvvm.DesktopView

class DesktopDiffUtil(
    private val old : List<DesktopView>,
    private val new : List<DesktopView>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldItem = old[oldItemPosition]
        val newItem = new[newItemPosition]

        if (oldItem is DesktopView.NewDocument && newItem is DesktopView.NewDocument)
            return true
        else {
            if (oldItem is DesktopView.Document && newItem is DesktopView.Document) {
                return oldItem.id == newItem.id
            } else {
                if (oldItem is DesktopView.NewDocument && newItem is DesktopView.Document)
                    return false
                if (oldItem is DesktopView.Document && newItem is DesktopView.NewDocument)
                    return false
                else
                    throw IllegalStateException("Unexpected state")
            }
        }
    }

    override fun getOldListSize(): Int = old.size
    override fun getNewListSize(): Int = new.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = old[oldItemPosition]
        val newItem = new[newItemPosition]

        return if (oldItem is DesktopView.NewDocument) {
            true
        } else {
            (oldItem as DesktopView.Document) == (newItem as DesktopView.Document)
        }
    }
}