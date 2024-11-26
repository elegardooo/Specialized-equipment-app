package com.example.specequipmentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

//class ProfileActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            ProfileScreen()
//        }
//    }
//}
//
//@Composable
//fun ProfileScreen() {
//    Scaffold(
//        topBar = { ProfileTopBar("Profile Screen") }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(text = "Profile Page Content Here")
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(onClick = { /* Add log out logic */ }) {
//                Text("Log Out")
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ProfileTopBar(title: String) {
//    TopAppBar(
//        title = { Text(title) }
//    )
//}
