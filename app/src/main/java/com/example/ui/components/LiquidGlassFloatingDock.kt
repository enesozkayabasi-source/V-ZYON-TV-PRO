package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.i18n.AppLanguage
import com.example.ui.i18n.AppStrings
import com.example.ui.viewmodel.MainNavigationTab

/**
 * Apple Liquid Glass (Sıvı Buzlu Cam) Yüzen Alt Gezinme Çubuğu (Floating Dock)
 *
 * Mükemmel Geçiş Animasyonları:
 * - Kesintisiz Kayan Sıvı Cam Hap Kapsülü (Sliding Liquid Glass Capsule with Apple Spring Physics)
 * - Simge zıplama ve yaylanma animasyonu (Bouncy pop icon scale)
 * - Yarı saydam koyu tonlu sıvı cam dokusu (Liquid Frosted Glass)
 * - 1.2.dp üstten ışık alan cam çerçeve parıltısı (Specular Rim)
 * - Dokunsal Taptic Engine titreşim geri bildirimi
 * - Android jest çubuğu güvenli alan uyumluluğu (.navigationBarsPadding())
 */
@Composable
fun LiquidGlassFloatingDock(
    selectedTab: MainNavigationTab,
    onTabSelected: (MainNavigationTab) -> Unit,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = true
) {
    val haptic = LocalHapticFeedback.current

    val visibleTabs = remember {
        listOf(
            MainNavigationTab.CANLI_TV,
            MainNavigationTab.HABER,
            MainNavigationTab.COCUK,
            MainNavigationTab.SPOR,
            MainNavigationTab.RADYO
        )
    }

    // Sıvı Cam Arka Plan Dokusu
    val glassBackgroundBrush = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xD90F172A), // Yarı saydam derin lacivert cam
                Color(0xF2070D18)  // Altta derin obsidyen taban
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xE6FFFFFF),
                Color(0xF0E2E8F0)
            )
        )
    }

    // Cam kenar yansıması (Specular Rim Highlight)
    val glassRimBorder = BorderStroke(
        width = 1.2.dp,
        brush = Brush.verticalGradient(
            colors = if (isDarkTheme) {
                listOf(
                    Color.White.copy(alpha = 0.28f), // Üstten vuran ışık yansıması
                    Color.White.copy(alpha = 0.05f)  // Altta kaybolan parıltı
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.90f),
                    Color(0xFFCBD5E1).copy(alpha = 0.40f)
                )
            }
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = RoundedCornerShape(33.dp),
                    spotColor = if (isDarkTheme) Color(0x99000000) else Color(0x33000000),
                    ambientColor = if (isDarkTheme) Color(0x55000000) else Color(0x1A000000)
                )
                .clip(RoundedCornerShape(33.dp))
                .border(border = glassRimBorder, shape = RoundedCornerShape(33.dp)),
            shape = RoundedCornerShape(33.dp),
            color = Color.Transparent
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(glassBackgroundBrush)
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                val dockWidth = maxWidth
                val tabCount = visibleTabs.size
                val tabWidth = dockWidth / tabCount
                val selectedIndex = visibleTabs.indexOf(selectedTab).coerceAtLeast(0)

                // Apple Yaylanma Fiziği (Bouncy Spring Physics) ile Kayan Gösterge
                val targetOffsetX = tabWidth * selectedIndex
                val indicatorOffset by animateDpAsState(
                    targetValue = targetOffsetX,
                    animationSpec = spring(
                        dampingRatio = 0.72f, // Akıcı Apple sıvı yaylanması
                        stiffness = Spring.StiffnessMediumLow
                    ),
                    label = "dockPillOffset"
                )

                // 1. Kayan Sıvı Cam Kapsülü (Sliding Liquid Glow Capsule)
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(
                            width = 1.2.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = if (isDarkTheme) 0.85f else 0.70f),
                                    Color(0xFF0284C7).copy(alpha = if (isDarkTheme) 0.30f else 0.20f)
                                )
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = if (isDarkTheme) 0.32f else 0.22f),
                                    Color(0xFF0284C7).copy(alpha = if (isDarkTheme) 0.16f else 0.10f)
                                )
                            )
                        )
                )

                // 2. Sekme Butonları ve İçerikleri (Kayan kapsülün üstünde)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    visibleTabs.forEach { tab ->
                        val isSelected = selectedTab == tab

                        // Aktif sekme için zıplama/büyüme yaylanması (Pop animation)
                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.20f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = 0.55f, // Canlı zıplama
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "iconScale_${tab.name}"
                        )

                        // Renk geçişleri
                        val iconColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> Color(0xFF38BDF8) // Canlı Elektrik Mavisi
                                isDarkTheme -> Color(0xFF94A3B8)
                                else -> Color(0xFF64748B)
                            },
                            animationSpec = tween(durationMillis = 180),
                            label = "iconColor_${tab.name}"
                        )

                        val textColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> if (isDarkTheme) Color.White else Color(0xFF0284C7)
                                isDarkTheme -> Color(0xFF94A3B8)
                                else -> Color(0xFF64748B)
                            },
                            animationSpec = tween(durationMillis = 180),
                            label = "textColor_${tab.name}"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(24.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, color = Color(0xFF38BDF8)),
                                    onClick = {
                                        if (!isSelected) {
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            onTabSelected(tab)
                                        }
                                    }
                                )
                                .testTag("dock_tab_${tab.key}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = getDockTabIcon(tab),
                                    contentDescription = AppStrings.get(tab.key, currentLanguage),
                                    tint = iconColor,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .scale(iconScale)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = AppStrings.get(tab.key, currentLanguage),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = textColor,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getDockTabIcon(tab: MainNavigationTab): ImageVector {
    return when (tab) {
        MainNavigationTab.CANLI_TV -> Icons.Default.LiveTv
        MainNavigationTab.HABER -> Icons.Default.Newspaper
        MainNavigationTab.COCUK -> Icons.Default.ChildCare
        MainNavigationTab.SPOR -> Icons.Default.SportsSoccer
        MainNavigationTab.RADYO -> Icons.Default.Radio
        MainNavigationTab.FAVORILER -> Icons.Default.Favorite
    }
}
