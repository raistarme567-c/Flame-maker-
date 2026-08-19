package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DevicePreviewPreset
import com.example.ui.theme.*

@Composable
fun DevicePreviewContainer(
    preset: DevicePreviewPreset,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CanvasDark),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(preset.widthAspect / preset.heightAspect)
                .fillMaxSize()
                .border(2.dp, BorderSubtle, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        ) {
            content()

            if (preset.hasNotch) {
                // Top Camera Notch / Dynamic Island
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 6.dp)
                        .width(80.dp)
                        .height(18.dp)
                        .background(Color.Black, RoundedCornerShape(9.dp))
                )
            }
        }
    }
}
