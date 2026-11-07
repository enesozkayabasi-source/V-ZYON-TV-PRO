package com.example.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.MediaCollection
import com.example.model.MediaItem
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed

private data class CardMeta(
    val isHaber: Boolean,
    val isCocuk: Boolean,
    val isRadio: Boolean,
    val isSport: Boolean,
    val displayCategory: String,
    val categoryColor: Color
)

@Composable
fun MediaCard(
    item: MediaItem,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    val meta = remember(item.id, item.baslik, item.kategori, item.collectionType) {
        val h = item.collectionType == MediaCollection.HABER ||
            item.kategori.contains("haber", ignoreCase = true) ||
            item.baslik.contains("haber", ignoreCase = true) ||
            item.baslik.contains("ntv", ignoreCase = true) ||
            item.baslik.contains("bloomberg", ignoreCase = true) ||
            item.baslik.contains("cnbc", ignoreCase = true)

        val c = item.collectionType == MediaCollection.COCUK ||
            item.kategori.contains("çocuk", ignoreCase = true) ||
            item.kategori.contains("cocuk", ignoreCase = true) ||
            item.baslik.contains("çocuk", ignoreCase = true) ||
            item.baslik.contains("disney", ignoreCase = true) ||
            item.baslik.contains("junior", ignoreCase = true) ||
            item.baslik.contains("jnr", ignoreCase = true) ||
            item.baslik.contains("minika", ignoreCase = true) ||
            item.baslik.contains("cartoon", ignoreCase = true) ||
            item.baslik.contains("baby", ignoreCase = true) ||
            item.baslik.contains("zarok", ignoreCase = true) ||
            item.baslik.contains("nickelodeon", ignoreCase = true) ||
            item.baslik.contains("pepee", ignoreCase = true)

        val r = item.collectionType == MediaCollection.RADYO ||
            item.kategori.contains("radyo", ignoreCase = true) ||
            item.baslik.contains("radyo", ignoreCase = true) ||
            item.baslik.contains("fm", ignoreCase = true)

        val s = !h && !c && !r && (
            item.collectionType == MediaCollection.SPOR ||
            item.kategori.equals("Spor", ignoreCase = true) ||
            item.baslik.contains("spor", ignoreCase = true) ||
            item.baslik.contains("tjk", ignoreCase = true)
        )

        val dispCat = when {
            r -> if (item.kategori.isBlank() || item.kategori.equals("Genel", ignoreCase = true)) "Canlı Radyo" else "Radyo • ${item.kategori}"
            h -> {
                val clean = item.kategori
                    .replace("Çizgi Film", "Gündem", ignoreCase = true)
                    .replace("Film", "Haber", ignoreCase = true)
                    .replace("Sinema", "Haber", ignoreCase = true)
                if (clean.isBlank() || clean.equals("Genel", ignoreCase = true)) "Canlı Haber" else clean
            }
            c -> {
                val clean = item.kategori
                    .replace("Çizgi Film", "Çizgi Dizi", ignoreCase = true)
                    .replace("Film", "Çizgi Dizi", ignoreCase = true)
                    .replace("Sinema", "Çocuk", ignoreCase = true)
                if (clean.isBlank() || clean.equals("Genel", ignoreCase = true)) "Çocuk Kulübü" else clean
            }
            else -> item.kategori
        }

        val catCol = when {
            h -> Color(0xFFFB7185) // Canlı Mercan Kırmızı
            c -> Color(0xFFFBBF24) // Sıcak Güneş Sarısı / Amber
            s -> Color(0xFF4ADE80) // Stadyum Çim Yeşili
            r -> Color(0xFFC084FC) // Kozmik Neon Mor
            else -> Color(0xFF38BDF8)    // Elektrik Cyan Mavi (Canlı TV)
        }

        CardMeta(h, c, r, s, dispCat, catCol)
    }

    val isHaber = meta.isHaber
    val isCocuk = meta.isCocuk
    val isRadio = meta.isRadio
    val isSport = meta.isSport
    val displayCategory = meta.displayCategory
    val categoryColor = meta.categoryColor

    val cardAspectRatio = 16f / 9f
    val cardBg = if (isFocused) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = (if (isFocused) modifier.scale(1.04f) else modifier)
            .testTag("media_card_${item.id}")
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = categoryColor),
                onClick = onClick
            )
            .border(
                width = if (isFocused) 2.5.dp else 1.dp,
                color = if (isFocused) categoryColor else categoryColor.copy(alpha = 0.24f),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            focusedElevation = 2.dp
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val context = LocalContext.current
            val resolvedLogoUrl = remember(item.resim_url, item.baslik) {
                if (item.resim_url.isNotBlank() && !item.resim_url.contains("unsplash")) {
                    item.resim_url
                } else {
                    com.example.service.InternetChannelService.getOfficialWebLogo(item.baslik)
                }
            }

            val imageRequest = remember(resolvedLogoUrl) {
                if (resolvedLogoUrl.isBlank()) null
                else {
                    ImageRequest.Builder(context)
                        .data(resolvedLogoUrl)
                        .crossfade(false)
                        .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .setHeader("Referer", "https://www.google.com/")
                        .memoryCacheKey(resolvedLogoUrl)
                        .diskCacheKey(resolvedLogoUrl)
                        .build()
                }
            }

            var isImageLoadError by remember(resolvedLogoUrl) { mutableStateOf(false) }

            // Channel Logo / Media Image Container (Placed cleanly at the top with 16:9 ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(cardAspectRatio)
                    .background(Color(0xFF090D17)),
                contentAlignment = Alignment.Center
            ) {
                if (imageRequest != null && !isImageLoadError) {
                    AsyncImage(
                        model = imageRequest,
                        contentDescription = item.baslik,
                        contentScale = ContentScale.Fit,
                        onError = { isImageLoadError = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when {
                                isRadio -> Icons.Default.Radio
                                isSport -> Icons.Default.SportsSoccer
                                else -> Icons.Default.LiveTv
                            },
                            contentDescription = null,
                            tint = categoryColor.copy(alpha = 0.85f),
                            modifier = Modifier.size(46.dp)
                        )
                    }
                }

                // Subtle bottom shadow gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.45f)
                                ),
                                startY = 100f
                            )
                        )
                )

                // Top badges row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isHaber) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFB7185).copy(alpha = 0.95f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "HABER",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else if (isCocuk) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFBBF24).copy(alpha = 0.95f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "ÇOCUK",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else if (isSport) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF4ADE80).copy(alpha = 0.95f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "SPOR",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else if (isRadio) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFC084FC).copy(alpha = 0.95f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "RADYO",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF38BDF8).copy(alpha = 0.95f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CANLI",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // IMDb rating badge if available
                    if (item.imdbRating.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.75f),
                            border = BorderStroke(1.dp, NeonAmber.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = "★ ${item.imdbRating}",
                                color = NeonAmber,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Favorite Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (item.isFavorite) NeonRed else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Focus play icon highlight
                if (isFocused) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(44.dp)
                            .background(categoryColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Oynat",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            // Info section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Text(
                    text = item.baslik,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isFocused) categoryColor else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = displayCategory,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(categoryColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = when {
                                isHaber -> "Haber"
                                isCocuk -> "Çocuk"
                                isSport -> "Spor"
                                isRadio -> "Radyo"
                                else -> "Canlı"
                            },
                            color = categoryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
