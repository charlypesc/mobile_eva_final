package com.example.evafinal

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import coil.compose.AsyncImage
import com.example.evafinal.db.AppDataBase
import com.example.evafinal.db.Lugar
import com.example.evafinal.ws.Fabrica
import com.example.evafinal.ws.ResponseData
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class AppVM : ViewModel(){
    val latitud     = mutableStateOf(0.0 )
    val longitud    = mutableStateOf(0.0)

    var permisosUbicacionOk:()-> Unit = {}
}
class DetallesActivity : ComponentActivity() {
    val appVM: AppVM by viewModels()
    val lanzadorPermisos = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){
        if(
            (it[android.Manifest.permission.ACCESS_FINE_LOCATION]?:false) or
            (it[android.Manifest.permission.ACCESS_COARSE_LOCATION]?:false)
        ){
            appVM.permisosUbicacionOk()
        }else{
            Log.v("Se denegaron los permisos", "Se denegaron los permisos")
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                val lugarId= intent.getIntExtra("lugar_id", -1)
                val contexto = LocalContext.current
                val lugarDetalle = remember { mutableStateOf<Lugar?>(null) }
            LaunchedEffect(Unit){
                withContext(Dispatchers.IO){
                    val dao = AppDataBase.getInstance(contexto).lugarDao()
                    val lugar = dao.getById(lugarId)
                    lugarDetalle.value = lugar
                }
            }

                DetalleLugar(lugarDetalle, appVM)
//                AppUI(appVM, lugarDetalle)
            }
        }
    }

@Composable
fun DetalleLugar(lugarDetalles: MutableState<Lugar?>, appVm:AppVM){
    var lugar = lugarDetalles.value
    val contexto = LocalContext.current

    if(lugar!=null){
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally)
        {
            Text( lugar.lugar , fontWeight = FontWeight.ExtraBold, fontSize = 20.sp )
            Box(modifier =Modifier.widthIn(min = 250.dp, max = 250.dp)){
                AsyncImage(
                    model = lugar.imagenRef,
                    contentDescription = "ImagenLugar"
                )
            }
            Row(){
                Column() {
                    Text("Costo x Noche", fontWeight= FontWeight.ExtraBold, fontSize=16.sp)
                    Text("$${lugar.costoAlojamiento}")
                    Text("USD:${getUsd(lugar.costoAlojamiento)}")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column() {
                    Text("Costo x traslado", fontWeight= FontWeight.ExtraBold, fontSize=16.sp)
                    Text("$${lugar.costoTraslado}")
                    Text("USD:${getUsd(lugar.costoTraslado)}")
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column (){
                Text("Comentarios", fontWeight=FontWeight.ExtraBold, fontSize=16.sp )
                Text(lugar.comentarios.toString()  )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val intento = Intent(contexto, MainActivity::class.java)
                contexto.startActivity(intento)
            }) {
                Text("Volver")

            }
            AppUI(appVm, lugar)
        }
    }
}

@Composable
fun getUsd(value:Int):Int{
//    println("L l e g o  - - - - - - -  - -  ")
    var responseData by remember { mutableStateOf<ResponseData?>(null) }
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Unit){
        coroutineScope.launch {
            try {
                val response = withContext(Dispatchers.IO){
                    Fabrica.getDolarService().buscar()

                }
                responseData = response

            }catch (e: Exception){
                Log.e("getDolarTag", "Ocurrió una excepción:", e)
            }
        }
    }
    var monedaUSD = responseData?.dolar?.valor ?: 0.0
    var conversion = value / monedaUSD
    return conversion.toInt()
}
@Composable
fun AppUI(appVm: AppVM, lugarDetalles:Lugar){
    val contexto= LocalContext.current
    var lugar = lugarDetalles

    appVm.permisosUbicacionOk = {
        conseguirUbicacion(contexto){
            lugar?.lat = it.latitude
            lugar?.lon = it.longitude
        }
    }



    Column(){

        Spacer(Modifier.height(100.dp))
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(200.dp)
        ){

            AndroidView(
                factory = {
                    MapView(it).apply{
                        setTileSource(TileSourceFactory.MAPNIK)
                        Configuration.getInstance().userAgentValue = contexto.packageName
                        controller.setZoom(15.0)
                    }
                }, update = {
                    it.overlays.removeIf { true }
                    it.invalidate()
                    val geoPoint= GeoPoint(lugar?.lat ?:0.0 , lugar?.lon ?: 0.0)
                    it.controller.animateTo(geoPoint)
                    val marcador = Marker(it)
                    marcador.position=geoPoint
                    marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    it.overlays.add(marcador)
                }
            )
        }
    }
}

fun conseguirUbicacion(contexto: Context, onSuccess:(ubicacion: Location) -> Unit){
    try {
        val servicio = LocationServices.getFusedLocationProviderClient(contexto)
        val tarea = servicio.getCurrentLocation(

            Priority.PRIORITY_HIGH_ACCURACY,
            null
        )
        tarea.addOnSuccessListener {
            onSuccess(it)
        }


    }catch (se:SecurityException){

    }


}