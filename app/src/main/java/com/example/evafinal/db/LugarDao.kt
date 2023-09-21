package com.example.evafinal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LugarDao {

    @Query("Select * from lugar order by orden")
    fun getAll():List<Lugar>

    @Query("Select * from lugar where id= :id")
    fun getById(id:Int):Lugar?

    @Query("Select count(*) from lugar")
    fun contar():Int

    @Insert
    fun insertar(tarea:Lugar):Long

    @Update
    fun actualizar(tarea:Lugar)

    @Delete
    fun eliminar(tarea:Lugar)
}