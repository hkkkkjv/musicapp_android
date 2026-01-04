package ru.kpfu.itis.impl.data

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import ru.kpfu.itis.auth.api.data.AuthRepository
import ru.kpfu.itis.auth.api.domain.models.User
import ru.kpfu.itis.auth.api.presentation.AuthErrorType
import ru.kpfu.itis.impl.data.exceptions.AuthRepositoryException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {
    override suspend fun isPhoneRegistered(phone: String): Boolean {
        return try {
            Log.i("isPhoneRegistered", "Checking phone: $phone")

            if (firebaseAuth.currentUser == null) {
                Log.i("isPhoneRegistered", "No current user - signing in anonymously")
                firebaseAuth.signInAnonymously().await()
            }

            val snapshot = firestore.collection("users")
                .whereEqualTo("phoneNumber", phone)
                .limit(1)
                .get()
                .await()

            val exists = !snapshot.isEmpty
            Log.i("isPhoneRegistered", "Phone exists: $exists")

            exists

        } catch (e: Exception) {
            Log.e("isPhoneRegistered", "ERROR: ${e.message}", e)
            throw AuthRepositoryException(AuthErrorType.NETWORK_ERROR, e)
        }
    }


    override suspend fun registerUser(
        username: String,
        phone: String
    ): User {
        return try {
            if (isPhoneRegistered(phone)) {
                throw AuthRepositoryException(
                    AuthErrorType.PHONE_ALREADY_REGISTERED,
                    Exception("This phone is already registered")
                )
            }

            val firebaseUser = firebaseAuth.currentUser
                ?: throw AuthRepositoryException(
                    AuthErrorType.USER_NOT_AUTHENTICATED,
                    Exception("User is not authenticated")
                )

            val uid = firebaseUser.uid
            val phoneNumber = firebaseUser.phoneNumber
                ?: throw AuthRepositoryException(
                    AuthErrorType.FIREBASE_AUTH_ERROR,
                    Exception("Phone number not found")
                )
            Log.i("registerUser", "START: uid=$uid, username=$username, phone=$phoneNumber")

            val createdAtTimestamp = Timestamp.now()


            val userMap = mapOf(
                "id" to uid,
                "username" to username,
                "phoneNumber" to phoneNumber,
                "photoUrl" to null,
                "createdAt" to createdAtTimestamp
            )
            Log.i("registerUser", username)

            firestore.collection("users")
                .document(uid)
                .set(userMap)
                .await()

            firebaseUser.updateProfile(
                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
            ).await()
            return User(
                id = uid,
                username = username,
                phoneNumber = phoneNumber,
                photoUrl = firebaseUser.photoUrl?.toString(),
                createdAt = Timestamp.now()
            )

        } catch (e: AuthRepositoryException) {
            throw e
        } catch (e: Exception) {
            Log.e("registerUser", "ERROR: ${e.message}", e)
            throw AuthRepositoryException(AuthErrorType.REGISTRATION_FAILED, e)
        }
    }

    override suspend fun verifyPhoneCode(
        verificationId: String,
        smsCode: String
    ): User {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, smsCode)

            val authResult = firebaseAuth
                .signInWithCredential(credential)
                .await()

            val firebaseUser = authResult.user
                ?: throw AuthRepositoryException(
                    AuthErrorType.FIREBASE_AUTH_ERROR,
                    Exception("User is null")
                )
            val phoneNumber = firebaseUser.phoneNumber
                ?: throw AuthRepositoryException(
                    AuthErrorType.FIREBASE_AUTH_ERROR,
                    Exception("Phone number is null")
                )
            return User("", phoneNumber, "", "", Timestamp.now())
        } catch (e: AuthRepositoryException) {
            throw e
        } catch (e: Exception) {
            Log.e("verifyPhoneCode", "ERROR: ${e.message}", e)
            throw AuthRepositoryException(AuthErrorType.FIREBASE_AUTH_ERROR, e)
        }
    }

    override suspend fun loginUser(phone: String): User {
        return try {
            if (!isPhoneRegistered(phone)) {
                throw AuthRepositoryException(
                    AuthErrorType.PHONE_NOT_REGISTERED,
                    Exception("This phone is not registered. Please sign up first.")
                )
            }

            return User(
                id = "",
                username = "",
                phoneNumber = phone,
                photoUrl = null,
                createdAt = Timestamp.now()
            )

        } catch (e: AuthRepositoryException) {
            throw e
        } catch (e: Exception) {
            Log.e("loginUser", "ERROR: ${e.message}", e)
            throw AuthRepositoryException(AuthErrorType.NETWORK_ERROR, e)
        }
    }

    override suspend fun logout() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Log.e("logout", "ERROR: ${e.message}", e)
            throw AuthRepositoryException(AuthErrorType.LOGOUT_FAILED, e)
        }
    }

    override fun getCurrentUser(): User? {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: return null
            val phoneNumber = firebaseUser.phoneNumber ?: return null
            if (firebaseUser.displayName.isNullOrEmpty()) {
                Log.i(
                    "getCurrentUser",
                    "FirebaseUser exists but displayName is empty - not fully authenticated"
                )
                return null
            }
            return User(
                id = firebaseUser.uid,
                phoneNumber = phoneNumber,
                username = firebaseUser.displayName,
                photoUrl = firebaseUser.photoUrl?.toString(),
                createdAt = Timestamp.now()
            )
        } catch (e: Exception) {
            Log.e("getCurrentUser", "ERROR: ${e.message}", e)
            null
        }
    }

    override fun isLoggedIn(): Boolean {
        val firebaseUser = firebaseAuth.currentUser
        return firebaseUser != null && !firebaseUser.displayName.isNullOrEmpty()
    }
}
