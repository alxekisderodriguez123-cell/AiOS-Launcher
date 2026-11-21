
package com.aios.launcher.data

import android.content.Context

class LauncherStore(context: Context) {

    private val preferences = context.getSharedPreferences(
        "aios_launcher_preferences",
        Context.MODE_PRIVATE
    )

    fun getAppOrder(): List<String> {
        return preferences.getString("desktop_order", null)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    fun saveAppOrder(packages: List<String>) {
        preferences.edit()
            .putString("desktop_order", packages.joinToString("|"))
            .apply()
    }

    fun getDockApps(): List<String> {
        return preferences.getString("dock_apps", null)
            ?.split("|")
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

    fun saveDockApps(packages: List<String>) {
        preferences.edit()
            .putString("dock_apps", packages.joinToString("|"))
            .apply()
    }

    fun getHiddenApps(): Set<String> {
        return preferences.getStringSet(
            "hidden_apps",
            emptySet()
        )?.toSet() ?: emptySet()
    }

    fun saveHiddenApps(packages: Set<String>) {
        preferences.edit()
            .putStringSet("hidden_apps", packages.toSet())
            .apply()
    }

    fun orderApps(
        installedPackages: List<String>
    ): List<String> {
        val savedOrder = getAppOrder()
        val installed = installedPackages.toSet()

        return savedOrder.filter { it in installed } +
            installedPackages.filterNot { it in savedOrder }
    }

    fun getConfiguredDock(
        installedPackages: List<String>,
        defaults: List<String>
    ): List<String> {
        val saved = getDockApps()

        val source = if (saved.isEmpty()) defaults else saved

        return source
            .filter { it in installedPackages }
            .distinct()
            .take(4)
    }
}
