
package com.aios.launcher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aios.launcher.model.AppInfo

@Composable
fun LiquidDock(
    apps: List<AppInfo>,
    onLaunch: (String) -> Unit,
    onLongClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 18.dp,
                shape = RoundedCornerShape(31.dp)
            ),
        shape = RoundedCornerShape(31.dp),
        color = Color.White.copy(alpha = 0.27f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.12f),
                                Color(0xFFD8D5F2).copy(alpha = 0.22f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 13.dp,
                        vertical = 13.dp
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val app = apps.getOrNull(index)

                    if (app != null) {
                        AppIcon(
                            app = app,
                            onClick = {
                                onLaunch(app.packageName)
                            },
                            onLongClick = {
                                onLongClick(app.packageName)
                            },
                            showLabel = false,
                            iconSize = 51.dp,
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .size(51.dp)
                        )
                    }
                }
            }
        }
    }
}
