package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.AuthManager
import com.example.auth.UserRole
import com.example.localization.LanguageManager
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryBlue
import com.example.ui.viewmodel.ScornerViewModel
import kotlinx.coroutines.launch

enum class ScreenTab(val titleKey: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("dashboard", Icons.Filled.Dashboard),
    TRANSCRIPTS("transcripts", Icons.Filled.School),
    FEES("fees", Icons.Filled.Payment),
    SEARCH("search", Icons.Filled.Search),
    REPORTS("reports", Icons.Filled.BarChart),
    PORTAL("portal", Icons.Filled.AdminPanelSettings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ScornerApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScornerApp(viewModel: ScornerViewModel = viewModel()) {
    var currentTab by remember { mutableStateOf(ScreenTab.DASHBOARD) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showMfaDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // BackHandler: Navigate back to Dashboard tab if on another screen
    BackHandler(enabled = currentTab != ScreenTab.DASHBOARD) {
        currentTab = ScreenTab.DASHBOARD
    }

    // Collect Reactive State
    val student by viewModel.currentStudent.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val transcripts by viewModel.currentTranscripts.collectAsStateWithLifecycle()
    val certificates by viewModel.currentCertificates.collectAsStateWithLifecycle()
    val fees by viewModel.currentFees.collectAsStateWithLifecycle()
    val deadlines by viewModel.currentDeadlines.collectAsStateWithLifecycle()
    val materials by viewModel.courseMaterials.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val analytics by viewModel.analyticsSummary.collectAsStateWithLifecycle()

    // Dialog states
    val activeReceipt by viewModel.activeReceipt.collectAsStateWithLifecycle()
    val shareTargetText by viewModel.shareTargetText.collectAsStateWithLifecycle()
    val showAddStudent by viewModel.showAddStudentDialog.collectAsStateWithLifecycle()
    val showEditStudent by viewModel.showEditStudentDialog.collectAsStateWithLifecycle()
    val showAddTranscript by viewModel.showAddTranscriptDialog.collectAsStateWithLifecycle()
    val showAddCertificate by viewModel.showAddCertificateDialog.collectAsStateWithLifecycle()
    val showAddDeadline by viewModel.showAddDeadlineDialog.collectAsStateWithLifecycle()
    val paymentFee by viewModel.showPaymentSheetForFee.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    // Trigger Snackbar on message
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.snackbarMessage.value = null
        }
    }

    val unreadCount = notifications.count { !it.isRead }

    Scaffold(
        topBar = {
            ScornerTopBar(
                unreadNotificationCount = unreadCount,
                onOpenNotifications = { showNotificationsSheet = true },
                onSyncClick = { viewModel.triggerDatabaseSync() },
                onOpenMfa = { showMfaDialog = true }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.testTag("scorner_bottom_nav")
            ) {
                listOf(
                    ScreenTab.DASHBOARD,
                    ScreenTab.TRANSCRIPTS,
                    ScreenTab.FEES,
                    ScreenTab.SEARCH,
                    ScreenTab.REPORTS,
                    ScreenTab.PORTAL
                ).forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = { currentTab = tab },
                        icon = { Icon(tab.icon, contentDescription = LanguageManager.getString(tab.titleKey)) },
                        label = {
                            Text(
                                text = LanguageManager.getString(tab.titleKey),
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PrimaryBlue,
                            selectedTextColor = PrimaryBlue,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                ScreenTab.DASHBOARD -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        student = student,
                        transcripts = transcripts,
                        certificates = certificates,
                        deadlines = deadlines,
                        analytics = analytics,
                        onNavigateToTranscripts = { currentTab = ScreenTab.TRANSCRIPTS },
                        onNavigateToFees = { currentTab = ScreenTab.FEES }
                    )
                }
                ScreenTab.TRANSCRIPTS -> {
                    TranscriptsAndCertificatesScreen(
                        viewModel = viewModel,
                        student = student,
                        transcripts = transcripts,
                        certificates = certificates,
                        analytics = analytics
                    )
                }
                ScreenTab.FEES -> {
                    FeesAndPaymentsScreen(
                        viewModel = viewModel,
                        student = student,
                        fees = fees,
                        analytics = analytics
                    )
                }
                ScreenTab.SEARCH -> {
                    SearchAndMaterialsScreen(
                        viewModel = viewModel,
                        materials = materials,
                        searchQuery = searchQuery
                    )
                }
                ScreenTab.REPORTS -> {
                    ReportingDashboardScreen(
                        viewModel = viewModel,
                        students = allStudents,
                        analytics = analytics
                    )
                }
                ScreenTab.PORTAL -> {
                    RolePortalsScreen(
                        viewModel = viewModel,
                        student = student
                    )
                }
            }
        }
    }

    // Modal Dialogs & Sheets
    if (showMfaDialog) {
        MfaVerificationDialog(
            onDismiss = { showMfaDialog = false },
            onVerified = {
                showMfaDialog = false
                scope.launch {
                    snackbarHostState.showSnackbar("MFA Authenticated: Access permissions confirmed.")
                }
            }
        )
    }

    activeReceipt?.let { fee ->
        FeeReceiptDialog(
            fee = fee,
            student = student,
            onDismiss = { viewModel.activeReceipt.value = null }
        )
    }

    shareTargetText?.let { text ->
        SocialShareDialog(
            shareText = text,
            onDismiss = { viewModel.shareTargetText.value = null }
        )
    }

    if (showAddStudent) {
        AddStudentDialog(
            onDismiss = { viewModel.showAddStudentDialog.value = false },
            onSave = { name, className, gpa, college, address, phone, email, guardian ->
                viewModel.saveStudent(name, className, gpa, college, address, phone, email, guardian)
            }
        )
    }

    if (showEditStudent && student != null) {
        EditStudentProfileDialog(
            student = student!!,
            onDismiss = { viewModel.showEditStudentDialog.value = false },
            onSave = { updated ->
                viewModel.updateStudentInfo(updated)
            }
        )
    }

    if (showAddTranscript) {
        AddTranscriptDialog(
            onDismiss = { viewModel.showAddTranscriptDialog.value = false },
            onSave = { subject, semester, marks, gradePoint, letterGrade, credits, remarks ->
                viewModel.addTranscript(subject, semester, marks, gradePoint, letterGrade, credits, remarks)
            }
        )
    }

    if (showAddCertificate) {
        AddCertificateDialog(
            onDismiss = { viewModel.showAddCertificateDialog.value = false },
            onSave = { title, issuer, category, verificationId ->
                viewModel.addCertificate(title, issuer, category, verificationId)
            }
        )
    }

    if (showAddDeadline) {
        AddDeadlineDialog(
            onDismiss = { viewModel.showAddDeadlineDialog.value = false },
            onSave = { title, course, deadlineDate, priority, type ->
                viewModel.addDeadline(title, course, deadlineDate, priority, type)
            }
        )
    }

    paymentFee?.let { fee ->
        PaymentGatewaySheet(
            fee = fee,
            onDismiss = { viewModel.showPaymentSheetForFee.value = null },
            onPaymentSuccess = { method ->
                viewModel.processPayment(fee, method)
            }
        )
    }

    if (showNotificationsSheet) {
        NotificationsBottomSheet(
            notifications = notifications,
            onDismiss = { showNotificationsSheet = false },
            onMarkRead = { id -> viewModel.markNotificationRead(id) },
            onClearAll = { viewModel.clearNotifications() }
        )
    }
}
