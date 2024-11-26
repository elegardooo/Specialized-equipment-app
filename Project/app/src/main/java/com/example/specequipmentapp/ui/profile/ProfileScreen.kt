package com.example.specequipmentapp.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.specequipmentapp.ui.signin.SignInActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {

    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val userName = sharedPreferences.getString("userName", "Guest") ?: "Guest"

    Scaffold(
        topBar = {
//            TopAppBar(
//                title = { Text("Profile Screen") }
//            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Logged in as: $userName",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 24.sp),
                modifier = Modifier
                    .padding(top = 16.dp, start = 8.dp, end = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    //viewModel.logout()
                    viewModel.logout()
                    val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("isLoggedIn", false)
                    editor.remove("userName")
                    editor.apply()

                    val intent = Intent(context, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                    (context as Activity).finish()
                },
                modifier = Modifier
                    .padding(16.dp)
                    .size(200.dp, 60.dp)
            ) {
                Text("Log Out")
            }
        }
    }
}

class ProfileViewModel : ViewModel() {
    // Состояние текущего имени пользователя
    private val _userName = MutableStateFlow("Guest") // Замените на реального пользователя
    val userName: StateFlow<String> = _userName

    fun logout() {
        // Очистка данных пользователя
        _userName.value = "Guest"
    }
}
