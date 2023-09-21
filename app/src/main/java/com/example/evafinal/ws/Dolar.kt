package com.example.evafinal.ws

data class ResponseData(
    val version: String,
    val autor: String,
    val fecha: String,
    val uf: IndicatorData,
    val ivp: IndicatorData,
    val dolar: IndicatorData,
    val dolar_intercambio: IndicatorData,
    val euro: IndicatorData,
    val ipc: IndicatorData,
    val utm: IndicatorData,
    val imacec: IndicatorData,
    val tpm: IndicatorData,
    val libra_cobre: IndicatorData,
    val tasa_desempleo: IndicatorData,
    val bitcoin: IndicatorData
)

data class IndicatorData(
    val codigo: String,
    val nombre: String,
    val unidad_medida: String,
    val fecha: String,
    val valor: Double
)