package com.arca.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.arca.android.ui.navigation.ArcaNavGraph
import com.arca.android.ui.theme.ArcaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArcaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ArcaTheme.colors.background
                ) {
                    ArcaNavGraph()
                }
            }
        }
    }
}
