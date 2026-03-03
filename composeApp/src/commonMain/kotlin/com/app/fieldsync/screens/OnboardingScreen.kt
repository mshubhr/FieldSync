package com.app.fieldsync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.MobileFriendly
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.fieldsync.model.OnboardingPage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(onOnboardingFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "Welcome to FieldSync",
            description = "The ultimate tool for field data collection and synchronization.",
            icon = Icons.Default.MobileFriendly,
            color = Color(0xFF2196F3)
        ), OnboardingPage(
            title = "Capture Everything",
            description = "Easily capture images and data directly from the field with our intuitive interface.",
            icon = Icons.Default.PhotoCamera,
            color = Color(0xFF00BCD4)
        ), OnboardingPage(
            title = "Real-time Sync",
            description = "Keep your team updated with instant synchronization to the central server.",
            icon = Icons.Default.CloudSync,
            color = Color(0xFF4CAF50)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        HorizontalPager(
            state = pagerState, modifier = Modifier.weight(1f).fillMaxWidth()
        ) { position ->
            val page = pages[position]
            Column(
                modifier = Modifier.fillMaxSize().padding(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(200.dp),
                    shape = RoundedCornerShape(32.dp),
                    color = page.color.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = page.icon,
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = page.color
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    lineHeight = 24.sp
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(pages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) Color.Black
                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)

                    Box(
                        modifier = Modifier.padding(2.dp).clip(CircleShape).background(color)
                            .size(9.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (pagerState.currentPage < pages.size - 1) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onOnboardingFinished()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (pagerState.currentPage == pages.size - 1) "Get Started" else "Next",
                    style = MaterialTheme.typography.labelLarge
                )
                if (pagerState.currentPage < pages.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun OnboardingScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF2196F3),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            secondary = Color(0xFF00BCD4)
        )
    ) {
        OnboardingScreen(onOnboardingFinished = {})
    }
}
