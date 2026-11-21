package com.aios.launcher.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.aios.launcher.data.AppRepository
import com.aios.launcher.model.AppInfo
import com.aios.launcher.ui.screens.HomeScreen
import com.aios.launcher.ui.theme.AiOSTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AiOSApp(
    onSetDefaultLauncher: () -> Unit
) {
    val context = LocalContext.current

    val repository = remember(context) {
        AppRepository(context)
    }

    var apps by remember {
        mutableStateOf(emptyList<AppInfo>())
    }

    LaunchedEffect(repository) {
        apps = withContext(Dispatchers.IO) {
            repository.getInstalledApps()
                .filter { it.packageName != context.packageName }
        }
    }

    AiOSTheme {
        HomeScreen(
            apps = apps,
            onLaunchApp = { packageName ->
                repository.launchApp(packageName)
            }
        )
    }
}
