package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.CourseMaterialEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScornerViewModel

@Composable
fun SearchAndMaterialsScreen(
    viewModel: ScornerViewModel,
    materials: List<CourseMaterialEntity>,
    searchQuery: String,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Lecture Notes", "Question Bank", "Cheat Sheet", "Lab Manual", "Textbook")

    val filteredMaterials = remember(materials, selectedCategory) {
        if (selectedCategory == "All") materials
        else materials.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search Input Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                label = { Text("Search courses, lecture notes, syllabus, board papers...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null, tint = PrimaryBlue) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_materials_input")
            )
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat) }
                    )
                }
            }
        }

        // Count header
        item {
            Text(
                text = "Course Resources Found (${filteredMaterials.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (filteredMaterials.isEmpty()) {
            item {
                EmptyStateCard(message = "No course materials matched '$searchQuery'. Try searching for 'Math', 'Physics', 'Chemistry', or 'Board'.")
            }
        } else {
            items(filteredMaterials) { item ->
                CourseMaterialCard(
                    material = item,
                    onDownload = { viewModel.simulateDownloadMaterial(item.id, item.title) }
                )
            }
        }
    }
}

@Composable
fun CourseMaterialCard(
    material: CourseMaterialEntity,
    onDownload: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = when (material.fileType) {
                    "PDF" -> ErrorRed.copy(alpha = 0.15f)
                    "DOCX" -> PrimaryBlue.copy(alpha = 0.15f)
                    else -> TertiaryAmber.copy(alpha = 0.15f)
                },
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = material.fileType,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = when (material.fileType) {
                            "PDF" -> ErrorRed
                            "DOCX" -> PrimaryBlue
                            else -> TertiaryAmber
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = PrimaryBlue.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = material.courseCode,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = PrimaryBlue,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = material.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = material.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "${material.instructor} • ${material.fileSize} • ${material.downloadsCount} downloads",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDownload,
                modifier = Modifier.testTag("download_material_${material.id}")
            ) {
                Icon(Icons.Filled.Download, contentDescription = "Download", tint = PrimaryBlue)
            }
        }
    }
}
