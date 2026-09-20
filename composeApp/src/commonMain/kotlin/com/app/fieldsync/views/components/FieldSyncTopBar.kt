package com.app.fieldsync.views.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fieldsync.composeapp.generated.resources.Res
import fieldsync.composeapp.generated.resources.fieldSync
import fieldsync.composeapp.generated.resources.fs_Logo
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldSyncTopBar(
    showActions: Boolean,
    onToggleActions: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            AnimatedContent(
                targetState = showActions, transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                        animationSpec = tween(
                            220
                        )
                    )
                }, label = "logo_transition"
            ) { expanded ->
                if (expanded) {
                    Image(
                        painter = painterResource(Res.drawable.fs_Logo),
                        contentDescription = "FieldSync Logo",
                        modifier = Modifier.size(32.dp).clickable { onToggleActions() })
                } else {
                    Box(
                        modifier = Modifier.size(54.dp).clip(CircleShape).border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.15f),
                            shape = CircleShape
                        ).background(Color.Black, CircleShape).clickable { onToggleActions() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(Res.drawable.fieldSync),
                            contentDescription = "FieldSync Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }, actions = {
            AnimatedVisibility(
                visible = showActions, enter = slideInHorizontally(
                    initialOffsetX = { it / 2 }) + fadeIn(), exit = slideOutHorizontally(
                    targetOffsetX = { it / 2 }) + fadeOut()
            ) {
                Row {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            }
        }, colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Black,
            scrolledContainerColor = Color.Unspecified,
            navigationIconContentColor = Color.Unspecified,
            titleContentColor = Color.White,
            actionIconContentColor = Color.Unspecified
        )
    )
}