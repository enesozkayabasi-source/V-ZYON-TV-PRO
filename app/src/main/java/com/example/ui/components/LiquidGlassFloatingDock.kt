package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
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
 * Özellikler:
 * - Kenarlardan boşluklu, yuvarlatılmış yüzen kapsül dock
 * - Yarı saydam koyu tonlu sıvı cam dokusu (Liquid Frosted Glass)
 * - 1.2.dp üstten ışık alan cam çerçeve parıltısı (Specular Rim)
 * - Apple tarzı içten aydınlatmalı ve kenar vurgulu aktif sekme hapı (Glow Capsule)
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

    // Sıvı Cam Arka Plan Geçişleri
    val glassBackgroundBrush = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xCC0F172A), // Üst: yarı saydam koyu lacivert/slate cam
                Color(0xEE0B0F19)  // Alt: derin yarı saydam cam tabanı
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xE6FFFFFF),
                Color(0xF0F1F5F9)
            )
        )
    }

    // Cam kenar ışığı (Specular Rim Highlight)
    val glassRimBorder = BorderStroke(
        width = 1.dp,
        brush = Brush.verticalGradient(
            colors = if (isDarkTheme) {
                listOf(
                    Color.White.copy(alpha = 0.28f), // Üstten gelen parlak ışık kırılması
                    Color.White.copy(alpha = 0.05f)  // Altta kaybolan zarif parıltı
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.85f),
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
                .height(64.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = if (isDarkTheme) Color(0x80000000) else Color(0x33000000),
                    ambientColor = if (isDarkTheme) Color(0x40000000) else Color(0x1A000000)
                )
                .clip(RoundedCornerShape(32.dp))
                .border(border = glassRimBorder, shape = RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(glassBackgroundBrush)
                    .padding(horizontal = 6.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    visibleTabs.forEach { tab ->
                        val isSelected = selectedTab == tab

                        // Aktif sekme için Apple tarzı yumuşak yaylanma animasyonu
                        val tabScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.04f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "tabScale_${tab.name}"
                        )

                        // Renk geçişleri
                        val iconColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> Color(0xFF38BDF8) // Canlı Apple iOS Electric Sky Blue
                                isDarkTheme -> Color(0xFF94A3B8)
                                else -> Color(0xFF64748B)
                            },
                            animationSpec = tween(durationMillis = 200),
                            label = "iconColor_${tab.name}"
                        )

                        val textColor by animateColorAsState(
                            targetValue = when {
                                isSelected -> if (isDarkTheme) Color.White else Color(0xFF0369A1)
                                isDarkTheme -> Color(0xFF94A3B8)
                                else -> Color(0xFF64748B)
                            },
                            animationSpec = tween(durationMillis = 200),
                            label = "textColor_${tab.name}"
                        )

                        // İçten aydınlatmalı hap kapsülü fırçası
                        val glowPillBrush = if (isSelected) {
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF38BDF8).copy(alpha = if (isDarkTheme) 0.28f else 0.20f),
                                    Color(0xFF0284C7).copy(alpha = if (isDarkTheme) 0.12f else 0.08f)
                                )
                            )
                        } else {
                            Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        }

                        val glowPillBorder = if (isSelected) {
                            BorderStroke(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF38BDF8).copy(alpha = 0.60f),
                                        Color(0xFF0284C7).copy(alpha = 0.20f)
                                    )
                                )
                            )
                        } else null

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .scale(tabScale)
                                .clip(RoundedCornerShape(22.dp))
                                .then(
                                    if (glowPillBorder != null) {
                                        Modifier.border(glowPillBorder, RoundedCornerShape(22.dp))
                                    } else Modifier
                                )
                                .background(glowPillBrush)
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
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = AppStrings.get(tab.key, currentLanguage),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
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
