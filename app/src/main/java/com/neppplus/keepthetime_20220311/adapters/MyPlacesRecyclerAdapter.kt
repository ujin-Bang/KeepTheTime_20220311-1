package com.neppplus.keepthetime_20220311.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neppplus.keepthetime_20220311.R
import com.neppplus.keepthetime_20220311.api.APIList
import com.neppplus.keepthetime_20220311.api.ServerAPI
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import com.neppplus.keepthetime_20220311.datas.PlaceData
import com.neppplus.keepthetime_20220311.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPlacesRecyclerAdapter(
    val mContext: Context,
    val mList: List<PlaceData>
) : RecyclerView.Adapter<MyPlacesRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val txtStartPlaceName = view.findViewById<TextView>(R.id.txtStartPlaceName)


        fun bind(data: PlaceData) {

            txtStartPlaceName.text = data.name


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.my_place_list_item, parent, false)
        return  MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data = mList[position]
        holder.bind(data)

    }

    override fun getItemCount() = mList.size

}