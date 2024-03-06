package org.catrobat.paintroid.model

import android.net.Uri

data class SaveImageOptions(val imagePreviewUri: Uri?, val saveAsCopy: Boolean, val saveProject: Boolean)
