package com.example.thesis.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.kitprotocol.kitinterface.MovieProtocol.Item.FooterItem
import kotlinx.android.synthetic.main.footer_item.view.textView_footer

class FooterItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    fun bind(footerItem: FooterItem) = with(view) { textView_footer.text = footerItem.message }
}