package com.example.timecapsule.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.timecapsule.data.MessageRepository
import com.example.timecapsule.di.AppModule
import com.example.timecapsule.ui.theme.Accent
import kotlinx.coroutines.launch

private const val MAX_TITLE_LENGTH = 60
private const val MAX_BODY_LENGTH = 2000

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    docId: String,
    onClose: () -> Unit,
    onReady: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember(context) { MessageRepository(AppModule.db(context).messageDao()) }
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var text by rememberSaveable { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isWorking by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var readySent by remember { mutableStateOf(false) }

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
        focusedBorderColor = MaterialTheme.colorScheme.secondary,
        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        errorBorderColor = MaterialTheme.colorScheme.error
    )

    LaunchedEffect(docId) {
        isLoading = true
        errorMessage = null
        isLoaded = false
        readySent = false
        try {
            val message = repository.get(docId)
            if (message != null) {
                title = message.title
                text = message.text
                isLoaded = true
            } else {
                title = ""
                text = ""
                errorMessage = "메시지를 찾을 수 없습니다."
                isLoaded = false
            }
        } catch (t: Throwable) {
            title = ""
            text = ""
            errorMessage = t.message ?: "메시지를 불러오지 못했습니다."
            isLoaded = false
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading && !readySent) {
            readySent = true
            onReady()
        }
    }

    fun updateIfPossible() {
        if (title.isBlank() || text.isBlank() || isWorking || !isLoaded) return
        isWorking = true
        errorMessage = null
        scope.launch {
            try {
                repository.update(docId, title, text)
                onClose()
            } catch (t: Throwable) {
                errorMessage = t.message ?: "수정하는 중 문제가 발생했습니다."
            } finally {
                isWorking = false
            }
        }
    }

    fun deleteIfPossible() {
        if (isWorking || !isLoaded) return
        isWorking = true
        errorMessage = null
        scope.launch {
            try {
                repository.delete(docId)
                onClose()
            } catch (t: Throwable) {
                errorMessage = t.message ?: "삭제하는 중 문제가 발생했습니다."
            }
            finally {
                isWorking = false
            }
        }
    }

    fun handleCancel() {
        if (!isWorking) {
            onClose()
        }
    }

    BackHandler(enabled = !isWorking && !isLoading) { handleCancel() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("캡슐 편집") },
                navigationIcon = {
                    TextButton(
                        onClick = { handleCancel() },
                        enabled = !isWorking
                    ) {
                        Text(
                            text = "취소",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { deleteIfPossible() },
                        enabled = !isWorking && isLoaded
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "삭제"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            !isLoaded -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage ?: "편집할 메시지를 찾을 수 없습니다.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.size(12.dp))
                        TextButton(onClick = { onClose() }) {
                            Text("목록으로")
                        }
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                ) {
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
                        enabled = !isWorking,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        colors = fieldColors
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "${title.length} / $MAX_TITLE_LENGTH",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.size(12.dp))

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
                                text = "내용을 수정해보세요...",
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        },
                        enabled = !isWorking,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            capitalization = KeyboardCapitalization.None,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Default
                        ),
                        colors = fieldColors
                    )

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

                    val canUpdate = title.isNotBlank() && text.isNotBlank() && !isWorking
                    Button(
                        onClick = { updateIfPossible() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = canUpdate,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canUpdate) Accent else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (canUpdate) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface.copy(
                                alpha = 0.7f
                            ),
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    ) {
                        if (isWorking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "편집 완료",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.size(2.dp))
                            Text(
                                text = "변경 사항을 저장합니다",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}
