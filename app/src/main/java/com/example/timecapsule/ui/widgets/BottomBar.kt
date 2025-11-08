package com.example.timecapsule.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
import androidx.compose.runtime.remember

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
        val homeInteraction = remember { MutableInteractionSource() }
        val listInteraction = remember { MutableInteractionSource() }
        Text(
            "홈",
            modifier = Modifier.alpha(if (current == "home") 0.3f else 1f)
                .clickable(
                    enabled = current != "home",
                    interactionSource = homeInteraction,
                    indication = null,
                    role = Role.Button
                ) { onHome() }
        )
        Text(
            "캡슐 보관함",
            modifier = Modifier.alpha(if (current == "list") 0.3f else 1f)
                .clickable(
                    enabled = current != "list",
                    interactionSource = listInteraction,
                    indication = null,
                    role = Role.Button
                ) { onList() }
        )
        Text(
            "캡슐 작성",
            modifier = Modifier.clickable { onWrite() }
        )
    }
}
