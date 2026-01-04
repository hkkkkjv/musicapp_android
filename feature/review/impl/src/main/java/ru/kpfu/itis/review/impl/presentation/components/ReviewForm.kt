package ru.kpfu.itis.review.impl.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.kpfu.itis.review.impl.R

@Composable
fun ReviewForm(
    title: String,
    onTitleChanged: (String) -> Unit,
    description: String,
    onDescriptionChanged: (String) -> Unit,
    pros: String,
    onProsChanged: (String) -> Unit,
    cons: String,
    onConsChanged: (String) -> Unit,
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false
) {
    Column(modifier = modifier.padding(16.dp)) {
        Text(
            text = if (isEditMode)
                stringResource(R.string.edit_review)
            else
                stringResource(R.string.write_review),
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChanged,
            label = { Text(stringResource(R.string.review_title)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            supportingText = {
                Text("${title.length}/50")
            },
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChanged,
            label = { Text(stringResource(R.string.review_description)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 5,
            supportingText = {
                Text("${description.length}/1000")
            },
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = pros,
            onValueChange = onProsChanged,
            label = { Text(stringResource(R.string.review_pros)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 3,
            supportingText = {
                Text("${pros.length}/300")
            },
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = cons,
            onValueChange = onConsChanged,
            label = { Text(stringResource(R.string.review_cons)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            maxLines = 3,
            supportingText = {
                Text("${cons.length}/300")
            },
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(Modifier.height(16.dp))

        Text(text = stringResource(R.string.rating) + ": ${"%.1f".format(rating)}/5.0")
        Spacer(Modifier.height(8.dp))

        StarRatingBar(
            rating = rating,
            onRatingChanged = onRatingChanged,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSubmitting && title.isNotBlank() && description.isNotBlank()
        ) {
            Text(
                if (isSubmitting)
                    stringResource(R.string.submitting)
                else if (isEditMode)
                    stringResource(R.string.update_review)
                else
                    stringResource(R.string.submit_review)
            )
        }
    }
}
