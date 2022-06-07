package com.broood.happyplaces.activities

import android.Manifest
import android.R.attr
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import com.broood.happyplaces.R
import com.broood.happyplaces.database.DatabaseHandler
import com.broood.happyplaces.models.HappyPlaceModel
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_places.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class AddHappyPlaces : AppCompatActivity(){
    private var cal = Calendar.getInstance()
    private var et_date : AppCompatEditText? = null
    private var selectedDate: String? = null
    private var saveImageToInternalStorage : Uri? = null
    private var mLatitude : Double = 0.0
    private var mLongitutde : Double = 0.0
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_places)
        et_date = findViewById(R.id.et_date)
        val toolbar_add_place : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_add_place)
        setSupportActionBar(toolbar_add_place)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar_add_place.setNavigationOnClickListener{
            onBackPressed()
        }
        if(!Places.isInitialized()){
            Places.initialize(this@AddHappyPlaces, resources.getString(R.string.gmapAPI))
        }
        et_date?.setOnClickListener {
            (clickDatePicker())
        }
        tv_add_image?.setOnClickListener{
            acha()
        }
        btn_save.setOnClickListener{
            saveKarDo()
        }
        et_location.setOnClickListener{locationChooseKaro()}
        tv_select_current_location.setOnClickListener{currentLocation()}
    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun currentLocation() {
        if (!isLocationEnabled()) {
            Toast.makeText(
                this,
                "Your location provider is turned off. Please turn it on.",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {

                            Toast.makeText(
                                this@AddHappyPlaces,
                                "Location permission is granted. Now you can request for a current location.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }
    }

    private fun locationChooseKaro() {
        try{
            val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this@AddHappyPlaces)
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUESTCODE)
        }
        catch(e: Exception){
            e.printStackTrace()
        }
    }
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            mLatitude = mLastLocation.latitude
            Log.e("Current Latitude", "$mLatitude")
            mLongitutde = mLastLocation.longitude
            Log.e("Current Longitude", "$mLongitutde")
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {

        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()!!
        )
    }
    private fun saveKarDo() {
            if (et_title.text?.isNullOrEmpty() == true){
                Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
            }
            else if(et_description.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
            }
            else if(et_date?.text.isNullOrEmpty()){
                Toast.makeText(this, "Please enter date", Toast.LENGTH_SHORT).show()
            }
            else if(et_location?.text.isNullOrEmpty()) {
                Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
            }
            else if(saveImageToInternalStorage == null) {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
            else {
                val happyPlaceModel = HappyPlaceModel(
                    0,
                    et_title?.text.toString(),
                    et_description?.text.toString(),
                    saveImageToInternalStorage.toString(),
                    et_date?.text.toString(),
                    et_location?.text.toString(),
                    mLatitude,
                    mLongitutde
                )
                val dbHandler = DatabaseHandler(this)
                val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                if (addHappyPlace > 0) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
    }

    private fun clickDatePicker() {
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, { _, SelectedYear, SelectedMonth, DayOfMonth -> selectedDate = "$DayOfMonth/${SelectedMonth+1}/$SelectedYear"
            et_date?.setText(selectedDate)
        }, year, month, day)
        dpd.show()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if(data!=null){
                    val contentURI = data.data
                    try{
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        Log.e("saved: ", "Path :: $saveImageToInternalStorage")
                        iv_place_image?.setImageBitmap(selectedImageBitmap)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
            else if(requestCode == CAMERA){
                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                Log.e("saved: ", "Path :: $saveImageToInternalStorage")
                iv_place_image?.setImageBitmap(thumbnail)
            }
            else if(requestCode == PLACE_AUTOCOMPLETE_REQUESTCODE){
                val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                et_location.setText(place.address)
                mLatitude = place.latLng!!.latitude
                mLongitutde = place.latLng!!.longitude
                val status: Status = Autocomplete.getStatusFromIntent(data)
                Log.e("achaJi", status.toString())
            }
        }
    }

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this).withPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
            ).withListener(object : MultiplePermissionsListener{
                override fun onPermissionsChecked(report: MultiplePermissionsReport)
                {
                    if(report!!.areAllPermissionsGranted()){
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)
                    }
                }
                override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?)
                {
                    showRationalDialogForPermissions()
                }
            }
        ).onSameThread().check()

    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this).setMessage("Looks like permissions have not been given, please give using settings."
        ).setPositiveButton("Go to settings") { _, _ ->
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch(e:Exception){
                e.printStackTrace()
            }
        }.setNegativeButton("Cancel"){ dialog, _ ->
            dialog.dismiss()
        }.show()
    }
    private fun acha(){
            val pictureDialog = AlertDialog.Builder(this@AddHappyPlaces)
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from Camera")
            pictureDialog.setItems(pictureDialogItems){
                    _, which ->
                when(which){
                    0 -> choosePhotoFromGallery()
                    1 -> cameraPhoto()
                }
            }
        pictureDialog.show()
    }

    private fun cameraPhoto() {
        Dexter.withContext(this).withPermissions(android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener{
            override fun onPermissionsChecked(report: MultiplePermissionsReport)
            {
                if(report!!.areAllPermissionsGranted()){
                    val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(galleryIntent, CAMERA)
                }
            }
            override fun onPermissionRationaleShouldBeShown(p0: MutableList<PermissionRequest>?, p1: PermissionToken?)
            {
                showRationalDialogForPermissions()
            }
        }
        ).onSameThread().check()

    }

    private fun saveImageToInternalStorage(bitmap: Bitmap) :Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpeg")
        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush(); stream.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }

    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTOCOMPLETE_REQUESTCODE = 420
    }
}