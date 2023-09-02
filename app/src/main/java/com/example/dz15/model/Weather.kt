package com.example.dz15.model

data class Weather(
    val location: Location,
    val current :Current
    )

data class Location(
    val name: String,
    val country: String,
    val localtime: String,
)
data class Current(
    val temp_c:String,
    val condition:Condition
)
data class Condition(
    val text :String,
    val icon:String,
)
