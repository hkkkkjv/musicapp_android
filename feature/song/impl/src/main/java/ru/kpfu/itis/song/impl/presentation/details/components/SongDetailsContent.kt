package ru.kpfu.itis.song.impl.presentation.details.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.collections.immutable.ImmutableList
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.core.domain.models.Song
import ru.kpfu.itis.core.domain.models.SongSource
import ru.kpfu.itis.song.impl.R

@Composable
fun SongDetailsContent(
    song: Song,
    userReview: Review?,
    otherReviews: ImmutableList<Review>,
    onDeleteClick: (String) -> Unit,
    onReviewDetailsClick: (String) -> Unit = {},
    coverSize: Dp,
    scrollState: ScrollState,
    isPlaying: Boolean,
    currentPosition: Float,
    duration: Float,
    playerError: String?,
    isLoadingPreview: Boolean,
    onPlayClick: () -> Unit,
    onPositionChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverSize),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = song.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .width(coverSize - 20.dp)
                    .height(coverSize - 20.dp)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(Modifier.height(24.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = song.artist,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            if (song.album != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.album_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = song.album!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (song.durationSec != null) {
                Spacer(Modifier.height(4.dp))
                val minutes = song.durationSec!! / 60
                val seconds = song.durationSec!! % 60
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.duration_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.duration_seconds,
                            minutes,
                            seconds.toString().padStart(2, '0')
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (song.releaseDate != null) {
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.released_label),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = song.releaseDate!!,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (song.source == SongSource.DEEZER && song.previewUrl != null) {
            Spacer(Modifier.height(24.dp))
            PlayerSection(
                isPlaying = isPlaying,
                currentPosition = currentPosition,
                duration = duration,
                onPlayClick = onPlayClick,
                onPositionChange = onPositionChange,
                modifier = Modifier.padding(horizontal = 16.dp),
                playerError = playerError,
                isLoadingPreview = isLoadingPreview
            )
        }

        if (!song.lyrics.isNullOrEmpty()) {
            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.lyrics_title),
                    style = MaterialTheme.typography.headlineSmall.copy(fontSize = 22.sp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = song.lyrics!!,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                        .padding(16.dp),
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.song_details_title) + song.source,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        Divider()

        if (userReview != null) {
            UserReviewSection(
                review = userReview,
                onDeleteClick = { onDeleteClick(userReview.id) },
                onDetailsClick = onReviewDetailsClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (otherReviews.isNotEmpty()) {
                Spacer(Modifier.height(24.dp))
                Text(
                    text = stringResource(R.string.other_reviews),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }
        } else if (otherReviews.isNotEmpty()) {
            Text(
                text = stringResource(R.string.user_reviews),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
            )
        }

        otherReviews.forEach { review ->
            ReviewItemMinimal(review = review, onReviewClick = onReviewDetailsClick)
        }

        Spacer(Modifier.height(32.dp))

    }
}