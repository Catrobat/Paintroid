package org.catrobat.paintroid.preference.delegate

import org.catrobat.paintroid.preference.UserPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun intUserPreference(defaultValue: Int, key: String? = null): ReadWriteProperty<UserPreferences, Int> =
    object : ReadWriteProperty<UserPreferences, Int> {
        override fun getValue(thisRef: UserPreferences, property: KProperty<*>): Int {
            val preferenceKey = key ?: property.name

            return thisRef
                .preferences
                .getInt(preferenceKey, defaultValue)
        }

        override fun setValue(thisRef: UserPreferences, property: KProperty<*>, value: Int) {
            val preferenceKey = key ?: property.name

            thisRef
                .preferences
                .edit()
                .putInt(preferenceKey, value)
                .apply()
        }
    }
