package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeePaymentEntity
import com.example.data.model.StudentEntity
import com.example.localization.LanguageManager
import com.example.ui.theme.*
import com.example.ui.viewmodel.AnalyticsData
import com.example.ui.viewmodel.ScornerViewModel

@Composable
fun FeesAndPaymentsScreen(
    viewModel: ScornerViewModel,
    student: StudentEntity?,
    fees: List<FeePaymentEntity>,
    analytics: AnalyticsData,
    modifier: Modifier = Modifier
) {
    val pendingFees = fees.filter { it.status != "PAID" }
    val paidFees = fees.filter { it.status == "PAID" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tuition Fees Master Financial Banner
        item {
            FinancialMasterBanner(
                pendingAmount = analytics.pendingFeeTotal,
                paidAmount = analytics.paidFeeTotal,
                student = student
            )
        }

        // Security Assurance Pill
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Slate100),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = SuccessGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SCORNER Secure Gateway: Encrypted educational payments via bKash, Cards, and NetBanking.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Slate800
                    )
                }
            }
        }

        // Pending Fees Section
        item {
            Text(
                text = "Pending Dues & Tuition (${pendingFees.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (pendingFees.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "All tuition and educational expenses are fully cleared!",
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }
        } else {
            items(pendingFees) { fee ->
                PendingFeeCard(
                    fee = fee,
                    onPay = { viewModel.showPaymentSheetForFee.value = fee }
                )
            }
        }

        // Paid Transactions & Receipts History
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Transaction History & Official Receipts (${paidFees.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (paidFees.isEmpty()) {
            item {
                EmptyStateCard(message = "No past transaction receipts found.")
            }
        } else {
            items(paidFees) { fee ->
                PaidFeeCard(
                    fee = fee,
                    onViewReceipt = { viewModel.activeReceipt.value = fee }
                )
            }
        }
    }
}

@Composable
fun FinancialMasterBanner(
    pendingAmount: Double,
    paidAmount: Double,
    student: StudentEntity?
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "TUITION & EXPENSES LEDGER",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "৳${pendingAmount.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "Total Outstanding Dues",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (pendingAmount > 0) ErrorRed else SuccessGreen
                ) {
                    Text(
                        text = if (pendingAmount > 0) "Payment Due" else "Fully Paid",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Paid: ৳${paidAmount.toInt()}", color = Color.White, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                Text(student?.college ?: "Dhaka College", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PendingFeeCard(
    fee: FeePaymentEntity,
    onPay: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fee.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Due Date: ${fee.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = ErrorRed,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "৳${fee.amount.toInt()}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = PrimaryBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onPay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("pay_fee_button_${fee.id}"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Icon(Icons.Filled.Payment, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(LanguageManager.getString("pay_fees"), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PaidFeeCard(
    fee: FeePaymentEntity,
    onViewReceipt: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fee.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Paid on ${fee.paidDate} via ${fee.paymentMethod}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Txn: ${fee.transactionId ?: "TXN-BK-992147"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "৳${fee.amount.toInt()}",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleMedium,
                    color = SuccessGreen
                )

                TextButton(onClick = onViewReceipt) {
                    Icon(Icons.Filled.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Receipt")
                }
            }
        }
    }
}
