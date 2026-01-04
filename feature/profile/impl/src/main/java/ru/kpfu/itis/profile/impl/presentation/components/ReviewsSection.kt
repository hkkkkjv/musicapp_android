package ru.kpfu.itis.profile.impl.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.profile.impl.R

@Composable
fun ReviewsSection(
    reviews: ImmutableList<Review>,
    isLoading: Boolean,
    hasMore: Boolean,
    onReviewClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (reviews.isEmpty() && !isLoading) {
            Text(
                stringResource(R.string.no_reviews_yet),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                reviews.forEach { review ->
                    ReviewItemCompact(
                        review = review,
                        onClick = { onReviewClick(review.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReviewClick(review.id) }
                    )
                }

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }

                if (hasMore && !isLoading) {
                    Button(
                        onClick = onLoadMore,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(stringResource(R.string.load_more))
                    }
                }
            }
        }
    }
}
