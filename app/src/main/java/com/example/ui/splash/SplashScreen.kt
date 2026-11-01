package com.example.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

/**
 * VİZYON TV Açılış (Splash) Ekranı
 *
 * Yenilikler:
 * - Yeni Mavi Metalik Kanatlı "V" Logosu (1:1 Kare ve 16:9 TV Banner Uyumu)
 * - Nefes Alan Elektrik Mavisi Arka Işık (Breathing Aurora Glow)
 * - Dinamik Yaylanan Logo Girişi (Spring Overshoot Entrance)
 * - Metalik Kanatlar Üzerinden Geçen Işık Yansıması (Specular Shimmer Sweep)
 * - Dönen Enerji Halkası ve Akıcı İlerleme Çubuğu
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("VİZYON TV Başlatılıyor...") }
    val entranceScale = remember { Animatable(0.6f) }
    val entranceAlpha = remember { Animatable(0f) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    // Sonsuz animasyon döngüsü (Nefes alan parlaklık ve ışık dalgası)
    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow_anim")

    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.40f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val subtleRotate by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "subtle_rotate"
    )

    // Açılış sekansı
    LaunchedEffect(Unit) {
        entranceAlpha.animateTo(1f, animationSpec = tween(240, easing = FastOutSlowInEasing))
        entranceScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.68f, stiffness = 450f)
        )

        progress = 0.35f
        statusText = "Türksat & Canlı Yayınlar Bağlanıyor..."
        delay(250)

        progress = 0.85f
        statusText = "Yüksek Kalite Akış Hazırlanıyor..."
        delay(300)

        progress = 1.0f
        statusText = "Hazır! Keyifli Seyirler..."
        delay(200)

        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .testTag("splash_screen")
    ) {
        // Derin Obsidyen & Safir Atmosfer Gradyanı
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF0B2545).copy(alpha = 0.65f * glowPulse),
                            Color(0xFF071226).copy(alpha = 0.85f),
                            Color(0xFF020617)
                        ),
                        radius = 1200f
                    )
                )
        )

        // Merkez İçerik Alanı
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            if (isLandscape) {
                // 16:9 TV Ekranı Modu: Logolu ve Başlıklı Geniş Banner
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .aspectRatio(16f / 9f)
                        .scale(entranceScale.value)
                        .alpha(entranceAlpha.value)
                        .shadow(
                            elevation = 28.dp,
                            shape = RoundedCornerShape(24.dp),
                            spotColor = Color(0xFF38BDF8).copy(alpha = 0.5f)
                        )
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            BorderStroke(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF38BDF8).copy(alpha = glowPulse),
                                        Color(0xFF00E5FF).copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            ),
                            RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF050811)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.vizyontv_tv_banner_16_9),
                        contentDescription = "VİZYON TV 16:9 Banner",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // 1:1 Mobil / Tablet Dikey Ekran: Animasyonlu Kanatlı "V" Logosu
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .scale(entranceScale.value)
                        .alpha(entranceAlpha.value)
                        .size(190.dp)
                ) {
                    // 1. Dönen Arka Plan Enerji Halkası
                    Box(
                        modifier = Modifier
                            .size(185.dp)
                            .rotate(subtleRotate)
                            .border(
                                width = 2.dp,
                                brush = Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF38BDF8).copy(alpha = 0.8f),
                                        Color(0xFF0284C7).copy(alpha = 0.2f),
                                        Color.Transparent,
                                        Color(0xFF00E5FF).copy(alpha = 0.9f),
                                        Color(0xFF38BDF8).copy(alpha = 0.8f)
                                    )
                                ),
                                shape = CircleShape
                            )
                    )

                    // 2. Nefes Alan Arka Plan Işık Aurası
                    Box(
                        modifier = Modifier
                            .size(165.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8).copy(alpha = 0.35f * glowPulse),
                                        Color(0xFF0284C7).copy(alpha = 0.15f * glowPulse),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // 3. Ana Mavi Kanatlı Logo Kutusu (Apple Liquid Glass Çerçeveli)
                    Surface(
                        modifier = Modifier
                            .size(156.dp)
                            .shadow(
                                elevation = 20.dp,
                                shape = RoundedCornerShape(36.dp),
                                spotColor = Color(0xFF38BDF8).copy(alpha = 0.6f)
                            )
                            .clip(RoundedCornerShape(36.dp))
                            .border(
                                width = 2.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF38BDF8).copy(alpha = glowPulse),
                                        Color(0xFF0284C7).copy(alpha = 0.30f)
                                    )
                                ),
                                shape = RoundedCornerShape(36.dp)
                            ),
                        shape = RoundedCornerShape(36.dp),
                        color = Color(0xFF000000)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vizyontv_blue_icon),
                            contentDescription = "VİZYON TV Yeni Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Brand Title (Elektrik Mavi & Parlak Beyaz)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "VİZYON",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 4.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TV",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF38BDF8),
                        letterSpacing = 4.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "CANLI YAYIN & MEDYA SİSTEMİ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8).copy(alpha = 0.90f),
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Alt Yükleme ve İlerleme Alanı
            Column(
                modifier = Modifier
                    .fillMaxWidth(if (isLandscape) 0.6f else 0.88f)
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = statusText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.95f),
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF38BDF8)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Akıcı Mavi İlerleme Çubuğu
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF38BDF8),
                    trackColor = Color.White.copy(alpha = 0.12f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hızlı Giriş Butonu
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color(0xFF0F172A).copy(alpha = 0.80f),
                    border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.45f)),
                    modifier = Modifier
                        .clickable { onFinished() }
                        .testTag("splash_skip_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hemen Başla",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
