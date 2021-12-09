package ru.geekbrains.popular.libraries.imageswork.logic

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.app.ActivityCompat.startActivityForResult
import ru.geekbrains.popular.libraries.imageswork.MainActivity

class MainPresenter(
    private val presenterView: PresenterView
) {
    /** ЗАДАНИЕ ПЕРЕМЕННЫХ */ //region
    private val logic: Logic = Logic(this@MainPresenter)
    //endregion

    /** Метод выбора картинки на телефоне */
    fun chooseImageOnPhone() {
        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(presenterView as MainActivity,
            Intent.createChooser(intent, "Выберите jpg файл"),
            Constants.REQUEST_CODE, null)
    }

    /** Метод загрузки выбранной картики */
    fun loadAndShowImage(data: Intent?) {
        if (data != null) {
            logic.loadingImage(data.data)
        } else {
            presenterView.showToastLogMessage("Вы не выбрали jpg-картинку для загрузки.")
        }
    }

    /** Отображение загруженной картинки */
    fun showImage(bitmap: Bitmap) {
        presenterView.showImage(bitmap)
    }

    /** Метод вывода сообщения об ошибке */
    fun showMessage(message: String) {
        presenterView.showToastLogMessage(message)
    }

    /** Метод передачи в логику контекста */
    fun getContext(): Context {
        return (presenterView as MainActivity)
    }

    /** Сохранение загруженной картики в png-формат */
    fun saveImageToPNG() {
        /** Сохранение картинки */
        logic.saveImageToPNGFile()
    }
}