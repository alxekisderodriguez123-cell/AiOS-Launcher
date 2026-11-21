package com.aios.launcher.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.aios.launcher.model.AppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    iconSize: Dp = 57.dp,
    labelColor: Color = Color.White,
    onLongClick: (() -> Unit)? = null
) {
    val iconBitmap = remember(app.packageName) {
        app.icon.toBitmap(
            width = 192,
            height = 192
        ).asImageBitmap()
    }

    Column(
        modifier = modifier
            .width(76.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.name,
            modifier = Modifier.size(iconSize),
            contentScale = ContentScale.Fit
        )

        if (showLabel) {
            Text(
                text = app.name,
                fontSize = 11.sp,
                lineHeight = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = labelColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
