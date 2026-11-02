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
import androidx.compose.foundation.shape.CircleShape
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
 * Apple Liquid Glass (Ultra Şık Saydam Yüzen Dock)
 *
 * Yenilikler:
 * - Ağır ve koyu arka plan tamamen kaldırıldı (Kristal berraklığında ultra saydam yüzen panel)
 * - Her Butona Özel Canlı Renk Kimliği (Canlı TV: Cyan, Haber: Mercan Kırmızı, Çocuk: Güneş Sarısı, Spor: Stadyum Yeşili, Radyo: Neon Mor)
 * - Kayan Sıvı Cam Kapsülü rengini aktif butona göre pürüzsüzce dönüştürür (Color-morphing Liquid Capsule)
 * - Zarif üstten ışık yansıtan yarı saydam cam çerçevesi (Frosted Specular Rim)
 * - Dokunsal Taptic Engine titreşim geri bildirimi
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

    // Aktif sekmenin özel renk kimliği
    val activeTabColor = getDockTabColor(selectedTab)
    val activeTabDarkColor = getDockTabDarkColor(selectedTab)

    val activePrimaryColor by animateColorAsState(
        targetValue = activeTabColor,
        animationSpec = tween(durationMillis = 220),
        label = "activeDockColor"
    )

    val activeSecondaryColor by animateColorAsState(
        targetValue = activeTabDarkColor,
        animationSpec = tween(durationMillis = 220),
        label = "activeDockDarkColor"
    )

    // Ultra şık, hafif ve saydam cam kenar çizgisi (Koyu panel arka planı kaldırıldı!)
    val ultraChicGlassRimBorder = BorderStroke(
        width = 1.dp,
        brush = Brush.horizontalGradient(
            colors = if (isDarkTheme) {
                listOf(
                    Color.White.copy(alpha = 0.18f),
                    Color.White.copy(alpha = 0.05f),
                    Color.White.copy(alpha = 0.18f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.65f),
                    Color(0xFFCBD5E1).copy(alpha = 0.25f),
                    Color.White.copy(alpha = 0.65f)
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
        // Tamamen saydam, zemin rengi olmayan şık kristal cam kapsül
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .border(border = ultraChicGlassRimBorder, shape = RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            color = Color.Transparent // Arka plan tamamen şeffaf
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
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

                // 1. Kayan Sıvı Cam Kapsülü (Rengi seçili butona göre değişen parlak aydınlatmalı hap)
                Box(
                    modifier = Modifier
                        .offset(x = indicatorOffset)
                        .width(tabWidth)
                        .fillMaxHeight()
                        .padding(horizontal = 3.dp, vertical = 2.dp)
                        .shadow(
                            elevation = 10.dp,
                            shape = RoundedCornerShape(22.dp),
                            spotColor = activePrimaryColor.copy(alpha = 0.65f)
                        )
                        .clip(RoundedCornerShape(22.dp))
                        .border(
                            width = 1.4.dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    activePrimaryColor.copy(alpha = 0.95f),
                                    activeSecondaryColor.copy(alpha = 0.35f)
                                )
                            ),
                            shape = RoundedCornerShape(22.dp)
                        )
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    activePrimaryColor.copy(alpha = 0.28f),
                                    activeSecondaryColor.copy(alpha = 0.12f)
                                )
                            )
                        )
                )

                // 2. Sekme Butonları (Her buton kendi özel rengiyle canlı parlar)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    visibleTabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val tabColor = getDockTabColor(tab)

                        // Aktif sekme için zıplama/büyüme yaylanması (Pop animation)
                        val iconScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.22f else 1.0f,
                            animationSpec = spring(
                                dampingRatio = 0.55f,
                                stiffness = Spring.StiffnessMedium
                            ),
                            label = "iconScale_${tab.name}"
                        )

                        // Her butonun KENDİNE ÖZEL CANLI RENGİ (Seçilmemişken de rengini korur, seçilince en parlak hale gelir)
                        val iconColor by animateColorAsState(
                            targetValue = if (isSelected) tabColor else tabColor.copy(alpha = 0.80f),
                            animationSpec = tween(durationMillis = 200),
                            label = "iconColor_${tab.name}"
                        )

                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color.White else tabColor.copy(alpha = 0.90f),
                            animationSpec = tween(durationMillis = 200),
                            label = "textColor_${tab.name}"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(22.dp))
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = ripple(bounded = true, color = tabColor),
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = AppStrings.get(tab.key, currentLanguage),
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                    color = textColor,
                                    maxLines = 1
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(tabColor)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Her buton için kendine özel canlı renk kimliği
 */
fun getDockTabColor(tab: MainNavigationTab): Color {
    return when (tab) {
        MainNavigationTab.CANLI_TV -> Color(0xFF38BDF8) // Canlı Elektrik Mavisi (Cyan)
        MainNavigationTab.HABER -> Color(0xFFFB7185)   // Canlı Mercan Kırmızısı (Coral Crimson)
        MainNavigationTab.COCUK -> Color(0xFFFBBF24)   // Sıcak Güneş Sarısı / Marigold Amber
        MainNavigationTab.SPOR -> Color(0xFF4ADE80)    // Dinamik Çim / Stadyum Yeşili (Emerald)
        MainNavigationTab.RADYO -> Color(0xFFC084FC)   // Kozmik Neon Mor / Menekşe (Violet)
        MainNavigationTab.FAVORILER -> Color(0xFFF43F5E) // Kalp Kırmızısı
    }
}

fun getDockTabDarkColor(tab: MainNavigationTab): Color {
    return when (tab) {
        MainNavigationTab.CANLI_TV -> Color(0xFF0284C7)
        MainNavigationTab.HABER -> Color(0xFFE11D48)
        MainNavigationTab.COCUK -> Color(0xFFD97706)
        MainNavigationTab.SPOR -> Color(0xFF16A34A)
        MainNavigationTab.RADYO -> Color(0xFF9333EA)
        MainNavigationTab.FAVORILER -> Color(0xFFBE123C)
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
