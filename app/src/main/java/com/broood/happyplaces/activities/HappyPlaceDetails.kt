package com.broood.happyplaces.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.broood.happyplaces.R
import com.broood.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_happy_place_details.*

class HappyPlaceDetails : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_details)
        var happyPlaceDetailsModel: HappyPlaceModel ?= null
        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            happyPlaceDetailsModel = intent.getSerializableExtra(
                MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }
        if(happyPlaceDetailsModel != null){
            setSupportActionBar(toolbar_happy_place_detail)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = happyPlaceDetailsModel.title
            toolbar_happy_place_detail.setNavigationOnClickListener{onBackPressed()}
            iv_place_image.setImageURI(Uri.parse(happyPlaceDetailsModel.desc))
            tv_description.setText(happyPlaceDetailsModel.image)
            tv_location.setText(happyPlaceDetailsModel.Location)
        }
        btn_view_on_map.setOnClickListener {
            val intent = Intent(this@HappyPlaceDetails, MapActivity::class.java)
            intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS, happyPlaceDetailsModel)
            startActivity(intent)
        }
    }
}