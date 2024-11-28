package com.example.specequipmentapp.ui.cart

import CartViewModel
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.specequipmentapp.screens.CartItem
import com.example.specequipmentapp.util.NativeUtils

@Composable
fun CartScreen(cartViewModel: CartViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val cartItems = cartViewModel.cartItems

    CartScreenContent(
        cartItems = cartItems,
        onUpdateQuantity = { item, delta -> cartViewModel.updateQuantity(item, delta) },
        onClearCart = { cartViewModel.clearCart() }
    )
}

@Composable
fun CartScreenContent(
    cartItems: List<CartItem>,
    onUpdateQuantity: (CartItem, Int) -> Unit,
    onClearCart: () -> Unit
) {
    val context = LocalContext.current
    //val totalPrice = cartItems.sumOf { it.product.price * it.quantity }
    val totalPrice = NativeUtils.calculateTotalPrice(
        cartItems.map { it.quantity }.toIntArray(),
        cartItems.map { it.product.price }.toDoubleArray()
    )
    val isCartEmpty = cartItems.isEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cartItems) { cartItem ->
                CartItemView(cartItem = cartItem, onUpdateQuantity = onUpdateQuantity)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Total: $${"%.2f".format(totalPrice)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(8.dp)
        )

        Button(
            onClick = {
                if (!isCartEmpty) {
                    onClearCart()
                    Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            enabled = !isCartEmpty
        ) {
            Text("Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CartItemView(cartItem: CartItem, onUpdateQuantity: (CartItem, Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cartItem.product.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = cartItem.product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = cartItem.product.name, fontSize = 16.sp)
                Text(text = "$${cartItem.product.price}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { onUpdateQuantity(cartItem, -1) }) {
                        Text("-")
                    }
                    Text(text = cartItem.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                    Button(onClick = { onUpdateQuantity(cartItem, 1) }) {
                        Text("+")
                    }
                }
            }
        }
    }
}
