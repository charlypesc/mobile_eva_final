package com.example.evafinal

import android.content.Intent
import android.os.Bundle
import android.text.style.ClickableSpan
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.evafinal.db.AppDataBase
import com.example.evafinal.db.Lugar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.evafinal.ws.DolarService
import com.example.evafinal.ws.Fabrica
import com.example.evafinal.ws.IndicatorData
import com.example.evafinal.ws.ResponseData
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.GlobalScope
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    lifecycleScope.launch(Dispatchers.IO){
        val lugarDao = AppDataBase.getInstance(this@MainActivity).lugarDao()
        val cantidadLugares = lugarDao.contar()
        if(cantidadLugares < 1 ){
            lugarDao.insertar(Lugar(0,"Nevados de Chillan", "https://otros", -23.211, -34.222,1, 100000,45000,"Lugar muy lindo"))
        }
    }

        setContent {

            ListaLugarUI()

        }
    }
}

@Composable
fun PantallaInicio(lugares: List<Lugar>, onSave: () -> Unit) {
    val contexto = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Button(onClick = {
            val intento = Intent(contexto, CrearLugarActivity::class.java)
            contexto.startActivity(intento)
        }) {
            Text("Crear lugar")

        }
    }
}
@Composable
fun ListaLugarUI(onSave:() -> Unit = {}){
    val contexto = LocalContext.current
    val (lugares, setLugares) = remember { mutableStateOf(emptyList<Lugar>()) }
    val alcanceCorrutina = rememberCoroutineScope()
    LaunchedEffect(Unit){
        withContext(Dispatchers.IO){
            val dao = AppDataBase.getInstance(contexto).lugarDao()
            setLugares(dao.getAll())
        }
    }
Column (
    modifier = Modifier.padding(16.dp)
)
    {
        LazyColumn (
        ){
            items(lugares) { lugar ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {

                    Box(
                        modifier = Modifier.widthIn(min = 100.dp, max = 100.dp)
                    ){

                        AsyncImage(
                            model = lugar.imagenRef,
                            contentDescription = "portadaLibro",
                            modifier = Modifier.clickable {
                                val intent = Intent(contexto, DetallesActivity::class.java)
                                intent.putExtra("lugar_id", lugar.id)
                                contexto.startActivity(intent)
                            }

                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column(){
                        Text(lugar.lugar, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Costo x Noche: ${lugar.costoAlojamiento}" )
                        Text("Valor USD: ${getDolar(lugar.costoAlojamiento.toInt())}")
                        Row(

                        ){
                            Icon(
                                Icons.Filled.Info,
                                contentDescription = "info",
                                modifier = Modifier.clickable {
//                                    alcanceCorrutina.launch(Dispatchers.IO ){
//                                        val dao = AppDataBase.getInstance( contexto).tareaDao()
//                                        dao.eliminar(tarea)
//                                        onSave()
//                                    }
                                }, tint = Color.Blue
                            )

                            Icon(
                                Icons.Filled.LocationOn,
                                contentDescription = "Ubication",
                                modifier = Modifier.clickable {
//                                    alcanceCorrutina.launch(Dispatchers.IO ){
//                                        val dao = AppDataBase.getInstance( contexto).tareaDao()
//                                        dao.eliminar(tarea)
//                                        onSave()
//                                    }
                                }, tint = Color.Blue
                            )
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Eliminar tarea",
                                modifier = Modifier.clickable {
                                    alcanceCorrutina.launch(Dispatchers.IO ){
                                        val dao = AppDataBase.getInstance( contexto).lugarDao()
                                        dao.eliminar(lugar)
                                        onSave()
                                    }
                                }, tint = Color.Red
                            )
                        }

                    }
                }
            }

        }
        PantallaInicio(lugares = lugares, onSave = onSave)

    }
}
@Composable
fun getDolar(value:Int):Int{
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




