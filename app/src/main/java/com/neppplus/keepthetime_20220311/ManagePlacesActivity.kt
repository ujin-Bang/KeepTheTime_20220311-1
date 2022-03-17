package com.neppplus.keepthetime_20220311

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.keepthetime_20220311.adapters.MyPlacesRecyclerAdapter
import com.neppplus.keepthetime_20220311.databinding.ActivityManagePlacesBinding
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import com.neppplus.keepthetime_20220311.datas.PlaceData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagePlacesActivity : BaseActivity() {

    lateinit var binding: ActivityManagePlacesBinding

    val mPlacesList = ArrayList<PlaceData>()

    lateinit var mPlaceAdapter : MyPlacesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_manage_places)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        mPlaceAdapter = MyPlacesRecyclerAdapter(mContext,mPlacesList)
        binding.myPlacesRecyclerView.adapter = mPlaceAdapter
        binding.myPlacesRecyclerView.layoutManager = LinearLayoutManager(mContext)

    }
   override fun onResume() {

       super.onResume()
       getMyplaceFromServer()

   }

    fun getMyplaceFromServer(){

        apiList.getRequestMyPlaceList().enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if(response.isSuccessful){

                    val br = response.body()!!

                    mPlacesList.clear()

                    mPlacesList.addAll( br.data.places)

                    mPlaceAdapter.notifyDataSetChanged()

                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }
}