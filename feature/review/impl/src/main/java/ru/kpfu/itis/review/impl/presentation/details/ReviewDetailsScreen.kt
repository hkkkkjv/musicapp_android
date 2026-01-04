package ru.kpfu.itis.review.impl.presentation.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.Timestamp
import ru.kpfu.itis.core.domain.models.Review
import ru.kpfu.itis.core.presentation.components.DeleteConfirmDialog
import ru.kpfu.itis.core.presentation.components.ErrorDialog
import ru.kpfu.itis.review.impl.R
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewDetailsScreen(
    viewModel: ReviewDetailsViewModel,
    reviewId: String,
    onBack: () -> Unit,
    onReviewDeleted: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    LaunchedEffect(reviewId) {
        viewModel.initialize(reviewId)
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ReviewDetailsEffect.ReviewDeleted -> {
                    onReviewDeleted()
                }

                is ReviewDetailsEffect.ShowError -> {
                }
            }
        }
    }

    var showErrorDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.error) {
        showErrorDialog = state.error != null
    }

    ErrorDialog(
        isVisible = showErrorDialog,
        error = state.error ?: "",
        onDismiss = {
            showErrorDialog = false
            viewModel.onEvent(ReviewDetailsEvent.OnDismissError)
        }
    )

    DeleteConfirmDialog(
        isVisible = state.showDeleteConfirmation,
        title = stringResource(R.string.delete_review),
        message = stringResource(R.string.this_action_cannot_be_undone_your_review_will_be_permanently_deleted),
        onConfirm = { viewModel.onEvent(ReviewDetailsEvent.OnConfirmDelete) },
        onDismiss = { viewModel.onEvent(ReviewDetailsEvent.OnCancelDelete) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.review_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (state.review != null && !state.isDeleting && state.isOwner) {
                        IconButton(onClick = { viewModel.onEvent(ReviewDetailsEvent.OnDeleteClick) }) {
                            Icon(
                                Icons.Default.Delete,
                                null,
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.review != null -> {
                ReviewDetailsContent(
                    review = state.review!!,
                    isDeleting = state.isDeleting,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    scrollState = scrollState
                )
            }
        }
    }
}

@Composable
private fun ReviewDetailsContent(
    review: Review,
    isDeleting: Boolean,
    scrollState: LazyListState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = scrollState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 0.dp)
    ) {
        item {
            SongHeaderSection(review)
        }

        item {
            RatingQuickInfoSection(review)
        }

        if (review.title.isNotEmpty()) {
            item {
                ReviewMetadataSection(review)
            }
        }

        item {
            ReviewContentCard(review)
        }

        if (review.pros.isNotEmpty()) {
            item {
                ProsSection(review)
            }
        }

        if (review.cons.isNotEmpty()) {
            item {
                ConsSection(review)
            }
        }

        item {
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SongHeaderSection(review: Review) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0f,
                    endY = 800f
                )
            )
    ) {
        AsyncImage(
            model = review.songCoverUrl,
            contentDescription = review.songTitle,
            modifier = Modifier
                .size(240.dp)
                .clip(RoundedCornerShape(24.dp))
                .align(Alignment.Center),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )
    }
}

@Composable
private fun RatingQuickInfoSection(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .offset(y = (-30).dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            Icons.Filled.Star,
                            null,
                            modifier = Modifier.size(24.dp),
                            tint = if (index < review.rating.toInt())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.rating_from_five, review.rating),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = review.songTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = review.songArtist,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = stringResource(R.string.reviewed_by),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = formatDate(review.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (review.isEdited) {
                            Text(
                                text = stringResource(R.string.edited),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewMetadataSection(review: Review) {
    AnimatedContent(
        targetState = review.title,
        transitionSpec = {
            slideInVertically { it } + fadeIn() togetherWith slideOutVertically { -it } + fadeOut()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) { title ->
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReviewContentCard(review: Review) {
    if (review.description.isEmpty()) return

    AnimatedVisibility(
        visible = review.description.isNotEmpty(),
        enter = slideInVertically(
            initialOffsetY = { 100 },
            animationSpec = tween(400, delayMillis = 100, easing = EaseInOutCubic)
        ) + fadeIn(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = stringResource(R.string.review),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = review.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 24.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Justify
                )
            }
        }
    }
}

@Composable
private fun ProsSection(review: Review) {
    AnimatedVisibility(
        visible = review.pros.isNotEmpty(),
        enter = slideInVertically(
            initialOffsetY = { 100 },
            animationSpec = tween(400, delayMillis = 200, easing = EaseInOutCubic)
        ) + fadeIn(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✓",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = stringResource(R.string.pros),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = review.pros,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun ConsSection(review: Review) {
    AnimatedVisibility(
        visible = review.cons.isNotEmpty(),
        enter = slideInVertically(
            initialOffsetY = { 100 },
            animationSpec = tween(400, delayMillis = 300, easing = EaseInOutCubic)
        ) + fadeIn(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✕",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = stringResource(R.string.cons),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }

                Spacer(Modifier.height(14.dp))

                Text(
                    text = review.cons,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 22.sp
                    ),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun formatDate(timestamp: Timestamp): String {
    val timestampMillis = timestamp.toDate().time
    val now = System.currentTimeMillis()
    val diff = now - timestampMillis

    return when {
        diff < 60 * 1000 -> stringResource(R.string.just_now)
        diff < 60 * 60 * 1000 -> stringResource(
            R.string.minutes_ago_format,
            (diff / (60 * 1000)).toInt()
        )

        diff < 24 * 60 * 60 * 1000 -> stringResource(
            R.string.hours_ago_format,
            (diff / (60 * 60 * 1000)).toInt()
        )

        diff < 7 * 24 * 60 * 60 * 1000 -> stringResource(
            R.string.days_ago_format,
            (diff / (24 * 60 * 60 * 1000)).toInt()
        )

        else -> {
            val format = SimpleDateFormat("dd.MM.yyyy", java.util.Locale.getDefault())
            format.format(Date(timestampMillis))
        }
    }
}
