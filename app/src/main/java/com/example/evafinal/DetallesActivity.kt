package com.example.evafinal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import com.example.evafinal.db.AppDataBase
import com.example.evafinal.db.Lugar
import com.example.evafinal.ws.Fabrica
import com.example.evafinal.ws.ResponseData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetallesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                val lugarId= intent.getIntExtra("lugar_id", -1)
                DetalleLugar(lugarId)
            }
        }
    }

@Composable
fun DetalleLugar(lugarId:Int){
    val contexto = LocalContext.current
    val lugarDetalle = remember { mutableStateOf<Lugar?>(null) }

    LaunchedEffect(Unit){
        withContext(Dispatchers.IO){
            val dao = AppDataBase.getInstance(contexto).lugarDao()
            val lugar = dao.getById(lugarId)
            lugarDetalle.value = lugar
        }
    }

    val lugar = lugarDetalle.value
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
