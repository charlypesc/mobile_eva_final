package com.example.evafinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import coil.compose.rememberImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    lifecycleScope.launch(Dispatchers.IO){
        val lugarDao = AppDataBase.getInstance(this@MainActivity).lugarDao()
        lugarDao.insertar(Lugar(0,"Nevados de Chillan", "https://otros", -23.211, -34.222,1, 100000,45000,"Lugar muy lindo"))
    }

        setContent {
            ListaLugarUI()
        }
    }
}

@Preview
@Composable
fun ListaLugarUI(){
    val contexto = LocalContext.current
    val (lugares, setLugares) = remember { mutableStateOf(emptyList<Lugar>()) }

    LaunchedEffect(Unit){
        withContext(Dispatchers.IO){
            val dao = AppDataBase.getInstance(contexto).lugarDao()
            setLugares(dao.getAll())
        }
    }

    LazyColumn {
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
                        model = "https://www.ladiscusion.cl/wp-content/uploads/2018/03/511937-1.jpg",
                        contentDescription = "portadaLibro"
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column(){
                    Text(lugar.lugar, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                    Text("Costo x Noche: ${lugar.costoAlojamiento}" )
                }
            }
        }
    }
}



