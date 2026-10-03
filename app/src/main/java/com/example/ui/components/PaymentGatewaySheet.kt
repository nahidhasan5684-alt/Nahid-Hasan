package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeePaymentEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentGatewaySheet(
    fee: FeePaymentEntity,
    onDismiss: () -> Unit,
    onPaymentSuccess: (method: String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("bKash Digital Payment") }
    var accountOrCardNumber by remember { mutableStateOf("01712-345678") }
    var pinOrCvv by remember { mutableStateOf("1234") }
    var isProcessing by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("payment_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Secure Tuition Checkout",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = fee.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "৳${fee.amount.toInt()}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = PrimaryBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Methods
            Text("Select Payment Gateway", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            val methods = listOf(
                PaymentMethodOption("bKash Digital Payment", "Instant mobile wallet payment", Color(0xFFE2136E)),
                PaymentMethodOption("Nagad / Rocket", "Zero fee mobile banking", Color(0xFFF7941D)),
                PaymentMethodOption("Credit / Debit Card", "Visa, Mastercard & Amex", PrimaryBlue),
                PaymentMethodOption("Direct Bank Transfer", "City Touch, IBBL & EBL NetBanking", SecondaryTeal)
            )

            methods.forEach { opt ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMethod == opt.name) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedMethod = opt.name }
                        .testTag("payment_method_${opt.name.take(5)}")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedMethod == opt.name,
                            onClick = { selectedMethod = opt.name }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(opt.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(opt.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Payment Credentials Input
            OutlinedTextField(
                value = accountOrCardNumber,
                onValueChange = { accountOrCardNumber = it },
                label = { Text(if (selectedMethod.contains("Card")) "Card Number" else "Mobile Account Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("payment_account_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pinOrCvv,
                onValueChange = { pinOrCvv = it },
                label = { Text(if (selectedMethod.contains("Card")) "CVV / Security Code" else "Wallet PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("payment_pin_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Security Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "256-bit SSL Encrypted • SCORNER Verified Gateway",
                    style = MaterialTheme.typography.labelSmall,
                    color = Slate700
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action
            Button(
                onClick = {
                    isProcessing = true
                    onPaymentSuccess(selectedMethod)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("confirm_payment_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
            ) {
                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pay ৳${fee.amount.toInt()} Securely",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class PaymentMethodOption(
    val name: String,
    val description: String,
    val brandColor: Color
)
