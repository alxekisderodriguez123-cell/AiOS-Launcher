package com.aios.launcher.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aios.launcher.data.LauncherStore
import com.aios.launcher.model.AppInfo
import com.aios.launcher.ui.components.AppContextMenu
import com.aios.launcher.ui.components.AppIcon
import com.aios.launcher.ui.components.LiquidDock
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val Ink = Color(0xFF29283D)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    apps: List<AppInfo>,
    onLaunchApp: (String) -> Unit
) {
    val context = LocalContext.current

    val store = remember(context) {
        LauncherStore(context)
    }

    var showLibrary by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    var orderedPackages by remember { mutableStateOf(emptyList<String>()) }
    var dockPackages by remember { mutableStateOf(emptyList<String>()) }
    var hiddenPackages by remember { mutableStateOf(emptySet<String>()) }
    var selectedPackage by remember { mutableStateOf<String?>(null) }

    val appMap = remember(apps) {
        apps.associateBy { it.packageName }
    }

    LaunchedEffect(apps) {
        if (apps.isNotEmpty()) {
            val packages = apps.map { it.packageName }

            orderedPackages = store.orderApps(packages)

            val defaults = listOf(
                "com.android.dialer",
                "com.google.android.dialer",
                "com.google.android.apps.messaging",
                "com.android.messaging",
                "com.android.chrome",
                "com.google.android.camera"
            )

            dockPackages = store.getConfiguredDock(
                installedPackages = packages,
                defaults = defaults
            )

            hiddenPackages = store.getHiddenApps()
                .filter { it in packages }
                .toSet()

            if (store.getDockApps().isEmpty()) {
                store.saveDockApps(dockPackages)
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            now = LocalDateTime.now()
            delay(1000)
        }
    }

    LaunchedEffect(orderedPackages) {
        if (orderedPackages.isNotEmpty()) {
            store.saveAppOrder(orderedPackages)
        }
    }

    LaunchedEffect(dockPackages) {
        store.saveDockApps(dockPackages)
    }

    LaunchedEffect(hiddenPackages) {
        store.saveHiddenApps(hiddenPackages)
    }

    BackHandler(
        enabled = showLibrary || selectedPackage != null
    ) {
        if (selectedPackage != null) {
            selectedPackage = null
        } else {
            showLibrary = false
            query = ""
        }
    }

    val visibleApps = orderedPackages
        .filterNot { it in hiddenPackages }
        .filterNot { it in dockPackages }
        .mapNotNull { appMap[it] }

    val pageSize = 16
    val pageCount = (
        (visibleApps.size.coerceAtLeast(1) + pageSize - 1) / pageSize
    )

    val pagerState = rememberPagerState(
        pageCount = { pageCount }
    )

    val selectedApp = selectedPackage?.let {
        appMap[it]
    }

    val dockApps = dockPackages.mapNotNull {
        appMap[it]
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AiOSWallpaper()

        Crossfade(
            targetState = showLibrary,
            animationSpec = tween(260),
            label = "AiOS screen transition"
        ) { library ->
            if (library) {
                AppLibraryScreen(
                    apps = apps,
                    query = query,
                    onQueryChange = { query = it },
                    onLaunchApp = onLaunchApp,
                    onLongClick = { selectedPackage = it },
                    onClose = {
                        showLibrary = false
                        query = ""
                    }
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(horizontal = 23.dp)
                        .padding(top = 15.dp, bottom = 7.dp)
                ) {
                    // Reloj
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 13.dp, bottom = 23.dp)
                    ) {
                        Text(
                            text = now.format(
                                DateTimeFormatter.ofPattern("HH:mm")
                            ),
                            fontSize = 76.sp,
                            lineHeight = 80.sp,
                            fontWeight = FontWeight.Light,
                            color = Ink,
                            letterSpacing = (-5).sp
                        )

                        Text(
                            text = now.format(
                                DateTimeFormatter.ofPattern(
                                    "EEEE, d 'de' MMMM",
                                    Locale("es", "ES")
                                )
                            ).replaceFirstChar { it.uppercase() },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Ink.copy(alpha = 0.65f)
                        )
                    }

                    // Widget de fecha
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 17.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White.copy(alpha = 0.37f),
                        tonalElevation = 0.dp,
                        shadowElevation = 0.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 19.dp,
                                vertical = 13.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    "CALENDARIO",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    color = Ink.copy(alpha = 0.48f)
                                )

                                Text(
                                    now.format(
                                        DateTimeFormatter.ofPattern(
                                            "MMMM yyyy",
                                            Locale("es", "ES")
                                        )
                                    ).replaceFirstChar { it.uppercase() },
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Ink
                                )
                            }

                            Text(
                                now.dayOfMonth.toString(),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Light,
                                color = Color(0xFF7779C9)
                            )
                        }
                    }

                    // Aplicaciones
                    if (visibleApps.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Tu escritorio está vacío",
                                color = Ink
                            )
                        }
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) { page ->
                            val pageApps = visibleApps
                                .drop(page * pageSize)
                                .take(pageSize)

                            LazyVerticalGrid(
                                columns = GridCells.Fixed(4),
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalArrangement = Arrangement.spacedBy(21.dp),
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
                                        onLongClick = {
                                            selectedPackage = app.packageName
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }

                    // Indicadores
                    if (pageCount > 1) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
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
                                            ) 19.dp else 6.dp,
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
                        Spacer(modifier = Modifier.height(26.dp))
                    }

                    // Dock
                    LiquidDock(
                        apps = dockApps,
                        onLaunch = onLaunchApp,
                        onLongClick = {
                            selectedPackage = it
                        }
                    )

                    IconButton(
                        onClick = {
                            showLibrary = true
                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 4.dp)
                            .size(37.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Apps,
                            contentDescription = "Biblioteca",
                            tint = Ink.copy(alpha = 0.65f),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        if (selectedApp != null) {
            AppContextMenu(
                app = selectedApp,
                isInDock = selectedPackage in dockPackages,
                onDismiss = {
                    selectedPackage = null
                },
                onMoveBackward = {
                    val index = orderedPackages.indexOf(
                        selectedApp.packageName
                    )

                    if (index > 0) {
                        val updated = orderedPackages.toMutableList()
                        val item = updated.removeAt(index)
                        updated.add(index - 1, item)
                        orderedPackages = updated
                    }

                    selectedPackage = null
                },
                onMoveForward = {
                    val index = orderedPackages.indexOf(
                        selectedApp.packageName
                    )

                    if (index >= 0 && index < orderedPackages.lastIndex) {
                        val updated = orderedPackages.toMutableList()
                        val item = updated.removeAt(index)
                        updated.add(index + 1, item)
                        orderedPackages = updated
                    }

                    selectedPackage = null
                },
                onToggleDock = {
                    val updated = dockPackages.toMutableList()

                    if (selectedApp.packageName in updated) {
                        updated.remove(selectedApp.packageName)
                    } else if (updated.size < 4) {
                        updated.add(selectedApp.packageName)
                    }

                    dockPackages = updated
                    selectedPackage = null
                },
                onHide = {
                    hiddenPackages = hiddenPackages +
                        selectedApp.packageName

                    dockPackages = dockPackages -
                        selectedApp.packageName

                    selectedPackage = null
                },
                onInfo = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    ).apply {
                        data = android.net.Uri.parse(
                            "package:${selectedApp.packageName}"
                        )
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    context.startActivity(intent)
                    selectedPackage = null
                }
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
                        Color.White.copy(alpha = 0.82f),
                        Color.Transparent
                    ),
                    center = Offset(
                        size.width * 0.13f,
                        size.height * 0.19f
                    ),
                    radius = size.width * 0.78f
                ),
                radius = size.width * 0.78f,
                center = Offset(
                    size.width * 0.13f,
                    size.height * 0.19f
                )
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE5B9EE).copy(alpha = 0.47f),
                        Color.Transparent
                    ),
                    center = Offset(
                        size.width * 0.94f,
                        size.height * 0.69f
                    ),
                    radius = size.width * 0.68f
                ),
                radius = size.width * 0.68f,
                center = Offset(
                    size.width * 0.94f,
                    size.height * 0.69f
                )
            )
        }
    }
}
