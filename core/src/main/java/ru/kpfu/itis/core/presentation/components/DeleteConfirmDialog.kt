package ru.kpfu.itis.core.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeleteConfirmDialog(
    isVisible: Boolean,
    title: String = "Delete item?",
    message: String = "This action cannot be undone.",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ConfirmDialog(
        isVisible = isVisible,
        title = title,
        message = message,
        confirmButtonText = "Delete",
        dismissButtonText = "Cancel",
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        isDestructive = true,
        modifier = modifier
    )
}