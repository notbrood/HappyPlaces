package com.broood.happyplaces.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.broood.happyplaces.models.HappyPlaceModel

class DatabaseHandler(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable"
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_HAPPY_PLACE_TABLE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_LOCATION + " TEXT,"
                + KEY_LATITUDE + " TEXT,"
                + KEY_LONGITUDE + " TEXT)")
        db?.execSQL(CREATE_HAPPY_PLACE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    /**
     * Function to insert a Happy Place details to SQLite Database.
     */
    fun addHappyPlace(happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(
            KEY_DESCRIPTION,
            happyPlace.desc
        )
        contentValues.put(KEY_DATE, happyPlace.date) // HappyPlaceModelClass DATE
        contentValues.put(KEY_LOCATION, happyPlace.Location) // HappyPlaceModelClass LOCATION
        contentValues.put(KEY_LATITUDE, happyPlace.Latitude) // HappyPlaceModelClass LATITUDE
        contentValues.put(KEY_LONGITUDE, happyPlace.Longitude) // HappyPlaceModelClass LONGITUDE

        val result = db.insert(TABLE_HAPPY_PLACE, null, contentValues)

        db.close()
        return result
    }
    fun getHappyPlacesList(): ArrayList<HappyPlaceModel>{
        val happyPlaceList: ArrayList<HappyPlaceModel> = ArrayList()
        val selectQuery = "SELECT * FROM $TABLE_HAPPY_PLACE"
        val db = this.readableDatabase
        try{
            val cursor : Cursor = db.rawQuery(selectQuery, null)
            if(cursor.moveToFirst()){
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(KEY_LONGITUDE))
                    )
                    happyPlaceList.add(place)
                }while(cursor.moveToNext())
            }
            cursor.close()
        }catch (e:SQLiteException){
            db.execSQL(selectQuery)
            return ArrayList()
        }
        return happyPlaceList
    }
}