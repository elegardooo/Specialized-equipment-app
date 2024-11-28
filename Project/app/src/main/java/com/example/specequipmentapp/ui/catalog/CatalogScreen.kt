package com.example.specequipmentapp.screens

import CartViewModel
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class Product(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: Double,
    val description: String
)

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

const val default_image_url = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOMx3IuTkJLK4Ws0VFkjjh3SWRN4x7bYhYRQ&s"

fun getProducts() = listOf(
    Product(
        id = 1,
        name = "Helmet",
        imageUrl = ProductResources.productImageUrls[1] ?: default_image_url,
        price = 99.99,
        description = ProductResources.productDescriptions[1] ?: "No description available"
    ),
    Product(
        id = 2,
        name = "Gloves",
        imageUrl = ProductResources.productImageUrls[2] ?: default_image_url,
        price = 29.99,
        description = ProductResources.productDescriptions[2] ?: "No description available"
    ),
    Product(
        id = 3,
        name = "Boots",
        imageUrl = ProductResources.productImageUrls[3] ?: default_image_url,
        price = 129.99,
        description = ProductResources.productDescriptions[3] ?: "No description available"
    ),
    Product(
        id = 4,
        name = "Jacket",
        imageUrl = ProductResources.productImageUrls[4] ?: default_image_url,
        price = 199.99,
        description = ProductResources.productDescriptions[4] ?: "No description available"
    ),
    Product(
        id = 5,
        name = "Pants",
        imageUrl = ProductResources.productImageUrls[5] ?: default_image_url,
        price = 89.99,
        description = ProductResources.productDescriptions[5] ?: "No description available"
    ),
    Product(
        id = 6,
        name = "Backpack",
        imageUrl = ProductResources.productImageUrls[6] ?: default_image_url,
        price = 149.99,
        description = ProductResources.productDescriptions[6] ?: "No description available"
    ),
    Product(
        id = 7,
        name = "Goggles",
        imageUrl = ProductResources.productImageUrls[7] ?: default_image_url,
        price = 59.99,
        description = ProductResources.productDescriptions[7] ?: "No description available"
    ),
    Product(
        id = 8,
        name = "Snowboard",
        imageUrl = ProductResources.productImageUrls[8] ?: default_image_url,
        price = 349.99,
        description = ProductResources.productDescriptions[8] ?: "No description available"
    ),
    Product(
        id = 9,
        name = "Skis",
        imageUrl = ProductResources.productImageUrls[9] ?: default_image_url,
        price = 249.99,
        description = ProductResources.productDescriptions[9] ?: "No description available"
    )
)

@Composable
fun CatalogScreen(cartViewModel: CartViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val products = remember { getProducts() }
    val selectedProduct = remember { mutableStateOf<Product?>(null) } // Хранение выбранного товара

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val columnsCount = if (isTablet && isLandscape) 4 else 2

    Scaffold {
        LazyVerticalGrid(
            columns = GridCells.Fixed(columnsCount),
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductItem(
                    product = product,
                    onSelectProduct = { selectedProduct.value = product },
                    onAddToCart = { cartViewModel.addToCart(CartItem(product = product, quantity = 1)) } // Используем ViewModel
                )
            }
        }
    }

    selectedProduct.value?.let { product ->
        ProductDetailsDialog(
            product = product,
            onDismiss = { selectedProduct.value = null },
            onAddToCart = { cartViewModel.addToCart(CartItem(product = product, quantity = 1)) }, // Используем ViewModel
            isTablet = isTablet,
            isLandscape = isLandscape
        )
    }
}


fun addToCart(cartItems: SnapshotStateList<CartItem>, product: Product) {

    val existingItem = cartItems.find { it.product.id == product.id }

    if (existingItem != null) {
        val index = cartItems.indexOf(existingItem)
        cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
    } else {
        cartItems.add(CartItem(product = product, quantity = 1))
    }
}


@Composable
fun ProductItem(
    product: Product,
    onSelectProduct: (Product) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clickable { onSelectProduct(product) }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val pressStartTime = System.currentTimeMillis()
                        val pressDuration = 500L

                        awaitRelease()
                        val pressEndTime = System.currentTimeMillis()
                        val holdingDuration = pressEndTime - pressStartTime

                        if (holdingDuration >= pressDuration) {
                            onAddToCart(product)
                            Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                        } else {
                            onSelectProduct(product)
                        }
                    }
                )
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Text(
                text = "$${product.price}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .align(Alignment.Start)
            )
            Text(
                text = product.name,
                fontSize = 16.sp,
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .align(Alignment.Start)
            )
            Button(
                onClick = {
                    onAddToCart(product)
                    Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add to Cart")
            }
        }
    }
}

@Composable
fun ProductDetailsDialog(
    product: Product,
    onDismiss: () -> Unit,
    onAddToCart: (Product) -> Unit,
    isTablet: Boolean,
    isLandscape: Boolean
) {
    val context = LocalContext.current

    val widthFraction = when {
        isTablet && isLandscape -> 0.5f
        isTablet -> 1f
        else -> 1f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(onClick = onDismiss)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .align(Alignment.BottomCenter)
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {

                }
                ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.5f)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$${product.price}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = {
                                onAddToCart(product)
                                Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp)
                        ) {
                            Text("Add to Cart")
                        }
                    }
                }
            }
        }
    }
}












