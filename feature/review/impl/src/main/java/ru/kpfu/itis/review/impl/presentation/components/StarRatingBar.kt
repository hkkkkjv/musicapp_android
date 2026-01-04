package ru.kpfu.itis.review.impl.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kpfu.itis.review.impl.R

@Composable
fun StarRatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(modifier = modifier) {
        repeat(5) { index ->
            val starRating = index + 1
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = stringResource(R.string.star_rating, starRating),
                tint = if (rating >= starRating) MaterialTheme.colorScheme.primary else Color.Gray,
                modifier = Modifier
                    .size(32.dp)
                    .clickable(enabled = enabled) {
                        onRatingChanged(starRating.toFloat())
                    }
            )
        }
    }
}
