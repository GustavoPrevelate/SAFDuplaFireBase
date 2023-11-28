package br.senai.sp.jandira.safdupla

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import br.senai.sp.jandira.safdupla.ui.theme.SAFDuplaTheme
import br.senai.sp.jandira.safdupla.retrofit.StorageUtil
import br.senai.sp.jandira.safdupla.retrofit.ApiResponse
import br.senai.sp.jandira.safdupla.retrofit.RetrofitFactory
import coil.compose.AsyncImage
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SAFDuplaTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ProjectSAF()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSAF() {

    //SOBRE A IMAGEM

    var uri by remember {
        mutableStateOf<Uri?>(null)
    }

    var url by remember {
        mutableStateOf<String>("")
    }

    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )

    val context = LocalContext.current

    // SOBRE OS CAMPOS DE TEXTO

    var username by remember {
        mutableStateOf<String>("")
    }

    var senha by remember {
        mutableStateOf<String>("")
    }

    // DESIGN DA TELA
    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Box(
            contentAlignment = Alignment.BottomEnd

        ) {

            Card(
                modifier = Modifier
                    .width(350.dp)
                    .height(200.dp)
                    .clickable {
                        singlePhotoPicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                shape = CircleShape,
            ) {
                AsyncImage(
                    model = uri ?: R.drawable.foto_padrao,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                DisposableEffect(uri) {
                    if (uri != null) {
                        // Call your UploadPick function here when uri is updated
                        fun UploadPick(uri: Uri, context: Context) {
                            uri?.let {
                                StorageUtil.uploadToStorage(
                                    uri = it,
                                    context = context,
                                    type = "image",
                                    {
                                        url = it
                                    })
                            }
                        }

                        UploadPick(uri!!, context)

                    }

                    onDispose { }
                }
            }
            Image(
                painterResource(id = R.drawable.adicionar_foto),
                contentDescription = "",
                modifier = Modifier.size(height = 32.dp, width = 32.dp)
            )
        }

        Column {
            OutlinedTextField(
                value = username ?: "example@gmail.com",
                shape = RoundedCornerShape(16.dp),
                onValueChange = { username = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(63.dp),
                label = { Text(text = "E-MAIL") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Username",
                        modifier = Modifier,
                        tint = Color.Red
                    )
                },

                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        focusedBorderColor = Color.Red,
                        focusedLabelColor = Color.Red
                    )
            )
            
            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = senha ?: "example_passworld.com",
                shape = RoundedCornerShape(16.dp),
                onValueChange = { senha = it },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(63.dp),
                label = { Text(text = "SENHA") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.lock),
                        contentDescription = "Username",
                        modifier = Modifier,
                        tint = Color.Red
                    )
                },
                colors = TextFieldDefaults
                    .outlinedTextFieldColors(
                        focusedBorderColor = Color.Red,
                        focusedLabelColor = Color.Red
                    )
            )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val body = JsonObject().apply {
                        addProperty("login", username)
                        addProperty("senha", senha)
                        addProperty("imagem", url)
                    }

                    val call = RetrofitFactory().cadastroService().createUser(body)

                    call.enqueue(object : Callback<ApiResponse> {
                        override fun onResponse(
                            call: Call<ApiResponse>,
                            response: Response<ApiResponse>
                        ) {
//                            results = response.body()!!.
                            Toast.makeText(
                                context,
                                "${response.body()!!.mensagem}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                            Log.i(
                                "ds2m",
                                "onFailure: ${t.message}"
                            )

                            Log.i(
                                "ds2m",
                                "onFailure: ${call}"
                            )
                        }

                    })



                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(Color.Red),

                ) {
                Text(text = "Cadastrar")
            }
        }
    }



}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    SAFDuplaTheme {
        ProjectSAF()
    }
}