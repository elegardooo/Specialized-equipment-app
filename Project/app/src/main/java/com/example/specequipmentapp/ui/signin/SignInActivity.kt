package com.example.specequipmentapp.ui.signin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.specequipmentapp.MainActivity
import com.example.specequipmentapp.repository.UserDatabaseHelper
import com.example.specequipmentapp.ui.signup.SignUpActivity

class SignInActivity : ComponentActivity() {

    private lateinit var dbHelper: UserDatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SignInActivity", "Launching SignInActivity")
        dbHelper = UserDatabaseHelper(this)
        // Database test user
        //dbHelper.insertUser("test@example.com", "password123")

        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        } else {
            setContent {
                SignInScreen(
                    onSignIn = { email, password ->
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.putString("userName", email)  // Сохраняем имя пользователя
                        editor.apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onSignInCheck = { email, password -> dbHelper.validateUser(email, password) },
                    onNavigateToSignUp = {
                        val intent = Intent(this, SignUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

fun validateCredentials(email: String, password: String): Boolean {
    if(email == "log" && password == "pass")
        return true
    else
        return false
}

@Composable
fun SignInScreen(
    onSignIn: (String, String) -> Unit,
    onSignInCheck: (String, String) -> Boolean,
    onNavigateToSignUp: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Email")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Password")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if(email.isEmpty() || password.isEmpty()) {
                        errorMessage = "All fields must be filled"
                    } else {
                        if(!onSignInCheck(email, password)) {
                            errorMessage = "Invalid email or password"
                        } else {
                            errorMessage = ""
                            onSignIn(email, password)
                        }
                    }
                }
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {passwordVisible = !passwordVisible}) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if(errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                focusManager.clearFocus()
                if(email.isEmpty() || password.isEmpty()) {
                    errorMessage = "All fields must be filled"
                } else {
                    if(!onSignInCheck(email, password)) {
                        errorMessage = "Invalid email or password"
                    } else {
                        errorMessage = ""
                        onSignIn(email, password)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Don't have an account? Sign Up",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onNavigateToSignUp() }
        )

    }
}