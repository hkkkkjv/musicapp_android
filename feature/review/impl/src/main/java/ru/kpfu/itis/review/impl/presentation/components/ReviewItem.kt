package ru.kpfu.itis.review.impl.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.review.impl.R

@Composable
fun ReviewItem(
    review: Review,
    isOwner: Boolean = false,
    onDeleteClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = review.userName,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatDate(review.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isOwner) {
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = review.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(4.dp))

        Row {
            repeat(5) { index ->
                Text(
                    text = if (review.rating > index)
                        stringResource(R.string.star_filled) else
                        stringResource(R.string.star_empty),
                    modifier = Modifier.width(16.dp)
                )
            }
            Text(
                text = stringResource(R.string.rating_from_five, review.rating),
                style = MaterialTheme.typography.labelMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = review.description,
            style = MaterialTheme.typography.bodySmall
        )

        if (review.pros.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.pros_with_text, review.pros),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (review.cons.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.cons_with_text, review.cons),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun formatDate(
    timestamp: Timestamp,
): String {
    val timestampMillis = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000

    val now = System.currentTimeMillis()
    val diff = now - timestampMillis
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> stringResource(R.string.just_now)
//        minutes < 60 -> stringResource(R.string.minutes_ago, minutes)
//        hours < 24 -> stringResource(R.string.hours_ago, hours)
//        days < 30 -> stringResource(R.string.days_ago, days)
        else -> stringResource(R.string.long_ago)
    }
}
