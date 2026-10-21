package com.example.casajojo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.casajojo.ui.navigation.AppNavigation
import com.example.casajojo.ui.navigation.AppViewModelFactory
import com.example.casajojo.ui.theme.CasaJojoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = AppViewModelFactory((application as CasaJojoApp).repository)
        setContent {
            CasaJojoTheme {
                AppNavigation(factory = factory)
            }
        }
    }
}
