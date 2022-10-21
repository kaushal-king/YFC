package com.health.health.adapter

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.health.health.ConstantHelper
import com.health.health.JymDetails
import com.health.health.R
import com.health.health.dataclass.Gym
import java.text.DecimalFormat


class jymlistadapter (private val mList: List<Gym>,
                      var mCtx: Context
) : RecyclerView.Adapter<jymlistadapter.ViewHolder>() {






    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_jym_card, parent, false)
        val viewHolder = ViewHolder(view)

        viewHolder.get_access.setOnClickListener{
            val intent = Intent(mCtx, JymDetails::class.java)
            val model = mList.get(viewHolder.adapterPosition)
            val bundle = Bundle()
            bundle.putSerializable("jymdetail",model )
            intent.putExtra("jymdetail", bundle)
            mCtx.startActivity(intent)
        }

        viewHolder.cardview.setOnClickListener {
            val intent = Intent(mCtx, JymDetails::class.java)
            val model = mList.get(viewHolder.adapterPosition)
            val bundle = Bundle()
            bundle.putSerializable("jymdetail",model )
            intent.putExtra("jymdetail", bundle)
            mCtx.startActivity(intent)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mList[position]
        holder.title.text=item.name
        holder.location.text=item.location

        try{
            val startPoint = Location("locationA")
            startPoint.setLatitude(ConstantHelper.location!!.latitude)
            startPoint.setLongitude(ConstantHelper.location!!.longitude)

            val endPoint = Location("locationA")
            endPoint.latitude = item.coordinates.get( 0)
            endPoint.longitude = item.coordinates.get(1)

            var distance=startPoint.distanceTo(endPoint)
            var km= DecimalFormat("#.##").format(distance/1000)

            holder.distance.text=km.toString()+ " km"
        }catch (e:Exception)
        {
            holder.distance.text="Not Found"
        }

        Glide.with(mCtx).load(item.logo)
            .placeholder(R.drawable.imageloading).error(R.drawable.imageloading)
            .into(holder.small_logo)

        Glide.with(mCtx).load(item.images.get(1))
            .placeholder(R.drawable.imageloading).error(R.drawable.imageloading)
            .into(holder.logo)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.tv_title)
        var cardview: CardView = itemView.findViewById(R.id.cv_main)
        var location: TextView = itemView.findViewById(R.id.tv_location)
        var distance: TextView = itemView.findViewById(R.id.tv_km)
        var get_access: TextView = itemView.findViewById(R.id.tv_get_acess)
        var small_logo: ImageView = itemView.findViewById(R.id.iv_small_logo)
        var logo: ImageView = itemView.findViewById(R.id.iv_gym_logo)


    }
}