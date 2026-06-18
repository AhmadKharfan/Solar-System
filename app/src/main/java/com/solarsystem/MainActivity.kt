package com.solarsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.solarsystem.ui.screen.ComponentPreviews
import com.solarsystem.ui.theme.SolarColors
import com.solarsystem.ui.theme.SolarSystemTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolarSystemTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = SolarColors.ScreenBackground,
                ) { innerPadding ->
                    ComponentPreviews(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
