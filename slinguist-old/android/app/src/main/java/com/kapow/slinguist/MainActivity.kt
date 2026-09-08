package com.kapow.slinguist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kapow.slinguist.presentation.navigation.SlinguistApp
import com.kapow.slinguist.presentation.theme.SlinguistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SlinguistTheme {
                SlinguistApp()
            }
        }
    }
}
