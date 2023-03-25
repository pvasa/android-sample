package dev.priyankvasa.sample.android.ui.util

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.AnyRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.FontRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

fun Context.getDrawableCompat(@DrawableRes resId: Int): Drawable? =
    AppCompatResources.getDrawable(this, resId)

fun Context.getColorCompat(@ColorRes resId: Int): Int =
    ContextCompat.getColor(this, resId)

fun Context.getDimension(@DimenRes dimenRes: Int): Float = resources.getDimension(dimenRes)

fun Context.getDimensionPixelSize(@DimenRes dimenRes: Int): Int =
    resources.getDimensionPixelSize(dimenRes)

fun Context.getFontCompat(@FontRes resId: Int): Typeface? =
    ResourcesCompat.getFont(this, resId)

@AnyRes
fun Context.getResIdAttribute(@AttrRes resId: Int): Int? {
    val value = TypedValue()
    theme.resolveAttribute(resId, value, true)
    return value.resourceId.takeIf { it > 0 }
}
