package com.example.time_capsule.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.time_capsule.data.MessageRepository
import com.example.time_capsule.di.AppModule
import com.example.time_capsule.ui.CapsuleViewModel
import com.example.time_capsule.ui.theme.Accent
import com.example.time_capsule.ui.theme.SurfaceMain
import com.example.time_capsule.ui.widgets.BottomBar
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onOpenList: () -> Unit,
    onOpenWrite: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val ctx = LocalContext.current
    val vm = remember {
        val db = AppModule.db(ctx)
        val repo = MessageRepository(db.messageDao())
        CapsuleViewModel(repo).also { it.load() }
    }
    val state by vm.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomBar(
                current = "home",
                onHome = { },
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
            Spacer(Modifier.height(24.dp))
            Text("오늘의 타임캡슐", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

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
                if (state.today != null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = state.today!!.title,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Text(
                            text = state.today!!.text,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    Text("첫 메시지를 작성해보세요.")
                }
            }

            Spacer(Modifier.height(24.dp))
            // 중앙 원형 "다시 보기" 버튼 (시안: 거대한 원형 버튼) :contentReference[oaicite:3]{index=3}
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                FilledTonalButton(
                    onClick = {
                        if (state.list.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("저장된 메시지가 없습니다. 먼저 캡슐을 작성해 주세요.")
                            }
                        } else {
                            vm.refreshRandom()
                        }
                    },
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Accent)
                ) {
                    Text("다시 보기", color = MaterialTheme.colorScheme.onSecondary)
                }
            }
        }
    }
}


