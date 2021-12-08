package ru.geekbrains.popular.libraries.imageswork.logic

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import androidx.core.app.ActivityCompat
import ru.geekbrains.popular.libraries.imageswork.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class Logic(
    private val mainPresenter: MainPresenter
) {
    /** ИСХОДНЫЕ ДАННЫЕ */ //region
    private var bitmap: Bitmap? = null
    //endregion

    /** Загрузка jpg-картики */
    fun loadingImage(selectedFile: Uri?) {
        if ((selectedFile != null) && (selectedFile.toString().length > 3) &&
            (selectedFile.toString().substring(selectedFile.toString().length - 3,
                selectedFile.toString().length)
                .lowercase() == Constants.NAME_INPUT_FILE_EXTENTION)
        ) {
            mainPresenter.showMessage("Загружена картинка: $selectedFile")
            /** Отображение картинки */
            bitmap = getBitmapFromUri(selectedFile, mainPresenter.getContext())
            bitmap?.let { bitmap ->
                mainPresenter.showImage(bitmap)
            }
            if (bitmap == null)
                mainPresenter.showMessage("Загрузить jpg-файл не получилось.")
        } else {
            mainPresenter.showMessage("Нужно выбрать jpg-файл. Вы выбрали не jpg файл. " +
                        "Попробуйте ещё раз.")
        }
    }

    /** Сохранение загруженной jpg-картики в png-файл */
    fun saveImageToPNGFile() {
        if (bitmap == null) {
            mainPresenter.showMessage("Не загружена картинка для сохранения в png.")
        }
        /** Сохранение загруженной картинки в png-файл */
        bitmap?.let {
            /** Получение разрешения на запись информации */
            isStoragePermissionGranted()
            /** Сохранение картинки в png-файл */
            bitmapToFile(it, Constants.NAME_PNG_FILE)
        }
    }

    /** Получение bitmap картинки по uri-ссылке */
    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")
        parcelFileDescriptor?.let {
            val fileDescriptor = it.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            it.close()
            return image
        }
        return null
    }

    /** Сохранение картинки в png-файл */
    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        var file: File? = null
        return try {
            // Сохранение в корневом каталоге Internal storage
            // Сохранение в Internal storage в папке приложения:
            // /storage/emulated/0/Android/data/
            // ru.geekbrains.popular.libraries.less_1_homework/files/Pictures/
            file = File("${mainPresenter.getContext().getExternalFilesDir(
                Environment.DIRECTORY_PICTURES)}")

            // Сохранение картинки в Internal storage:
            /** Создание директории, если она ещё не создана */
            if (!file.exists()) {
                file.mkdirs()
            }

            /** Создание файла */
            file = File(file, "${File.separator}$fileNameToSave")
            file.createNewFile()

            /** Конвертация картинки в png */
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()

            /** Бинарная запись картинки в файл */
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            mainPresenter.showMessage("Картинка успешно сохранена по адресу: $file")
            file
        } catch (e: Exception) {
            mainPresenter.showMessage("${e.message}")
            file
        }
    }

    /** Получение разрешений на запись и считывание информации с телефона */
    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mainPresenter.getContext().checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                mainPresenter.showMessage("Разрешение на запись и считывание данных получено.")
                true
            } else {
                mainPresenter.showMessage("Разрешение на запись и считывание данных отсутствует.")
                ActivityCompat.requestPermissions(
                    mainPresenter as MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)
                false
            }
        } else {
            mainPresenter.showMessage("Разрешение на запись и считывание данных получено.")
            true
        }
    }
}