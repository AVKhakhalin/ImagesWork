package ru.geekbrains.popular.libraries.imageswork.logic

import android.graphics.Bitmap

interface PresenterView {
    fun showImage(bitmap: Bitmap)
    fun showToastLogMessage(newText: String)
}