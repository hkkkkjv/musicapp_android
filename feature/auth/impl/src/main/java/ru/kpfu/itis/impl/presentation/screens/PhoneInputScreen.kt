package ru.kpfu.itis.impl.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.kpfu.itis.impl.R
import ru.kpfu.itis.impl.utils.phone.PhoneValidator
import ru.kpfu.itis.impl.utils.phone.PhoneVisualTransformation

@Composable
fun PhoneInputScreen(
    phone: String,
    onPhoneChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean,
    isRegistration: Boolean,
    phoneValidator: PhoneValidator,
    onSignUpClick: () -> Unit
) {
    val isPhoneValid = phoneValidator.isPhoneValidForUI(phone)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = if (isRegistration) stringResource(R.string.btn_sign_up) else stringResource(
                    R.string.btn_login
                ),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.desc_enter_phone),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text(stringResource(R.string.label_phone)) },
                placeholder = { Text(stringResource(R.string.placeholder_phone)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                enabled = !isLoading,
                shape = RoundedCornerShape(24.dp),
                visualTransformation = PhoneVisualTransformation(isValid = isPhoneValid),
                isError = phone.isNotEmpty() && !isPhoneValid
            )

            Button(
                onClick = onSendCode,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(bottom = 16.dp),
                enabled = !isLoading && isPhoneValid
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(stringResource(R.string.btn_send_code))
                }
            }

            if (!isRegistration) {
                TextButton(
                    onClick = onSignUpClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.hint_dont_have_account))
                }
            }
        }
    }
}