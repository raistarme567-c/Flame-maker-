package com.example.monetization

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import kotlinx.coroutines.delay

object AdMobConfig {
    const val APP_ID = "ca-app-pub-1709872861108358~5137745755"
    const val INTERSTITIAL_AD_ID = "ca-app-pub-1709872861108358/4551939312" // Interstitial / Instant Ads
    const val BANNER_AD_ID = "ca-app-pub-1709872861108358/4100142042" // Banner Ads

    // Interstitial Cooldown Timing (in milliseconds)
    // Default: 45 seconds between popup ads on button clicks
    var interstitialCooldownMs: Long = 45_000L
    var lastInterstitialTimestamp: Long = 0L

    fun shouldShowInterstitial(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastInterstitialTimestamp >= interstitialCooldownMs) {
            lastInterstitialTimestamp = now
            return true
        }
        return false
    }

    fun forceShowInterstitial() {
        lastInterstitialTimestamp = System.currentTimeMillis()
    }
}

data class AdCreative(
    val title: String,
    val description: String,
    val ctaText: String,
    val rating: String,
    val icon: ImageVector,
    val primaryColor: Color,
    val secondaryColor: Color,
    val sponsorName: String
)

val sampleBannerAds = listOf(
    AdCreative(
        title = "Cyber Legends: Battle Royale",
        description = "Survive the warzone! 100 Players, Real-time 3D shooter.",
        ctaText = "INSTALL",
        rating = "4.8 ★",
        icon = Icons.Default.SportsEsports,
        primaryColor = Color(0xFFFF5722),
        secondaryColor = Color(0xFF3E2723),
        sponsorName = "AdMob • Cyber Interactive"
    ),
    AdCreative(
        title = "Cloud Game Server VPS 99.9%",
        description = "Ultra-low ping multiplayer hosting. Instant setup.",
        ctaText = "TRY FREE",
        rating = "4.9 ★",
        icon = Icons.Default.CloudQueue,
        primaryColor = Color(0xFF00E5FF),
        secondaryColor = Color(0xFF002244),
        sponsorName = "AdMob • Nexus Cloud"
    ),
    AdCreative(
        title = "3D Unreal Shader Master Kit",
        description = "500+ PBR Textures & Particle FX. Download now.",
        ctaText = "DOWNLOAD",
        rating = "4.7 ★",
        icon = Icons.Default.ViewInAr,
        primaryColor = Color(0xFF00E676),
        secondaryColor = Color(0xFF003311),
        sponsorName = "AdMob • GameDev Asset Store"
    ),
    AdCreative(
        title = "Pro Gaming Headset RGB 7.1",
        description = "70% OFF Limited Time Flash Sale! Free delivery.",
        ctaText = "SHOP NOW",
        rating = "4.9 ★",
        icon = Icons.Default.Headphones,
        primaryColor = Color(0xFFFFD600),
        secondaryColor = Color(0xFF332A00),
        sponsorName = "AdMob • GearStore"
    )
)

@Composable
fun AdMobBannerView(
    modifier: Modifier = Modifier,
    adUnitId: String = AdMobConfig.BANNER_AD_ID
) {
    var adIndex by remember { mutableIntStateOf(0) }
    var isAdVisible by remember { mutableStateOf(true) }
    var showAdDetails by remember { mutableStateOf(false) }

    // Auto rotate banner every 8 seconds, just like real Google AdMob smart banners
    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)
            if (isAdVisible) {
                adIndex = (adIndex + 1) % sampleBannerAds.size
            }
        }
    }

    if (!isAdVisible) {
        // Collapsed / Closed Ad State with Reload timer
        LaunchedEffect(Unit) {
            delay(10000) // re-fetch ad after 10s
            isAdVisible = true
        }
        return
    }

    val currentAd = sampleBannerAds[adIndex]

    Surface(
        color = SurfaceDark,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .testTag("admob_banner_view")
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ad Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(currentAd.primaryColor.copy(alpha = 0.8f), currentAd.secondaryColor)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        currentAd.icon,
                        contentDescription = "Ad Icon",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Ad Text Details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showAdDetails = !showAdDetails }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = FlameOrange,
                            shape = RoundedCornerShape(3.dp)
                        ) {
                            Text(
                                text = "Ad",
                                color = CanvasDark,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Text(
                            text = currentAd.title,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = currentAd.description,
                        color = TextSecondary,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // CTA Button
                Button(
                    onClick = { showAdDetails = !showAdDetails },
                    colors = ButtonDefaults.buttonColors(containerColor = currentAd.primaryColor),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = currentAd.ctaText,
                        color = CanvasDark,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                // Close Button
                IconButton(
                    onClick = { isAdVisible = false },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close Ad", tint = TextMuted, modifier = Modifier.size(14.dp))
                }
            }

            AnimatedVisibility(visible = showAdDetails) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                        .background(CanvasDark, RoundedCornerShape(6.dp))
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(currentAd.sponsorName, color = MatrixGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("Rating: ${currentAd.rating}", color = FlameAmber, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Ad Unit: $adUnitId", color = TextMuted, fontFamily = FontFamily.Monospace, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun AdMobInterstitialDialog(
    adUnitId: String = AdMobConfig.INTERSTITIAL_AD_ID,
    onDismiss: () -> Unit
) {
    var countdown by remember { mutableIntStateOf(5) }
    val adIndex = remember { (0 until sampleBannerAds.size).random() }
    val ad = sampleBannerAds[adIndex]

    LaunchedEffect(Unit) {
        while (countdown > 0) {
            delay(1000)
            countdown -= 1
        }
    }

    Dialog(
        onDismissRequest = { if (countdown == 0) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.96f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Bar inside Ad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(color = FlameOrange, shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    "Ad",
                                    color = CanvasDark,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                "Google AdMob Instant Ad",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (countdown > 0) {
                            Surface(
                                color = SurfaceDark,
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                            ) {
                                Text(
                                    text = "Skip in ${countdown}s",
                                    color = FlameAmber,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp).testTag("close_interstitial_ad_button")
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Close", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Ad Poster Creative Graphic Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(ad.primaryColor.copy(alpha = 0.9f), Color(0xFF0F111E))
                                )
                            )
                            .border(1.dp, ad.primaryColor.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(CanvasDark.copy(alpha = 0.6f))
                                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    ad.icon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Text(
                                text = ad.title,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = ad.description,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(ad.rating, color = FlameAmber, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text("• 50M+ Downloads", color = MatrixGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Install / Play Action
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = ad.primaryColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = CanvasDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${ad.ctaText} NOW - FREE",
                            color = CanvasDark,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }

                    // Meta Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Unit: ${adUnitId.take(24)}...",
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp
                        )
                        Text(
                            text = "Ads by Google",
                            color = TextMuted,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}
