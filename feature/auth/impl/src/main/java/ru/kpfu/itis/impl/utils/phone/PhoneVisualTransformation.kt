package ru.kpfu.itis.impl.utils.phone

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle

class PhoneVisualTransformation(
    private val isValid: Boolean = true,
    private val errorColor: Color = Color.Red
)  : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text.filter(Char::isDigit).take(10)
        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Unspecified)) {
                append("+7(")
            }

            val digitColor = if (isValid) Color.Unspecified else errorColor

            digits.forEachIndexed { i, c ->
                withStyle(style = SpanStyle(color = digitColor)) {
                    append(c)
                    when (i) {
                        2 -> withStyle(style = SpanStyle(color = Color.Unspecified)) {
                            append(") ")
                        }
                        5, 7 -> withStyle(style = SpanStyle(color = Color.Unspecified)) {
                            append("-")
                        }
                    }
                }
            }
        }

        val plainText = annotatedString.text

        val origToTrans = IntArray(digits.length + 1)
        var tIndex = 3 // after "+7("
        origToTrans[0] = 3
        for (i in 0 until digits.length) {
            when (i) {
                2 -> tIndex += 2  // skip ") "
                5, 7 -> tIndex += 1 // skip "-"
            }
            origToTrans[i + 1] = ++tIndex
        }
        origToTrans[digits.length] = plainText.length

        val transToOrig = IntArray(plainText.length + 1)
        var oi = 0
        for (ti in 0..plainText.length) {
            while (oi < origToTrans.size - 1 && origToTrans[oi + 1] <= ti) oi++
            transToOrig[ti] = oi
        }

        val offsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val o = offset.coerceIn(0, origToTrans.size - 1)
                return origToTrans[o].coerceIn(0, plainText.length)
            }
            override fun transformedToOriginal(offset: Int): Int {
                val t = offset.coerceIn(0, transToOrig.size - 1)
                return transToOrig[t].coerceIn(0, digits.length)
            }
        }

        return TransformedText(annotatedString, offsetTranslator)
    }
}
