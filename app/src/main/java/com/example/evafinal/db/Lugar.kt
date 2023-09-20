package com.example.evafinal.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Lugar (
    @PrimaryKey(autoGenerate = true) val id:Int,
    var lugar:String,
    var imagenRef:String,
    var lat:Double,
    var lon:Double,
    var orden:Int,
    var costoAlojamiento: Int,
    var costoTraslado:Int,
    var comentarios:String
)