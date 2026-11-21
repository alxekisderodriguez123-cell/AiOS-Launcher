package com.aios.launcher.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Dock
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aios.launcher.model.AppInfo

@Composable
fun AppContextMenu(
    app: AppInfo,
    isInDock: Boolean,
    onDismiss: () -> Unit,
    onMoveBackward: () -> Unit,
    onMoveForward: () -> Unit,
    onToggleDock: () -> Unit,
    onHide: () -> Unit,
    onInfo: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(app.name)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                MenuAction(
                    icon = {
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    },
                    text = "Mover antes",
                    onClick = onMoveBackward
                )

                MenuAction(
                    icon = {
                        Icon(
                            Icons.Rounded.ArrowForward,
                            contentDescription = null
                        )
                    },
                    text = "Mover después",
                    onClick = onMoveForward
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth()
                )

                MenuAction(
                    icon = {
                        Icon(
                            Icons.Rounded.Dock,
                            contentDescription = null
                        )
                    },
                    text = if (isInDock) {
                        "Quitar del dock"
                    } else {
                        "Añadir al dock"
                    },
                    onClick = onToggleDock
                )

                MenuAction(
                    icon = {
                        Icon(
                            Icons.Rounded.VisibilityOff,
                            contentDescription = null
                        )
                    },
                    text = "Ocultar aplicación",
                    onClick = onHide
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth()
                )

                MenuAction(
                    icon = {
                        Icon(
                            Icons.Rounded.Info,
                            contentDescription = null
                        )
                    },
                    text = "Información de aplicación",
                    onClick = onInfo
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Icon(
                    Icons.Rounded.Close,
                    contentDescription = null
                )
                Text("Cerrar")
            }
        }
    )
}

@Composable
private fun MenuAction(
    icon: @Composable () -> Unit,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        icon()
        Text(
            text = text,
            modifier = Modifier.weight(1f)
        )
    }
}
