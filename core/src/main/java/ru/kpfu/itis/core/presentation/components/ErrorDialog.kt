package ru.kpfu.itis.core.presentation.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.kpfu.itis.core.R

@Composable
fun ErrorDialog(
    error: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.dialog_error)) },
        text = { Text(error) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.btn_ok))
            }
        }
    )
}