package com.example.specequipmentapp

import CartViewModel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.specequipmentapp.screens.CartItem
import com.example.specequipmentapp.screens.CatalogScreen
import com.example.specequipmentapp.screens.ProfileScreen
import com.example.specequipmentapp.ui.cart.CartScreen
import com.example.specequipmentapp.ui.signin.SignInActivity
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

class MainActivity : ComponentActivity() {

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
    val cartViewModel: CartViewModel = viewModel()

    val onUpdateQuantity: (CartItem, Int) -> Unit = { cartItem, delta ->
        val newQuantity = cartItem.quantity + delta
        if (newQuantity > 0) {
            val index = cartItems.indexOf(cartItem)
            if (index != -1) {
                cartItems[index] = cartItem.copy(quantity = newQuantity)
            }
        } else {
            cartItems.remove(cartItem)
        }
    }

    var swipeInProgress by remember { mutableStateOf(false) }
    var isGoingForward by remember { mutableStateOf(true) } // Флаг направления перехода

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreenTitle.value) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { swipeInProgress = false },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (!swipeInProgress) {
                                val swipeThreshold = 40f
                                when {
                                    dragAmount > swipeThreshold -> {
                                        swipeInProgress = true
                                        isGoingForward = false
                                        navigateToPreviousScreen(navController, currentScreenTitle)
                                    }
                                    dragAmount < -swipeThreshold -> {
                                        swipeInProgress = true
                                        isGoingForward = true
                                        navigateToNextScreen(navController, currentScreenTitle)
                                    }
                                }
                            }
                        }
                    )
                }
        ) {
            AnimatedNavigationHost(
                navController = navController,
                cartItems = cartItems,
                cartViewModel = cartViewModel,
                onUpdateQuantity = onUpdateQuantity,
                isGoingForward = isGoingForward
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnimatedNavigationHost(
    navController: NavHostController,
    cartItems: SnapshotStateList<CartItem>,
    cartViewModel: CartViewModel,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    isGoingForward: Boolean
) {
    val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn()
    }

    val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut()
    }

    val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn()
    }

    val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut()
    }

    val transitionEnter = if (isGoingForward) enterTransition else popEnterTransition
    val transitionExit = if (isGoingForward) exitTransition else popExitTransition

    AnimatedNavHost(
        navController = navController,
        startDestination = "Catalog",
        enterTransition = transitionEnter,
        exitTransition = transitionExit,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition
    ) {
        composable("Catalog") { CatalogScreen(cartViewModel = cartViewModel) }
        composable("Cart") { CartScreen(cartViewModel = cartViewModel) }
        composable("Profile") { ProfileScreen() }
    }
}

fun navigateToPreviousScreen(navController: NavHostController, currentScreenTitle: MutableState<String>) {
    val screens = listOf("Catalog", "Cart", "Profile")
    val currentIndex = screens.indexOf(currentScreenTitle.value)
    if (currentIndex > 0) {
        val previousScreen = screens[currentIndex - 1]
        navController.navigate(previousScreen) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
        currentScreenTitle.value = previousScreen
    }
}

fun navigateToNextScreen(navController: NavHostController, currentScreenTitle: MutableState<String>) {
    val screens = listOf("Catalog", "Cart", "Profile")
    val currentIndex = screens.indexOf(currentScreenTitle.value)
    if (currentIndex < screens.size - 1) {
        val nextScreen = screens[currentIndex + 1]
        navController.navigate(nextScreen) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
        currentScreenTitle.value = nextScreen
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

//@Composable
//fun NavigationHost(
//    navController: NavHostController,
//    cartItems: SnapshotStateList<CartItem>,
//    onUpdateQuantity: (CartItem, Int) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    NavHost(navController = navController, startDestination = "Catalog", modifier = modifier) {
//        composable("Catalog") { CatalogScreen() }
//        composable("Cart") { CartScreen() }
//        composable("Profile") { ProfileScreen() }
//    }
//}

data class BottomBarItem(val name: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
