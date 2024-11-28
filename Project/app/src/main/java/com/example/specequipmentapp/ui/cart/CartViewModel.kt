import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.specequipmentapp.screens.CartItem

class CartViewModel : ViewModel() {
    private val _cartItems = mutableStateListOf<CartItem>()
    val cartItems: List<CartItem> get() = _cartItems

    fun addToCart(item: CartItem) {
        val existingItem = _cartItems.find { it.product.id == item.product.id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            _cartItems[index] = existingItem.copy(quantity = existingItem.quantity + item.quantity)
        } else {
            _cartItems.add(item)
        }
    }

    fun updateQuantity(item: CartItem, delta: Int) {
        val existingItem = _cartItems.find { it.product.id == item.product.id }
        if (existingItem != null) {
            val index = _cartItems.indexOf(existingItem)
            val newQuantity = existingItem.quantity + delta
            if (newQuantity > 0) {
                _cartItems[index] = existingItem.copy(quantity = newQuantity)
            } else {
                _cartItems.removeAt(index)
            }
        }
    }

    fun clearCart() {
        _cartItems.clear()
    }
}
