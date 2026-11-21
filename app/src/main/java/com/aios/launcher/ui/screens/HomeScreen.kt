package com.aios.launcher.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aios.launcher.model.AppInfo
import com.aios.launcher.ui.components.AppIcon
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val AiOSInk = Color(0xFF29283D)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onLaunchApp: (String) -> Unit
) {
    var showLibrary by remember { mutableStateOf(false) }
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1000)
        }
    }

    BackHandler(enabled = showLibrary) {
        showLibrary = false
    }

    val pageSize = 16
    val pageCount = ((apps.size.coerceAtLeast(1) + pageSize - 1) / pageSize)
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val dockApps = remember(apps) {
        val groups = listOf(
            listOf("teléfono", "phone", "dialer"),
            listOf("mensajes", "messages", "sms"),
            listOf("chrome", "browser", "navegador"),
            listOf("cámara", "camera")
        )

        val selected = groups.mapNotNull { keywords ->
            apps.firstOrNull { app ->
                keywords.any { keyword ->
                    app.name.contains(keyword, true) ||
                        app.packageName.contains(keyword, true)
                }
            }
        }.distinctBy { it.packageName }

        (selected + apps.filterNot { app ->
            selected.any { it.packageName == app.packageName }
        }).take(4)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AiOSWallpaper()

        if (!showLibrary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp)
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                // Reloj y fecha
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 18.dp)
                ) {
                    Text(
                        text = now.format(
                            DateTimeFormatter.ofPattern("HH:mm")
                        ),
                        fontSize = 66.sp,
                        lineHeight = 70.sp,
                        fontWeight = FontWeight.Light,
                        color = AiOSInk,
                        letterSpacing = (-3).sp
                    )

                    Text(
                        text = now.format(
                            DateTimeFormatter.ofPattern(
                                "EEEE, d 'de' MMMM",
                                Locale("es", "ES")
                            )
                        ).replaceFirstChar { it.uppercase() },
                        fontSize = 15.sp,
                        color = AiOSInk.copy(alpha = 0.76f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Widget de calendario
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    shape = RoundedCornerShape(26.dp),
                    color = Color.White.copy(alpha = 0.43f),
                    shadowElevation = 5.dp
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 20.dp,
                            vertical = 15.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "HOY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AiOSInk.copy(alpha = 0.55f),
                                letterSpacing = 2.sp
                            )
                            Text(
                                now.format(
                                    DateTimeFormatter.ofPattern(
                                        "d 'de' MMMM",
                                        Locale("es", "ES")
                                    )
                                ),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AiOSInk
                            )
                        }

                        Text(
                            now.dayOfMonth.toString(),
                            fontSize = 39.sp,
                            fontWeight = FontWeight.Light,
                            color = Color(0xFF7779C9)
                        )
                    }
                }

                // Escritorio de aplicaciones
                if (apps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay aplicaciones disponibles",
                            color = AiOSInk
                        )
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) { page ->
                        val pageApps = apps
                            .drop(page * pageSize)
                            .take(pageSize)

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalArrangement = Arrangement.spacedBy(19.dp),
                            contentPadding = PaddingValues(
                                top = 8.dp,
                                bottom = 12.dp
                            ),
                            userScrollEnabled = false
                        ) {
                            items(
                                items = pageApps,
                                key = { it.packageName }
                            ) { app ->
                                AppIcon(
                                    app = app,
                                    onClick = {
                                        onLaunchApp(app.packageName)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                // Indicadores de páginas
                if (pageCount > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 9.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(pageCount) { index ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(
                                        width = if (
                                            pagerState.currentPage == index
                                        ) 17.dp else 6.dp,
                                        height = 6.dp
                                    )
                                    .background(
                                        if (pagerState.currentPage == index)
                                            Color.White
                                        else Color.White.copy(alpha = 0.48f),
                                        CircleShape
                                    )
                            )
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.height(21.dp))
                }

                // Dock
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 5.dp, bottom = 10.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(30.dp)
                        ),
                    shape = RoundedCornerShape(30.dp),
                    color = Color.White.copy(alpha = 0.48f),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 9.dp,
                                vertical = 12.dp
                            ),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        dockApps.forEach { app ->
                            AppIcon(
                                app = app,
                                onClick = {
                                    onLaunchApp(app.packageName)
                                },
                                showLabel = false,
                                iconSize = 51.dp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Acceso a la biblioteca
                IconButton(
                    onClick = { showLibrary = true },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Apps,
                        contentDescription = "Biblioteca",
                        tint = AiOSInk.copy(alpha = 0.72f),
                        modifier = Modifier.size(23.dp)
                    )
                }
            }
        } else {
            AppLibraryScreen(
                apps = apps,
                onLaunchApp = onLaunchApp,
                onClose = { showLibrary = false }
            )
        }
    }
}

@Composable
private fun AiOSWallpaper() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFE8E7FA),
                        Color(0xFFDCDCF5),
                        Color(0xFFF4EAF7),
                        Color(0xFFE7EDF9)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.78f),
                        Color(0xFFB8B9F1).copy(alpha = 0.0f)
                    ),
                    center = Offset(size.width * 0.15f, size.height * 0.20f),
                    radius = size.width * 0.75f
                ),
                radius = size.width * 0.75f,
                center = Offset(size.width * 0.15f, size.height * 0.20f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE9BFEF).copy(alpha = 0.55f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.92f, size.height * 0.65f),
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = Offset(size.width * 0.92f, size.height * 0.65f)
            )
        }
    }
}

@Composable
private fun AppLibraryScreen(
    apps: List<AppInfo>,
    onLaunchApp: (String) -> Unit,
    onClose: () -> Unit
) {
    var query by remember { mutableStateOf("") }

    val filteredApps = remember(apps, query) {
        apps.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0EFF8))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Biblioteca",
                modifier = Modifier.weight(1f),
                fontSize = 29.sp,
                fontWeight = FontWeight.Bold,
                color = AiOSInk
            )

            IconButton(onClick = onClose) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = "Cerrar",
                    tint = AiOSInk
                )
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Buscar") },
            leadingIcon = {
                Icon(
                    Icons.Rounded.Search,
                    contentDescription = "Buscar"
                )
            },
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White.copy(alpha = 0.75f),
                focusedContainerColor = Color.White,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color(0xFF9694E9)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (filteredApps.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No se encontraron aplicaciones",
                    color = AiOSInk.copy(alpha = 0.65f)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(22.dp),
                contentPadding = PaddingValues(bottom = 30.dp)
            ) {
                items(
                    items = filteredApps,
                    key = { it.packageName }
                ) { app ->
                    AppIcon(
                        app = app,
                        onClick = { onLaunchApp(app.packageName) },
                        modifier = Modifier.fillMaxWidth(),
                        labelColor = AiOSInk
                    )
                }
            }
        }
    }
}
