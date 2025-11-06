package com.example.time_capsule.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BottomBar(
    current: String,
    onHome: () -> Unit,
    onList: () -> Unit,
    onWrite: () -> Unit
) {

    Row(
        Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(MaterialTheme.colorScheme.surface),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("홈", modifier = Modifier.clickable { onHome() })
        Text("캡슐 보관함", modifier = Modifier.clickable { onList() })
        Text("캡슐 작성", modifier = Modifier.clickable { onWrite() })
    }
}
