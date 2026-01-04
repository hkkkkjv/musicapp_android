package ru.kpfu.itis.profile.impl.domain

import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.profile.api.data.ProfileRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val profileRepository: ProfileRepository
) {
    suspend operator fun invoke(): User = profileRepository.getUserProfile()
}
