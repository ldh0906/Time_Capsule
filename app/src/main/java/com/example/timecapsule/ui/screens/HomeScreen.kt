package com.example.timecapsule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.timecapsule.data.MessageRepository
import com.example.timecapsule.di.AppModule
import com.example.timecapsule.ui.CapsuleViewModel
import com.example.timecapsule.ui.theme.Accent
import com.example.timecapsule.ui.theme.SurfaceMain
import com.example.timecapsule.ui.widgets.BottomBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onScreenReady: () -> Unit = {},
    onOpenList: () -> Unit,
    onOpenWrite: () -> Unit,
    onOpenSettings: () -> Unit,
    onBackToHome: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val vm = remember {
        val db = AppModule.db(ctx)
        val repo = MessageRepository(db.messageDao())
        CapsuleViewModel(repo).also { it.load() }
    }
    val state by vm.state.collectAsState()
    LaunchedEffect(Unit) { onScreenReady() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomBar(
                current = "home",
                onHome = { onBackToHome?.invoke() },
                onList = onOpenList,
                onWrite = onOpenWrite
            )
        },
        floatingActionButton = {
            // 우하단 "설정" 진입(시안의 우측하단 액션 느낌)
            FloatingActionButton(
                onClick = onOpenSettings,
                containerColor = Accent
            ) { Text("설정", color = MaterialTheme.colorScheme.onSecondary) }
        }
    ) { pad ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                "오늘의 타임캡슐",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = MaterialTheme.typography.titleLarge.fontSize * 1.56f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
                ) {
                    // 메시지 카드
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .clip(MaterialTheme.shapes.extraLarge)
                            .background(SurfaceMain)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        val contentTextStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize * 1.5f,
                            fontWeight = FontWeight.Bold
                        )
                        if (state.today != null) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Text(
                                    text = state.today!!.text,
                                    style = contentTextStyle,
                                    modifier = Modifier.align(Alignment.Center),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "- ${state.today!!.title}",
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.align(Alignment.BottomEnd)
                                )
                            }
                        } else {
                            Text(
                                "첫 메시지를 작성해보세요.",
                                style = contentTextStyle,
                                modifier = Modifier.align(Alignment.Center),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    FloatingActionButton(
                        onClick = {
                            if (state.list.isEmpty()) {
                                scope.launch {
                                    snackbarHostState.showSnackbar("저장된 메시지가 없습니다. 먼저 캡슐을 작성해 주세요.")
                                }
                            } else {
                                vm.refreshRandom()
                            }
                        },
                        containerColor = Accent,
                        modifier = Modifier.size(230.dp),
                        shape = CircleShape
                    ) {
                        Text(
                            "다시 보기",
                            color = MaterialTheme.colorScheme.onSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


