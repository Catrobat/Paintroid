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
package org.catrobat.paintroid.ui

import android.content.Context
import android.net.Uri
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.catrobat.paintroid.command.serialization.CommandSerializer
import org.catrobat.paintroid.contract.LayerContracts
import org.catrobat.paintroid.contract.MainActivityContracts.Interactor
import org.catrobat.paintroid.iotasks.CreateFile
import org.catrobat.paintroid.iotasks.CreateFile.CreateFileCallback
import org.catrobat.paintroid.iotasks.LoadImage
import org.catrobat.paintroid.iotasks.LoadImage.LoadImageCallback
import org.catrobat.paintroid.iotasks.SaveImage
import org.catrobat.paintroid.iotasks.SaveImage.SaveImageCallback

class MainActivityInteractor(private val idlingResource: CountingIdlingResource) : Interactor {
    private val scopeIO = CoroutineScope(Dispatchers.IO)

    override fun saveCopy(
        callback: SaveImageCallback,
        requestCode: Int,
        layerModel: LayerContracts.Model,
        commandSerializer: CommandSerializer,
        uri: Uri?,
        context: Context
    ) {
        SaveImage(callback, requestCode, layerModel, commandSerializer, uri, true, context, scopeIO, idlingResource).execute()
    }

    override fun createFile(callback: CreateFileCallback, requestCode: Int, filename: String) {
        CreateFile(callback, requestCode, filename, scopeIO).execute()
    }

    override fun saveImage(
        callback: SaveImageCallback,
        requestCode: Int,
        layerModel: LayerContracts.Model,
        commandSerializer: CommandSerializer,
        uri: Uri?,
        context: Context
    ) {
        SaveImage(callback, requestCode, layerModel, commandSerializer, uri, false, context, scopeIO, idlingResource).execute()
    }

    override fun loadFile(
        callback: LoadImageCallback,
        requestCode: Int,
        uri: Uri?,
        context: Context,
        scaling: Boolean,
        commandSerializer: CommandSerializer,
    ) {
        LoadImage(callback, requestCode, uri, context, scaling, commandSerializer, scopeIO, idlingResource).execute()
    }
}
