package com.example.timecapsule.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.timecapsule.data.MessageRepository
import com.example.timecapsule.di.AppModule
import com.example.timecapsule.ui.theme.Accent
import kotlinx.coroutines.launch

private const val MAX_TITLE_LENGTH = 60
private const val MAX_BODY_LENGTH = 2000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    onSaved: () -> Unit,
    onCancel: () -> Unit,
    onReady: () -> Unit = {}
) {
    val context = LocalContext.current
    // Room DB 기반 저장소를 한 번만 생성해 사용한다.
    val repository = remember(context) { MessageRepository(AppModule.db(context).messageDao()) }
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { onReady() }

    // 다크 테마에서도 잘 보이도록 입력 필드 컬러를 직접 지정한다.
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        cursorColor = MaterialTheme.colorScheme.secondary,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedBorderColor = MaterialTheme.colorScheme.secondary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        errorBorderColor = MaterialTheme.colorScheme.error
    )

    fun saveIfPossible() {
        // 제목과 본문이 모두 채워져 있고 저장 중이 아닐 때만 저장을 진행한다.
        if (title.isBlank() || text.isBlank() || isSaving) return
        isSaving = true
        errorMessage = null
        scope.launch {
            try {
                repository.add(title, text)
                text = ""
                title = ""
                isSaving = false
                onSaved()
            } catch (t: Throwable) {
                errorMessage = t.message ?: "저장하는 중 문제가 발생했습니다."
                isSaving = false
            }
        }
    }

    fun handleCancel() {
        if (isSaving) return
        text = ""
        title = ""
        errorMessage = null
        onCancel()
    }

    BackHandler(enabled = !isSaving) {
        handleCancel()
    }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text("캡슐 작성") },
            navigationIcon = {
                // 저장 중이 아니면 입력값을 비우고 취소 콜백을 호출한다.
                TextButton(
                    onClick = { handleCancel() },
                    enabled = !isSaving
                ) {
                    Text(
                        text = "취소",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        )
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 제목 입력 필드
            OutlinedTextField(
                value = title,
                onValueChange = {
                    if (it.length <= MAX_TITLE_LENGTH) title = it
                },
                modifier = Modifier.fillMaxWidth(),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                placeholder = {
                    Text(
                        text = "제목을 입력하세요",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                colors = fieldColors
            )

            // 제목 글자 수를 보여준다.
            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "${title.length} / $MAX_TITLE_LENGTH",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.size(12.dp))

            // 본문 입력 필드
            OutlinedTextField(
                value = text,
                onValueChange = {
                    if (it.length <= MAX_BODY_LENGTH) text = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface),
                placeholder = {
                    Text(
                        text = "미래의 나에게 남기고 싶은 메시지를 자유롭게 작성해보세요...",
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Default
                ),
                colors = fieldColors
            )

            // 본문 글자 수를 보여준다.
            Text(
                text = "${text.length} / $MAX_BODY_LENGTH",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.size(8.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.size(8.dp))
            }

            val canSave = title.isNotBlank() && text.isNotBlank() && !isSaving
            Button(
                onClick = { saveIfPossible() },
                modifier = Modifier.fillMaxWidth(),
                enabled = canSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (canSave) Accent else MaterialTheme.colorScheme.surface,
                    contentColor = if (canSave) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            ) {
                if (isSaving) {
                    // 저장 중에는 로딩 스피너와 여백을 보여준다.
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(20.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                }
                Text("저장")
            }
        }
    }
}
