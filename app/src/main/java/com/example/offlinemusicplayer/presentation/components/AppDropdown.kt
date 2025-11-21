package com.example.offlinemusicplayer.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.offlinemusicplayer.domain.enum_classes.Options

@Composable
fun AppDropdown(
    options: List<Options>,
    onOptionSelected: (Options) -> Unit,
    onDismiss: () -> Unit,
    menuExpanded: Boolean
) {
    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(12.dp)
    ) {
        for (option in options) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = option.displayName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = {
                    onOptionSelected(option)
                    onDismiss()
                }
            )
        }
    }
}