package com.example.evafinal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.evafinal.db.AppDataBase
import com.example.evafinal.db.Lugar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CrearLugarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            crearLugar()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun crearLugar(){
    val contexto = LocalContext.current
    var nombreLugar by remember{ mutableStateOf("") }
    var imagenRef by remember{ mutableStateOf("") }
    var latitud by remember{ mutableStateOf("") }
    var longitud by remember{ mutableStateOf("") }
    var orden by remember{ mutableStateOf("") }
    var costoAlojamiento by remember{ mutableStateOf("") }
    var costoTraslado by remember{ mutableStateOf("") }
    var comentarios by remember{ mutableStateOf("") }

    val alcanceCorrutina = rememberCoroutineScope()
    val lugarAgregado = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = nombreLugar,
            onValueChange = { newValue ->
                nombreLugar = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("Nombre de Lugar") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        //imagen
        TextField(
            value = imagenRef,
            onValueChange = { newValue ->
                imagenRef = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("URL de Imagen") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        //Latitud
        TextField(
            value = latitud,
            onValueChange = { newValue ->
                latitud = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("Latitud") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        //longitud
        TextField(
            value = longitud,
            onValueChange = { newValue ->
                longitud = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("Longitud") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        //Orden
        TextField(
            value = orden,
            onValueChange = { newValue ->
                orden = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("Orden") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        //costo alojamiento
        TextField(
            value = costoAlojamiento,
            onValueChange = { newValue ->
                costoAlojamiento = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("Costo Alojamiento") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        //costo traslado
        TextField(
            value = costoTraslado,
            onValueChange = { newValue ->
                costoTraslado = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("Costo Traslado") }
        )
        Spacer(modifier = Modifier.height(10.dp))
        //comentarios
        TextField(
            value = comentarios,
            onValueChange = { newValue ->
                comentarios = newValue
                if(newValue.isNotBlank()){
                    newValue.toString();
                }
            },
            label = { Text("Comentarios") }
        )
        Row {
            Button(onClick = {
                alcanceCorrutina.launch(Dispatchers.IO) {
                    val dao = AppDataBase.getInstance(contexto).lugarDao()
                    dao.insertar(Lugar(
                        0,
                        nombreLugar,
                        imagenRef,
                        latitud.toDouble(),
                        longitud.toDouble(),
                        orden.toInt(),
                        costoAlojamiento.toInt(),
                        costoTraslado.toInt(),
                        comentarios ))
                    lugarAgregado.value=true
                }



            }) {
                Text("Guardar")

            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(onClick = {
                val intento = Intent(contexto, MainActivity::class.java)
                contexto.startActivity(intento)
            }) {
                Text("Volver")

            }
            Spacer(modifier = Modifier.width(16.dp))
            if(lugarAgregado.value){
                Text(
                    text = "Lugar Agregado!",
                    modifier = Modifier
                    ,color = Color.Red
                )
            }
        }
    }
}
@Composable
fun Volver() {
    val contexto = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            Button(onClick = {
                val intento = Intent(contexto, MainActivity::class.java)
                contexto.startActivity(intento)
            }) {
                Text("Guardar")

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