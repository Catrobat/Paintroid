/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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

package org.catrobat.paintroid.model;

import android.net.Uri;

import org.catrobat.paintroid.contract.MainActivityContracts;

public class MainActivityModel implements MainActivityContracts.Model {
	private boolean openedFromCatroid;
	private boolean isFullscreen;
	private boolean isSaved;
	private boolean wasInitialAnimationPlayed;
	private Uri savedPictureUri;
	private Uri cameraImageUri;

	@Override
	public Uri getCameraImageUri() {
		return cameraImageUri;
	}

	@Override
	public void setCameraImageUri(Uri cameraImageUri) {
		this.cameraImageUri = cameraImageUri;
	}

	@Override
	public Uri getSavedPictureUri() {
		return savedPictureUri;
	}

	@Override
	public void setSavedPictureUri(Uri savedPictureUri) {
		this.savedPictureUri = savedPictureUri;
	}

	@Override
	public boolean isSaved() {
		return isSaved;
	}

	@Override
	public void setSaved(boolean saved) {
		isSaved = saved;
	}

	@Override
	public boolean isFullscreen() {
		return isFullscreen;
	}

	@Override
	public void setFullscreen(boolean fullscreen) {
		isFullscreen = fullscreen;
	}

	@Override
	public boolean isOpenedFromCatroid() {
		return openedFromCatroid;
	}

	@Override
	public void setOpenedFromCatroid(boolean openedFromCatroid) {
		this.openedFromCatroid = openedFromCatroid;
	}

	@Override
	public boolean wasInitialAnimationPlayed() {
		return wasInitialAnimationPlayed;
	}

	@Override
	public void setInitialAnimationPlayed(boolean wasInitialAnimationPlayed) {
		this.wasInitialAnimationPlayed = wasInitialAnimationPlayed;
	}
}
