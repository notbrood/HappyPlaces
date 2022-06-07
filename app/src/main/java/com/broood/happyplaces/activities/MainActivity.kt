package com.broood.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.broood.happyplaces.R
import com.broood.happyplaces.database.DatabaseHandler
import com.broood.happyplaces.models.HappyPlaceModel
import com.happyplaces.adapters.HappyPlacesAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fabAdd = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(
            R.id.fabAddHappyPlace
        )
        fabAdd.setOnClickListener{
            val intent = Intent(this, AddHappyPlaces::class.java)
            startActivityForResult(intent, ADD_PLACE_ACTIVITY_REQUEST_CODE)
        }
        getHappyPlacesListFromLocalDB()
    }
    private fun getHappyPlacesListFromLocalDB() {

        val dbHandler = DatabaseHandler(this)

        val getHappyPlacesList = dbHandler.getHappyPlacesList()

        if (getHappyPlacesList.size > 0) {
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoRecordAvailable.visibility = View.GONE
            setupHappyPlacesRecyclerView(getHappyPlacesList)
        }
        else{
            rvHappyPlacesList.visibility = View.GONE
            tvNoRecordAvailable.visibility = View.VISIBLE
        }
    }
    private fun setupHappyPlacesRecyclerView(happyPlacesList: ArrayList<HappyPlaceModel>){
        rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)
        val placesAdapter = HappyPlacesAdapter(this, happyPlacesList)
        rvHappyPlacesList.adapter = placesAdapter
        placesAdapter.setOnClickListener(object :
            HappyPlacesAdapter.OnClickListener {
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent = Intent(this@MainActivity, HappyPlaceDetails::class.java)
                intent.putExtra(EXTRA_PLACE_DETAILS, model)
                startActivity(intent)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE){
            if(resultCode == Activity.RESULT_OK){
                getHappyPlacesListFromLocalDB()
            }
            else{
                Log.e("Activity", "back daba diya")
            }
        }
    }
    companion object{
        var ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
        var EXTRA_PLACE_DETAILS = "EXTRA_PLACE_DETAILS"
    }
}