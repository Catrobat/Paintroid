package org.catrobat.paintroid.preference.delegate

import org.catrobat.paintroid.preference.UserPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun booleanUserPreference(defaultValue: Boolean, key: String): ReadWriteProperty<UserPreferences, Boolean> =
    object : ReadWriteProperty<UserPreferences, Boolean> {
        override fun getValue(thisRef: UserPreferences, property: KProperty<*>): Boolean {
            return thisRef
                .preferences
                .getBoolean(key, defaultValue)
        }

        override fun setValue(thisRef: UserPreferences, property: KProperty<*>, value: Boolean) {
            thisRef
                .preferences
                .edit()
                .putBoolean(key, value)
                .apply()
        }
    }
