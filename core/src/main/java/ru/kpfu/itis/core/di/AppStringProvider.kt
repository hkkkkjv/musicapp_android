package ru.kpfu.itis.core.di

import android.content.Context
import androidx.annotation.StringRes
import ru.kpfu.itis.core.utils.StringProvider
import javax.inject.Inject

class AppStringProvider @Inject constructor(
    private val context: Context
) : StringProvider {

    override fun getString(@StringRes resId: Int): String =
        context.getString(resId)


    override fun getString(@StringRes resId: Int, vararg formatArgs: Any): String =
        context.getString(resId, *formatArgs)

}