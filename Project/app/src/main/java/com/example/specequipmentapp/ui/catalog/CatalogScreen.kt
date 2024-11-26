package com.example.specequipmentapp.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.specequipmentapp.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import coil.request.ImageRequest

data class Product(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val price: Double
)

data class CartItem(
    val product: Product,
    var quantity: Int = 1
)

fun getProducts() = listOf(
    Product(1, "Helmet", "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3a/Cat03.jpg/640px-Cat03.jpg", 99.99),
    Product(2, "Gloves", "https://www.alleycat.org/wp-content/uploads/2019/03/FELV-cat.jpg", 29.99),
    Product(3, "Boots", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSBtuAHayX2Cjbmq5DuvygoqGnlBvSvYaaUnw&s", 129.99),
    Product(4, "Jacket", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOMx3IuTkJLK4Ws0VFkjjh3SWRN4x7bYhYRQ&s", 199.99),
    Product(5, "Pants", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOMx3IuTkJLK4Ws0VFkjjh3SWRN4x7bYhYRQ&s", 89.99),
    Product(6, "Backpack", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOMx3IuTkJLK4Ws0VFkjjh3SWRN4x7bYhYRQ&s", 149.99),
    Product(7, "Goggles", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOMx3IuTkJLK4Ws0VFkjjh3SWRN4x7bYhYRQ&s", 59.99),
    Product(8, "Snowboard", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTOMx3IuTkJLK4Ws0VFkjjh3SWRN4x7bYhYRQ&s", 349.99),
    Product(9, "Skis", "https://steamuserimages-a.akamaihd.net/ugc/2017099938946854056/F720D6D3F7E77A202EB1F5F69A8DDA43BC15AF14/", 249.99)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(cartItems: SnapshotStateList<CartItem>) {
    val products = remember { getProducts() }

    Scaffold {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                ProductItem(product = product, onAddToCart = { addToCart(cartItems, product) })
            }
        }
    }
}

fun addToCart(cartItems: SnapshotStateList<CartItem>, product: Product) {
    // Проверяем, есть ли уже товар в корзине
    val existingItem = cartItems.find { it.product.id == product.id }

    if (existingItem != null) {
        // Если товар уже есть, увеличиваем его количество
        val index = cartItems.indexOf(existingItem)
        cartItems[index] = existingItem.copy(quantity = existingItem.quantity + 1)
    } else {
        // Если товара нет, добавляем новый объект CartItem
        cartItems.add(CartItem(product = product, quantity = 1))
    }
}


@Composable
fun ProductItem(product: Product, onAddToCart: (Product) -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
