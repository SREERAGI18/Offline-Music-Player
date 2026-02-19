package com.example.offlinemusicplayer.presentation.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun CommonDialog(
    title: String,
    description: String,
    positiveText: String,
    onDismiss: () -> Unit,
    onPositiveClick: () -> Unit,
    negativeText: String? = null,
    onNegativeClick: (() -> Unit)? = null,
    dismissable: Boolean = true,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },
        text = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onPositiveClick,
                content = {
                    Text(
                        text = positiveText,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                },
            )
        },
        dismissButton = {
            if (negativeText != null) {
                TextButton(
                    onClick = {
                        onNegativeClick?.invoke()
                        onDismiss()
                    },
                    content = {
                        Text(
                            text = negativeText,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                )
            }
        },
        properties =
            DialogProperties(
                dismissOnBackPress = dismissable,
                dismissOnClickOutside = dismissable,
            ),
    )
}
