package com.broood.happyplaces.models

import java.io.Serializable

class HappyPlaceModel (
    val id: Int,
    val title: String,
    val desc: String,
    val image: String,
    val date: String,
    val Location: String,
    val Latitude: Double,
    val Longitude: Double
    ) : Serializable