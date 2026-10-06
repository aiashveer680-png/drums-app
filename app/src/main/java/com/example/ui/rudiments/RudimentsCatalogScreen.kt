package com.example.ui.rudiments

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RudimentCatalog
import com.example.model.Rudiment
import com.example.model.RudimentCategory
import com.example.ui.theme.DrumAmberPrimary
import com.example.ui.theme.DrumCyanSecondary

@Composable
fun RudimentsCatalogScreen(
    onSelectRudiment: (Rudiment) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<RudimentCategory?>(null) }

    val categories = remember { RudimentCategory.entries.toList() }

    val filteredRudiments = remember(searchQuery, selectedCategory) {
        RudimentCatalog.allRudiments.filter { r ->
            val matchesCategory = (selectedCategory == null || r.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    r.name.contains(searchQuery, ignoreCase = true) ||
                    r.alternativeName.contains(searchQuery, ignoreCase = true) ||
                    r.stickingSequence.contains(searchQuery, ignoreCase = true) ||
                    r.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Drum Rudiment Library",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Essential Technical Foundations • PAS Standard",
                    style = MaterialTheme.typography.bodySmall,
                    color = DrumAmberPrimary
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1B2232))
                    .border(1.dp, Color(0xFF28344A), RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${filteredRudiments.size} Rudiments",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search rolls, diddles, flams, sticking...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = DrumAmberPrimary)
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_rudiment_search"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DrumAmberPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Category Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All (22)", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DrumAmberPrimary,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("chip_cat_all")
                )
            }
            items(categories) { cat ->
                val isSelected = (selectedCategory == cat)
                val count = RudimentCatalog.getByCategory(cat).size
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = if (isSelected) null else cat },
                    label = { Text("${cat.displayName} ($count)", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DrumAmberPrimary,
                        selectedLabelColor = Color.Black
                    ),
                    modifier = Modifier.testTag("chip_cat_${cat.name}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Rudiment List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredRudiments, key = { it.id }) { rudiment ->
                RudimentCatalogItemCard(
                    rudiment = rudiment,
                    onClick = { onSelectRudiment(rudiment) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun RudimentCatalogItemCard(
    rudiment: Rudiment,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("rudiment_card_${rudiment.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = rudiment.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1B2433))
                            .border(1.dp, Color(0xFF2B3A52), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = rudiment.difficulty.displayName,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(rudiment.difficulty.colorHex)
                        )
                    }
                }

                if (rudiment.alternativeName.isNotEmpty()) {
                    Text(
                        text = rudiment.alternativeName,
                        fontSize = 11.sp,
                        color = DrumAmberPrimary
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Sticking preview pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF101420))
                        .border(1.dp, Color(0xFF1E283C), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = rudiment.stickingSequence,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = rudiment.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View",
                tint = DrumAmberPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
