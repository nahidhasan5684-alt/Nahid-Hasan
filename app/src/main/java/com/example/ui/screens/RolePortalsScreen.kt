package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.AuthManager
import com.example.auth.UserRole
import com.example.data.model.StudentEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScornerViewModel

@Composable
fun RolePortalsScreen(
    viewModel: ScornerViewModel,
    student: StudentEntity?,
    modifier: Modifier = Modifier
) {
    val activeRole = AuthManager.currentUser.role

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Active Role Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ACTIVE ACCESS PORTAL",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = activeRole.badge,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldYellow
                        ) {
                            Text(
                                text = "Verified Session",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Slate900,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Logged in as ${AuthManager.currentUser.name} (${AuthManager.currentUser.email})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }

        // Role Switcher Cards
        item {
            Text(
                text = "Switch Academic Role",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                RoleSelectionCard(
                    role = UserRole.STUDENT,
                    isSelected = activeRole == UserRole.STUDENT,
                    onSelect = { AuthManager.switchRole(UserRole.STUDENT) },
                    modifier = Modifier.weight(1f)
                )
                RoleSelectionCard(
                    role = UserRole.TEACHER,
                    isSelected = activeRole == UserRole.TEACHER,
                    onSelect = { AuthManager.switchRole(UserRole.TEACHER) },
                    modifier = Modifier.weight(1f)
                )
                RoleSelectionCard(
                    role = UserRole.ADMINISTRATOR,
                    isSelected = activeRole == UserRole.ADMINISTRATOR,
                    onSelect = { AuthManager.switchRole(UserRole.ADMINISTRATOR) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Role-Specific Action Controls
        item {
            when (activeRole) {
                UserRole.TEACHER -> {
                    TeacherPortalActions(viewModel = viewModel)
                }
                UserRole.ADMINISTRATOR -> {
                    AdminPortalActions(viewModel = viewModel)
                }
                UserRole.STUDENT -> {
                    StudentPortalActions(viewModel = viewModel, student = student)
                }
            }
        }
    }
}

@Composable
fun RoleSelectionCard(
    role: UserRole,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSelect() }
            .testTag("switch_to_${role.name}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = when (role) {
                    UserRole.STUDENT -> Icons.Filled.School
                    UserRole.TEACHER -> Icons.Filled.Person
                    UserRole.ADMINISTRATOR -> Icons.Filled.AdminPanelSettings
                },
                contentDescription = null,
                tint = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = when (role) {
                    UserRole.STUDENT -> "Student"
                    UserRole.TEACHER -> "Teacher"
                    UserRole.ADMINISTRATOR -> "Admin"
                },
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp,
                color = if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TeacherPortalActions(viewModel: ScornerViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Faculty Academic Controls",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Post student grades, announce assignments, and schedule deadlines.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.showAddTranscriptDialog.value = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.PostAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Record Student Course Marks & GPA")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.showAddDeadlineDialog.value = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.AlarmAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Broadcast Assignment / Exam Alert")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.showAddCertificateDialog.value = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Verify Extracurricular Award")
            }
        }
    }
}

@Composable
fun AdminPortalActions(viewModel: ScornerViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Administrator Management Console",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Register students, broadcast notices, and trigger cloud sync.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.showAddStudentDialog.value = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register New Student Profile")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    viewModel.broadcastAnnouncement(
                        "Institution Notice",
                        "Campus portal updated with 2026-2027 curriculum calendar."
                    )
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TertiaryAmber),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Campaign, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send School-Wide Notification")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { viewModel.triggerDatabaseSync() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.CloudSync, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sync Local Database with Cloud")
            }
        }
    }
}

@Composable
fun StudentPortalActions(viewModel: ScornerViewModel, student: StudentEntity?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Student Personal Hub",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Review your academic standings, pay tuition, and share profile.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.showEditStudentDialog.value = true },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Update Contact & Address")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = {
                    val share = "🎓 Student Academic Portfolio:\nName: ${student?.name}\nClass: ${student?.className}\nCollege: ${student?.college}\nSSC GPA: ${student?.sscGpa} (Golden)\nVerified by SCORNER Institutional Portal."
                    viewModel.shareTargetText.value = share
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Filled.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share Verified Portfolio")
            }
        }
    }
}
