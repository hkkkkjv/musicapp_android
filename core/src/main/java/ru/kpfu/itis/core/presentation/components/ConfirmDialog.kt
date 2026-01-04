package ru.kpfu.itis.core.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import ru.kpfu.itis.core.R

@Composable
fun ConfirmDialog(
    isVisible: Boolean,
    title: String,
    message: String,
    confirmButtonText: String? = null,
    dismissButtonText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isDestructive: Boolean = false,
    modifier: Modifier = Modifier
) {
    if (isVisible) {
        AlertDialog(
            modifier = modifier,
            onDismissRequest = onDismiss,
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isDestructive) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start
                )
            },
            confirmButton = {
                Button(
                    onClick = onConfirm,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDestructive) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.primary,
                        contentColor = if (isDestructive) MaterialTheme.colorScheme.onError
                        else MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        confirmButtonText ?: stringResource(R.string.btn_ok)
                    )
                }
            },
            dismissButton = if (dismissButtonText != null) {
                {
                    TextButton(onClick = onDismiss) {
                        Text(
                            dismissButtonText,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else null
        )
    }
}