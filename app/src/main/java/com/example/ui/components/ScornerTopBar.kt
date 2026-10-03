package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.example.auth.AuthManager
import com.example.auth.UserRole
import com.example.localization.AppLanguage
import com.example.localization.LanguageManager
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScornerTopBar(
    unreadNotificationCount: Int,
    onOpenNotifications: () -> Unit,
    onSyncClick: () -> Unit,
    onOpenMfa: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showLanguageMenu by remember { mutableStateOf(false) }
    var showRoleMenu by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            // Main Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // App Logo & Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "S",
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SCORNER",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = PrimaryBlue
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            // Offline / Online Indicator Chip
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (AuthManager.isOfflineMode) Color(0xFFFDE047) else SuccessGreen.copy(alpha = 0.15f),
                                modifier = Modifier
                                    .clickable {
                                        AuthManager.isOfflineMode = !AuthManager.isOfflineMode
                                    }
                                    .testTag("offline_toggle_chip")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (AuthManager.isOfflineMode) WarningOrange else SuccessGreen)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (AuthManager.isOfflineMode) "OFFLINE" else "ONLINE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (AuthManager.isOfflineMode) Slate900 else SuccessGreen
                                    )
                                }
                            }
                        }

                        // Role badge dropdown trigger
                        Box {
                            Row(
                                modifier = Modifier
                                    .clickable { showRoleMenu = true }
                                    .testTag("role_switcher_trigger"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = AuthManager.currentUser.role.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Switch Role",
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            DropdownMenu(
                                expanded = showRoleMenu,
                                onDismissRequest = { showRoleMenu = false }
                            ) {
                                UserRole.values().forEach { role ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    when (role) {
                                                        UserRole.STUDENT -> Icons.Filled.School
                                                        UserRole.TEACHER -> Icons.Filled.Person
                                                        UserRole.ADMINISTRATOR -> Icons.Filled.AdminPanelSettings
                                                    },
                                                    contentDescription = null,
                                                    modifier = Modifier.size(18.dp),
                                                    tint = PrimaryBlue
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(role.title)
                                            }
                                        },
                                        onClick = {
                                            AuthManager.switchRole(role)
                                            showRoleMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Action buttons: Sync, MFA, Language, Notifications
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Sync Button
                    IconButton(
                        onClick = onSyncClick,
                        modifier = Modifier.testTag("topbar_sync_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Sync,
                            contentDescription = "Sync Data",
                            tint = if (AuthManager.isSyncing) TertiaryAmber else PrimaryBlue
                        )
                    }

                    // MFA Security Button
                    IconButton(
                        onClick = onOpenMfa,
                        modifier = Modifier.testTag("topbar_mfa_button")
                    ) {
                        Icon(
                            imageVector = if (AuthManager.currentUser.isMfaVerified) Icons.Filled.VerifiedUser else Icons.Filled.Security,
                            contentDescription = "MFA Security",
                            tint = if (AuthManager.currentUser.isMfaVerified) SuccessGreen else WarningOrange
                        )
                    }

                    // Language Selector
                    Box {
                        IconButton(
                            onClick = { showLanguageMenu = true },
                            modifier = Modifier.testTag("topbar_language_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Translate,
                                contentDescription = "Language",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        DropdownMenu(
                            expanded = showLanguageMenu,
                            onDismissRequest = { showLanguageMenu = false }
                        ) {
                            AppLanguage.values().forEach { lang ->
                                DropdownMenuItem(
                                    text = {
                                        Text("${lang.nativeName} (${lang.displayName})")
                                    },
                                    onClick = {
                                        LanguageManager.currentLanguage = lang
                                        showLanguageMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Notifications Bell
                    IconButton(
                        onClick = onOpenNotifications,
                        modifier = Modifier.testTag("topbar_notifications_button")
                    ) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationCount > 0) {
                                    Badge(containerColor = ErrorRed) {
                                        Text("$unreadNotificationCount")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            HorizontalDivider(color = Slate200.copy(alpha = 0.6f))
        }
    }
}
