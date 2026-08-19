package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.EngineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneAuthScreen(
    viewModel: EngineViewModel,
    onNavigateBack: () -> Unit
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val developerName by viewModel.developerName.collectAsState()
    val otpSent by viewModel.otpSent.collectAsState()
    val generatedOtp by viewModel.generatedOtp.collectAsState()
    val otpCountdown by viewModel.otpCountdown.collectAsState()
    val authStatusMessage by viewModel.authStatusMessage.collectAsState()

    var phoneInput by remember { mutableStateOf(userPhone ?: "") }
    var devNameInput by remember { mutableStateOf(developerName) }
    var otpInput by remember { mutableStateOf("") }
    var countryCode by remember { mutableStateOf("+91") }

    Scaffold(
        containerColor = CanvasDark,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isLoggedIn) "Developer Cloud Profile" else "Phone Authentication",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isLoggedIn) "Firebase Cloud Connected" else "Sign in with Mobile OTP",
                            color = if (isLoggedIn) MatrixGreen else FlameOrange,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("auth_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoggedIn) {
                // Logged In Profile View
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(FlameOrange.copy(alpha = 0.2f))
                                    .border(2.dp, FlameOrange, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "User Avatar",
                                    tint = FlameOrange,
                                    modifier = Modifier.size(48.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = developerName,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$countryCode $userPhone",
                                    color = TextSecondary,
                                    fontSize = 14.sp
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = MatrixGreen.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(MatrixGreen)
                                        )
                                        Text("Firebase Cloud Sync Active", color = MatrixGreen, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }

                            HorizontalDivider(color = BorderSubtle)

                            // Quick Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Dev Tier", color = TextSecondary, fontSize = 11.sp)
                                    Text("Flame Pro", color = FlameOrange, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Cloud Builds", color = TextSecondary, fontSize = 11.sp)
                                    Text("Unlimited", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Multiplayer", color = TextSecondary, fontSize = 11.sp)
                                    Text("Enabled", color = PlasmaCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = { viewModel.logout() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("logout_button")
                            ) {
                                Icon(Icons.Default.ExitToApp, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Not Logged In - Phone Auth Form
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(FlameOrange.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = FlameOrange)
                                }
                                Column {
                                    Text(
                                        text = "Mobile OTP Login",
                                        color = TextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Login to sync games and publish APKs to cloud",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            HorizontalDivider(color = BorderSubtle)

                            // Developer Name Input
                            OutlinedTextField(
                                value = devNameInput,
                                onValueChange = { devNameInput = it },
                                label = { Text("Developer / Studio Name") },
                                placeholder = { Text("e.g. CyberStudio Games") },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = FlameOrange,
                                    unfocusedBorderColor = BorderSubtle
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("dev_name_input")
                            )

                            // Phone Number Input with Country Code
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = countryCode,
                                    onValueChange = { countryCode = it },
                                    label = { Text("Code") },
                                    modifier = Modifier.width(80.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = FlameOrange,
                                        unfocusedBorderColor = BorderSubtle
                                    )
                                )

                                OutlinedTextField(
                                    value = phoneInput,
                                    onValueChange = { if (it.length <= 10) phoneInput = it },
                                    label = { Text("Mobile Number") },
                                    placeholder = { Text("9876543210") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = FlameOrange,
                                        unfocusedBorderColor = BorderSubtle
                                    ),
                                    modifier = Modifier.weight(1f).testTag("phone_number_input")
                                )
                            }

                            // Send OTP Button
                            Button(
                                onClick = {
                                    viewModel.sendPhoneOtp(phoneInput)
                                },
                                enabled = phoneInput.length >= 10 && otpCountdown == 0,
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().testTag("send_otp_button")
                            ) {
                                Icon(Icons.Default.Send, contentDescription = null, tint = CanvasDark)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (otpCountdown > 0) "Resend OTP in ${otpCountdown}s" else if (otpSent) "Resend OTP" else "Send OTP Code",
                                    color = CanvasDark,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // OTP Input Section (shown when OTP is sent)
                            if (otpSent) {
                                Surface(
                                    color = CanvasDark,
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, FlameOrange.copy(alpha = 0.5f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Enter 6-Digit OTP", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            // Demo Auto-fill Button for ease of testing
                                            TextButton(
                                                onClick = { otpInput = generatedOtp },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                            ) {
                                                Text("Auto-Fill Demo ($generatedOtp)", color = PlasmaCyan, fontSize = 11.sp)
                                            }
                                        }

                                        OutlinedTextField(
                                            value = otpInput,
                                            onValueChange = { if (it.length <= 6) otpInput = it },
                                            placeholder = { Text("••••••") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            textStyle = LocalTextStyle.current.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 20.sp,
                                                letterSpacing = 6.sp,
                                                color = FlameOrange
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = FlameOrange,
                                                unfocusedTextColor = FlameOrange,
                                                focusedBorderColor = FlameOrange,
                                                unfocusedBorderColor = BorderSubtle
                                            ),
                                            modifier = Modifier.fillMaxWidth().testTag("otp_input_field")
                                        )

                                        Button(
                                            onClick = {
                                                val success = viewModel.verifyPhoneOtp(otpInput, devNameInput)
                                                if (success) {
                                                    onNavigateBack()
                                                }
                                            },
                                            enabled = otpInput.length >= 4,
                                            colors = ButtonDefaults.buttonColors(containerColor = MatrixGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth().testTag("verify_otp_button")
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CanvasDark)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Verify & Continue", color = CanvasDark, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            // Status Banner
                            authStatusMessage?.let { msg ->
                                Surface(
                                    color = SurfaceDark,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = msg,
                                        color = if (isLoggedIn) MatrixGreen else FlameOrange,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(10.dp)
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
