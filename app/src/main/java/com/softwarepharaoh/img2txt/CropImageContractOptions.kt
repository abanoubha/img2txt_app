package com.softwarepharaoh.img2txt

import android.net.Uri
import com.canhub.cropper.CropImageOptions

data class CropImageContractOptions(
    val uri: Uri?,
    val cropImageOptions: CropImageOptions,
)
