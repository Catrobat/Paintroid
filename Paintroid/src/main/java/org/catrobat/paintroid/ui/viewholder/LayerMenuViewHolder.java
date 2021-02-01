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

package org.catrobat.paintroid.ui.viewholder;

import android.view.View;
import android.view.ViewGroup;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.contract.LayerContracts;

public class LayerMenuViewHolder implements LayerContracts.LayerMenuViewHolder {
	public final View layerAddButton;
	public final View layerDeleteButton;

	public LayerMenuViewHolder(ViewGroup layerLayout) {
		layerAddButton = layerLayout.findViewById(R.id.pocketpaint_layer_side_nav_button_add);
		layerDeleteButton = layerLayout.findViewById(R.id.pocketpaint_layer_side_nav_button_delete);
	}

	@Override
	public void disableAddLayerButton() {
		layerAddButton.setEnabled(false);
	}

	@Override
	public void enableAddLayerButton() {
		layerAddButton.setEnabled(true);
	}

	@Override
	public void disableRemoveLayerButton() {
		layerDeleteButton.setEnabled(false);
	}

	@Override
	public void enableRemoveLayerButton() {
		layerDeleteButton.setEnabled(true);
	}
}
