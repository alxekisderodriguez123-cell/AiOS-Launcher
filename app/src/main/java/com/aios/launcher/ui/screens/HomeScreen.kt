package com.aios.launcher.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aios.launcher.model.AppInfo
import com.aios.launcher.ui.components.AppIcon

@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    query: String,
    showAllApps: Boolean,
    onQueryChange: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onToggleDrawer: () -> Unit,
    onSetDefaultLauncher: () -> Unit
) {
    val filteredApps = apps.filter {
        it.name.contains(query, ignoreCase = true)
    }

    val visibleApps = if (showAllApps || query.isNotBlank()) {
        filteredApps
    } else {
        filteredApps.take(12)
    }

    val dockApps = apps.take(4)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F7FC),
                        Color(0xFFEDEBFA),
                        Color(0xFFF4F2F9)
                    )
                )
            )
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(34.dp))

            Text(
                text = "AiOS",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF29283D)
            )

            Text(
                text = "A new vision of Android experience.",
                fontSize = 12.sp,
                color = Color(0xFF77758E)
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                        imageVector = Icons.Rounded.Search,
                        contentDescription = "Buscar"
                    )
                },
                shape = RoundedCornerShape(22.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White.copy(alpha = 0.58f),
                    focusedContainerColor = Color.White.copy(alpha = 0.78f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
                    focusedBorderColor = Color(0xFF9694E9)
                )
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (showAllApps) "Todas las aplicaciones" else "Aplicaciones",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF29283D),
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onToggleDrawer) {
                    Icon(
                        imageVector = Icons.Rounded.Apps,
                        contentDescription = "Abrir cajón de aplicaciones",
                        tint = Color(0xFF6968A9)
                    )
                }
            }

            if (apps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Todavía no hay aplicaciones disponibles.")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Revisa los permisos y vuelve a abrir AiOS.",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else if (visibleApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No se encontraron aplicaciones.")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalArrangement = Arrangement.spacedBy(22.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        top = 8.dp,
                        bottom = 18.dp
                    )
                ) {
                    items(
                        items = visibleApps,
                        key = { it.packageName }
                    ) { app ->
                        AppIcon(
                            app = app,
                            onClick = { onLaunchApp(app.packageName) },
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(30.dp),
                color = Color.White.copy(alpha = 0.55f),
                shadowElevation = 8.dp,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (dockApps.isEmpty()) {
                        Text("Tu dock aparecerá aquí")
                    } else {
                        dockApps.forEach { app ->
                            AppIcon(
                                app = app,
                                onClick = { onLaunchApp(app.packageName) },
                                modifier = Modifier.size(width = 70.dp, height = 76.dp)
                            )
                        }
                    }
                }
            }

            Text(
                text = "Mantén AiOS como pantalla de inicio",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                fontSize = 11.sp,
                color = Color(0xFF77758E),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            androidx.compose.material3.TextButton(
                onClick = onSetDefaultLauncher,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 8.dp)
            ) {
                Text("Establecer AiOS como launcher predeterminado")
            }
        }
    }
}
