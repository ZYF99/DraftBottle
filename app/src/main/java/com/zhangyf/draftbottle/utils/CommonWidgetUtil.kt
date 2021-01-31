package com.zhangyf.draftbottle.utils

import android.content.Context
import com.zhangyf.draftbottle.ui.adapter.ImagePagerAdapter
import com.zhangyf.draftbottle.R
import com.zhangyf.draftbottle.widget.FullScreenDialog
import kotlinx.android.synthetic.main.pop_gallery.*


fun showGallery(context: Context, imgList: List<String>, currentPosition: Int) {
	val gallery = FullScreenDialog(context, R.layout.pop_gallery)
	gallery.show()
	gallery.viewpager.adapter =
		ImagePagerAdapter(context, imgList, gallery.viewpager) { gallery.dismiss() }
	gallery.viewpager.currentItem = currentPosition
}