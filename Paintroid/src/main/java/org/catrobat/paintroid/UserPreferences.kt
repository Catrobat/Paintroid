/*
 * Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid

import android.content.SharedPreferences
import org.catrobat.paintroid.common.IMAGE_NUMBER_SHARED_PREFERENCES_TAG
import org.catrobat.paintroid.common.SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG

open class UserPreferences(var preferences: SharedPreferences) {
    open val preferenceLikeUsDialogValue: Boolean
        get() = preferences.getBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, false)
    open var preferenceImageNumber: Int
        get() = preferences.getInt(IMAGE_NUMBER_SHARED_PREFERENCES_TAG, 0)
        set(value) {
            preferences
                .edit()
                .putInt(IMAGE_NUMBER_SHARED_PREFERENCES_TAG, value)
                .apply()
        }

    open fun setPreferenceLikeUsDialogValue() {
        preferences
            .edit()
            .putBoolean(SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG, true)
            .apply()
    }
}
