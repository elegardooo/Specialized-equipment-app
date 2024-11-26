package com.example.specequipmentapp.ui.cart

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.platform.LocalContext
import com.example.specequipmentapp.screens.CartItem

@Composable
fun CartScreen(cartItems: SnapshotStateList<CartItem>, onUpdateQuantity: (CartItem, Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cartItems) { cartItem ->
            CartItemView(cartItem = cartItem, onUpdateQuantity = onUpdateQuantity)
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
