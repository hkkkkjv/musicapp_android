package ru.kpfu.itis.impl.utils

import kotlinx.coroutines.CancellationException

@SuppressWarnings("TooGenericExceptionCaught")
inline fun <T> runSuspendCatching(block: () -> T): Result<T> {
    return try {
        Result.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (e: Throwable) {
        Result.failure(e)
    }
}