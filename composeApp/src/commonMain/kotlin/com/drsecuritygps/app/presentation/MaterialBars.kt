@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.drsecuritygps.app.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow

/**
 * Simple data for top bar actions.
 */
@Immutable
data class MaterialBarAction(
    val icon: ImageVector,
    val contentDescription: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * Simple data for bottom navigation items.
 */
@Immutable
data class MaterialNavigationItem(
    val label: String,
    val icon: ImageVector,
    val selected: Boolean,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val badgeCount: Int? = null,
)

/**
 * Material 3 top app bar with centered title and optional subtitle.
 *
 * Use this for screen headers that need a back button and a short status line.
 */
@Composable
fun DrSecurityTopBar(
    title: String,
    subtitle: String? = null,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    onNavigationClick: (() -> Unit)? = null,
    actions: List<MaterialBarAction> = emptyList(),
    navigationContent: (@Composable (() -> Unit))? = null,
    actionsContent: (@Composable RowScope.() -> Unit)? = null,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    titleContentColor: Color = MaterialTheme.colorScheme.onSurface,
    subtitleContentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    navigationIconContentColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Column {
                Text(
                    text = title,
                    color = titleContentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                subtitle?.takeIf { it.isNotBlank() }?.let {
                    Text(
                        text = it,
                        color = subtitleContentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        navigationIcon = {
            when {
                navigationContent != null -> navigationContent()
                onNavigationClick != null -> {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = "Navigate back",
                        tint = navigationIconContentColor,
                    )
                }
            }
            }
        },
        actions = {
            if (actionsContent != null) {
                actionsContent()
            } else {
                actions.forEach { action ->
                    IconButton(
                        onClick = action.onClick,
                        enabled = action.enabled,
                    ) {
                        Icon(
                            imageVector = action.icon,
                            contentDescription = action.contentDescription,
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            titleContentColor = titleContentColor,
            navigationIconContentColor = navigationIconContentColor,
            actionIconContentColor = titleContentColor,
        ),
    )
}

/**
 * Material 3 bottom navigation bar.
 */
@Composable
fun DrSecurityNavigationBar(
    items: List<MaterialNavigationItem>,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    selectedContentColor: Color = MaterialTheme.colorScheme.primary,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = containerColor,
        contentColor = contentColor,
    ) {
        items.forEach { item ->
            DrSecurityNavigationBarItem(
                item = item,
                selectedContentColor = selectedContentColor,
                unselectedContentColor = contentColor,
            )
        }
    }
}

@Composable
private fun RowScope.DrSecurityNavigationBarItem(
    item: MaterialNavigationItem,
    selectedContentColor: Color,
    unselectedContentColor: Color,
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = selectedContentColor,
        selectedTextColor = selectedContentColor,
        indicatorColor = selectedContentColor.copy(alpha = 0.20f),
        unselectedIconColor = unselectedContentColor,
        unselectedTextColor = unselectedContentColor,
    )

    NavigationBarItem(
        selected = item.selected,
        onClick = item.onClick,
        enabled = item.enabled,
        icon = {
            if (item.badgeCount != null && item.badgeCount > 0) {
                BadgedBox(
                    badge = {
                        Text(
                            text = if (item.badgeCount > 99) "99+" else item.badgeCount.toString(),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                ) {
                    Icon(imageVector = item.icon, contentDescription = item.label)
                }
            } else {
                Icon(imageVector = item.icon, contentDescription = item.label)
            }
        },
        label = {
            Text(
                text = item.label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        colors = itemColors,
    )
}
