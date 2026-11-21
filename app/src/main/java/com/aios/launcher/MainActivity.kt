package com.aios.launcher

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.aios.launcher.ui.AiOSApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.rgb(242, 241, 248)
        window.navigationBarColor = android.graphics.Color.rgb(242, 241, 248)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.decorView.systemUiVisibility =
                android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                android.view.View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }

        setContent {
            AiOSApp(
                onSetDefaultLauncher = {
                    requestDefaultLauncher()
                }
            )
        }
    }

    private fun requestDefaultLauncher() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)

            if (roleManager != null &&
                roleManager.isRoleAvailable(RoleManager.ROLE_HOME)
            ) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
                    startActivity(
                        roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                    )
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
        }
    }
}
