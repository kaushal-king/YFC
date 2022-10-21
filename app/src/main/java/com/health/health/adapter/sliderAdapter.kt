package com.health.health.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.health.health.R
import com.smarteist.autoimageslider.SliderViewAdapter


class sliderAdapter(val mSliderItems: List<String>,
                    var mCtx: Context
): SliderViewAdapter<sliderAdapter.SliderAdapterViewHolder>() {

     class SliderAdapterViewHolder(itemView: View) : ViewHolder(itemView) {
        // Adapter class for initializing
        // the views of our slider view.

        var imageViewBackground: ImageView=itemView.findViewById(R.id.myimage)
         var itemVieww: View? =itemView

     }

    override fun getCount(): Int {
        return mSliderItems.size;
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
        val inflate =
            LayoutInflater.from(parent!!.context).inflate(R.layout.slider_layout, null)
        return SliderAdapterViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder?, position: Int) {
        var  sliderItem:String = mSliderItems.get(position);

        // Glide is use to load image
        // from url in your imageview.
        Glide.with(viewHolder!!.itemVieww!!)
            .load(sliderItem.toString())
            .fitCenter()
            .into(viewHolder.imageViewBackground);
    }
}