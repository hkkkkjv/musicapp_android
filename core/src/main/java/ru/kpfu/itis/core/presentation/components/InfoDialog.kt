package ru.kpfu.itis.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ru.kpfu.itis.core.R

@Composable
fun InfoDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmDialog(
        isVisible = isVisible,
        title = title,
        message = message,
        confirmButtonText = stringResource(R.string.btn_ok),
        dismissButtonText = null,
        onConfirm = onDismiss,
        onDismiss = onDismiss,
        isDestructive = false,
        modifier = modifier
    )
}