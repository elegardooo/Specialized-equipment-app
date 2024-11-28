package com.example.specequipmentapp.ui.signup

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.specequipmentapp.MainActivity
import com.example.specequipmentapp.repository.UserDatabaseHelper
import com.example.specequipmentapp.ui.signin.SignInActivity

class SignUpActivity : ComponentActivity() {

    private lateinit var dbHelper: UserDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = UserDatabaseHelper(this)

        setContent{
            SignUpScreen(
                onSignUp = {email, password ->
                    dbHelper.insertUser(email, password)
                    val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
                    sharedPreferences.edit()
                        .putString("userName", email)
                        .putBoolean("isLoggedIn", true)
                        .apply()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                onSignUpCheck = {email -> dbHelper.validateUserEmail(email) },
                onNavigateToSignIn = {
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            )
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
fun SignUpScreen(
    onSignUp: (String, String) -> Unit,
    onSignUpCheck: (String) -> Boolean,
    onNavigateToSignIn: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordVisibleConfirmation by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val widthFraction = when {
        isTablet && configuration.orientation == Configuration.ORIENTATION_LANDSCAPE -> 0.45f
        isTablet -> 0.6f
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && !isTablet -> 0.8f
        else -> 1f
    }

    fun isPasswordStrong(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isLowerCase() } &&
                password.any { it.isDigit() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Specialized equipment app",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(widthFraction),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = { Text("Enter your password") },
            modifier = Modifier.fillMaxWidth(widthFraction),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
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

        OutlinedTextField(
            value = passwordConfirmation,
            onValueChange = {passwordConfirmation = it},
            label = { Text("Confirm your password") },
            modifier = Modifier.fillMaxWidth(widthFraction),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
                        errorMessage = "All fields must be filled"
                    } else if (password != passwordConfirmation) {
                        errorMessage = "Passwords don't match"
                    } else if (!isPasswordStrong(password)) {
                        errorMessage = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, and a number."
                    } else if (onSignUpCheck(email)) {
                        errorMessage = "This user already exists"
                    } else {
                        errorMessage = ""
                        onSignUp(email, password)
                    }
                }
            ),
            visualTransformation = if (passwordVisibleConfirmation) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {passwordVisibleConfirmation = !passwordVisibleConfirmation}) {
                    Icon(
                        imageVector = if (passwordVisibleConfirmation) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisibleConfirmation) "Hide password" else "Show password"
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
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                focusManager.clearFocus()
                if (email.isEmpty() || password.isEmpty() || passwordConfirmation.isEmpty()) {
                    errorMessage = "All fields must be filled"
                } else if (password != passwordConfirmation) {
                    errorMessage = "Passwords don't match"
                } else if (!isPasswordStrong(password)) {
                    errorMessage = "Password must be at least 8 characters long, include an uppercase letter, a lowercase letter, and a number."
                } else if (onSignUpCheck(email)) {
                    errorMessage = "This user already exists"
                } else {
                    errorMessage = ""
                    onSignUp(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(widthFraction)
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Already registered? Sign In",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onNavigateToSignIn() }
        )

    }
}
