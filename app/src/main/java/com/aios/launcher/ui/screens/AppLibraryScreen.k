
package com.aios.launcher.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aios.launcher.model.AppInfo
import com.aios.launcher.ui.components.AppIcon

private val LibraryInk = Color(0xFF29283D)

@Composable
fun AppLibraryScreen(
    apps: List<AppInfo>,
    query: String,
    onQueryChange: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onLongClick: (String) -> Unit,
    onClose: () -> Unit
) {
    val filteredApps = remember(apps, query) {
        apps.filter {
            it.name.contains(query, ignoreCase = true)
        }.sortedBy {
            it.name.lowercase()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFF5F3FC),
                        Color(0xFFE8E7F7),
                        Color(0xFFF1EAF5)
                    )
                )
            )
            .padding(horizontal = 22.dp)
            .padding(top = 18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    "APLICACIONES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.4.sp,
                    color = LibraryInk.copy(alpha = 0.52f)
                )

                Text(
                    "Biblioteca",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LibraryInk,
                    letterSpacing = (-1).sp
                )
            }

            IconButton(onClick = onClose) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Cerrar biblioteca",
                    tint = LibraryInk
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = {
                Text("Buscar aplicaciones")
            },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = null
                )
            },
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White.copy(alpha = 0.63f),
                focusedContainerColor = Color.White.copy(alpha = 0.88f),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF9993DF),
                focusedTextColor = LibraryInk,
                unfocusedTextColor = LibraryInk
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "${filteredApps.size} aplicaciones",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = LibraryInk.copy(alpha = 0.55f),
            modifier = Modifier.padding(bottom = 15.dp)
        )

        if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No se encontraron aplicaciones",
                    color = LibraryInk.copy(alpha = 0.65f)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(23.dp),
                contentPadding = PaddingValues(bottom = 35.dp)
            ) {
                items(
                    items = filteredApps,
                    key = { it.packageName }
                ) { app ->
                    AppIcon(
                        app = app,
                        onClick = {
                            onLaunchApp(app.packageName)
                        },
                        onLongClick = {
                            onLongClick(app.packageName)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        labelColor = LibraryInk,
                        iconSize = 53.dp
                    )
                }
            }
        }
    }
}
