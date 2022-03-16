/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.preference

import android.content.SharedPreferences
import org.catrobat.paintroid.common.IMAGE_NUMBER_SHARED_PREFERENCES_TAG
import org.catrobat.paintroid.common.SHOW_INTRO_AFTER_INSTALL_PREFERENCES_TAG
import org.catrobat.paintroid.common.SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG
import org.catrobat.paintroid.preference.delegate.booleanUserPreference
import org.catrobat.paintroid.preference.delegate.intUserPreference

class UserPreferences(var preferences: SharedPreferences) {
    var likeUsDialogShown by booleanUserPreference(false, SHOW_LIKE_US_DIALOG_SHARED_PREFERENCES_TAG)
    var appFirstOpenedAfterInstall by booleanUserPreference(true, SHOW_INTRO_AFTER_INSTALL_PREFERENCES_TAG)
    var savedImagesNumber by intUserPreference(0, IMAGE_NUMBER_SHARED_PREFERENCES_TAG)
}
