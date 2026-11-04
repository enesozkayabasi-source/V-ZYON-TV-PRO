package com.example

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.screens.HomeScreen
import com.example.ui.splash.SplashScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.StreamViewModel

class MainActivity : ComponentActivity() {

    private val streamViewModel: StreamViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Kamera adası / çentik (Display Cutout) alanını tamamlayıcı tam ekran yap
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
            } else {
                @Suppress("DEPRECATION")
                window.attributes.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            val themeMode by streamViewModel.themeMode.collectAsState()
            var showSplash by remember { mutableStateOf(true) }

            MyApplicationTheme(themeMode = themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AnimatedContent(
                        targetState = showSplash,
                        transitionSpec = {
                            (androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500, easing = androidx.compose.animation.core.FastOutSlowInEasing)) +
                             androidx.compose.animation.scaleIn(initialScale = 0.95f, animationSpec = androidx.compose.animation.core.tween(500, easing = androidx.compose.animation.core.FastOutSlowInEasing)))
                            .togetherWith(
                                androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(400, easing = androidx.compose.animation.core.FastOutLinearInEasing)) +
                                androidx.compose.animation.scaleOut(targetScale = 1.05f, animationSpec = androidx.compose.animation.core.tween(400, easing = androidx.compose.animation.core.FastOutLinearInEasing))
                            )
                        },
                        label = "splash_transition"
                    ) { isSplashVisible ->
                        if (isSplashVisible) {
                            SplashScreen(onFinished = { showSplash = false })
                        } else {
                            HomeScreen(viewModel = streamViewModel)
                        }
                    }
                }
            }
        }
    }
}
