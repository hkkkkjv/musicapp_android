package ru.kpfu.itis.core.utils

import androidx.annotation.StringRes

interface StringProvider {
    fun getString(@StringRes resId: Int): String
    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String
}