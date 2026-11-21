
package com.aios.launcher.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.BackHandler
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aios.launcher.data.LauncherStore
import com.aios.launcher.model.AppInfo
import com.aios.launcher.ui.components.AppContextMenu
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
    val context = LocalContext.current

    val store = remember(context) {
        LauncherStore(context)
    }

    var showLibrary by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var now by remember { mutableStateOf(LocalDateTime.now()) }

    var orderedPackages by remember {
        mutableStateOf(emptyList<String>())
    }

    var dockPackages by remember {
        mutableStateOf(emptyList<String>())
    }

    var hiddenPackages by remember {
        mutableStateOf(emptySet<String>())
    }

    var selectedPackage by remember {
        mutableStateOf<String?>(null)
    }

    val appMap = remember(apps) {
        apps.associateBy { it.packageName }
    }

    LaunchedEffect(apps) {
        if (apps.isNotEmpty()) {
            val packageNames = apps.map { it.packageName }

            orderedPackages = store.orderApps(packageNames)

            val defaults = listOf(
                "com.android.dialer",
                "com.google.android.dialer",
                "com.google.android.apps.messaging",
                "com.android.messaging",
                "com.android.chrome",
                "com.google.android.camera"
            )

            dockPackages = store.getConfiguredDock(
                installedPackages = packageNames,
                defaults = defaults
            )

            hiddenPackages = store.getHiddenApps()
                .filter { it in packageNames }
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

    BackHandler(enabled = showLibrary || selectedPackage != null) {
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

        if (!showLibrary) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp)
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
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

                if (visibleApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tu escritorio está vacío",
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
                        val pageApps = visibleApps
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
                                    onLongClick = {
                                        selectedPackage = app.packageName
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

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
                        repeat(4) { index ->
                            val app = dockApps.getOrNull(index)

                            if (app != null) {
                                AppIcon(
                                    app = app,
                                    onClick = {
                                        onLaunchApp(app.packageName)
                                    },
                                    onLongClick = {
                                        selectedPackage = app.packageName
                                    },
                                    showLabel = false,
                                    iconSize = 51.dp,
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = {
                        showLibrary = true
                    },
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
                hiddenPackages = hiddenPackages,
                query = query,
                onQueryChange = { query = it },
                onLaunchApp = onLaunchApp,
                onLongClick = { selectedPackage = it },
                onClose = {
                    showLibrary = false
                    query = ""
                }
            )
        }

        if (selectedApp != null) {
            AppContextMenu(
                app = selectedApp,
                isInDock = selectedPackage in dockPackages,
                onDismiss = {
                    selectedPackage = null
                },
                onMoveBackward = {
                    val index = orderedPackages.indexOf(selectedApp.packageName)

                    if (index > 0) {
                        val updated = orderedPackages.toMutableList()
                        val item = updated.removeAt(index)
                        updated.add(index - 1, item)
                        orderedPackages = updated
                    }

                    selectedPackage = null
                },
                onMoveForward = {
                    val index = orderedPackages.indexOf(selectedApp.packageName)

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
                        Color.White.copy(alpha = 0.78f),
                        Color.Transparent
                    ),
                    center = Offset(
                        size.width * 0.15f,
                        size.height * 0.20f
                    ),
                    radius = size.width * 0.75f
                ),
                radius = size.width * 0.75f,
                center = Offset(
                    size.width * 0.15f,
                    size.height * 0.20f
                )
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE9BFEF).copy(alpha = 0.55f),
                        Color.Transparent
                    ),
                    center = Offset(
                        size.width * 0.92f,
                        size.height * 0.65f
                    ),
                    radius = size.width * 0.65f
                ),
                radius = size.width * 0.65f,
                center = Offset(
                    size.width * 0.92f,
                    size.height * 0.65f
                )
            )
        }
    }
}

@Composable
private fun AppLibraryScreen(
    apps: List<AppInfo>,
    hiddenPackages: Set<String>,
    query: String,
    onQueryChange: (String) -> Unit,
    onLaunchApp: (String) -> Unit,
    onLongClick: (String) -> Unit,
    onClose: () -> Unit
) {
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
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            placeholder = { Text("Buscar aplicaciones") },
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

        Spacer(modifier = Modifier.height(12.dp))

        if (hiddenPackages.isNotEmpty()) {
            Text(
                text = "${hiddenPackages.size} aplicaciones ocultas",
                color = AiOSInk.copy(alpha = 0.65f),
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

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
                        onClick = {
                            onLaunchApp(app.packageName)
                        },
                        onLongClick = {
                            onLongClick(app.packageName)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        labelColor = AiOSInk
                    )
                }
            }
        }
    }
}
