package com.example.specequipmentapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.specequipmentapp.screens.CartItem
import com.example.specequipmentapp.screens.CatalogScreen
import com.example.specequipmentapp.screens.ProfileScreen
import com.example.specequipmentapp.ui.cart.CartScreen
import com.example.specequipmentapp.ui.signin.SignInActivity

class MainActivity : ComponentActivity() {
    private val cartItems = mutableStateListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "Launching MainActivity")
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (!isLoggedIn) {
            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        setContent {
            AppContent()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    val navController = rememberNavController()
    val currentScreenTitle = remember { mutableStateOf("Catalog") }

    val cartItems = remember { mutableStateListOf<CartItem>() }

    val onUpdateQuantity: (CartItem, Int) -> Unit = { cartItem, delta ->
        val newQuantity = cartItem.quantity + delta
        if (newQuantity > 0) {
            val index = cartItems.indexOf(cartItem)
            if (index != -1) {
                cartItems[index] = cartItem.copy(quantity = newQuantity) // Создаем новый объект с обновленным количеством
            }
        } else {
            cartItems.remove(cartItem) // Удаляем товар, если количество <= 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreenTitle.value)},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, onTitleChange = { title ->
                currentScreenTitle.value = title
            })
        }
    ) { paddingValues ->
        NavigationHost(
            navController = navController,
            cartItems = cartItems,
            onUpdateQuantity = onUpdateQuantity,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    onTitleChange: (String) -> Unit
) {
    val items = listOf(
        BottomBarItem("Catalog", Icons.Default.Store),
        BottomBarItem("Cart", Icons.Default.ShoppingCart),
        BottomBarItem("Profile", Icons.Default.Person)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = currentRoute == item.name,
                onClick = {
                    navController.navigate(item.name) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    onTitleChange(item.name)
                }
            )
        }
    }
}

@Composable
fun NavigationHost(
    navController: NavHostController,
    cartItems: SnapshotStateList<CartItem>,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = "Catalog", modifier = modifier) {
        composable("Catalog") { CatalogScreen(cartItems) }
        composable("Cart") { CartScreen(cartItems, onUpdateQuantity) }
        composable("Profile") { ProfileScreen() }
    }
}

data class BottomBarItem(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
