package ru.geekbrains.popular.libraries.imageswork.logic

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.core.app.ActivityCompat
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.geekbrains.popular.libraries.imageswork.MainActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class Logic(
    private val mainPresenter: MainPresenter
) {
    /** ИСХОДНЫЕ ДАННЫЕ */ //region
    // bitmap
    private var bitmap: Bitmap? = null
    // message
    private var message: String = ""
    //endregion

    /** Загрузка jpg-картинки */ //region
    fun loadingImage(selectedFile: Uri?) {
        loadingImageCompletable(selectedFile)
            .doOnComplete {
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                mainPresenter.showMessage(message)
                bitmap?.let { bitmap ->
                    mainPresenter.showImage(bitmap)
                }
            }, {
                Log.d("mylogs", "Ошибка загрузки jpg-файла: ${it.message}")
            })
    }
    fun loadingImageCompletable(selectedFile: Uri?): Completable = Completable.create { emitter ->
        if ((selectedFile != null) && (selectedFile.toString().length > 3) &&
            (selectedFile.toString().substring(selectedFile.toString().length - 3,
                selectedFile.toString().length)
                .lowercase() == Constants.NAME_INPUT_FILE_EXTENTION)
        ) {
            /** Загрузка картинки */
            Log.d("mylogs", "bitmap before: $bitmap")
            bitmap = getBitmapFromUri(selectedFile, mainPresenter.getContext())
            if (bitmap == null) {
                message = "Загрузить jpg-файл не получилось"
                emitter.onError(IllegalStateException("Загрузить jpg-файл не получилось"))
                return@create
            }
            message = "Загружена картинка: $selectedFile"
            emitter.onComplete()
        } else {
            message = "Нужно выбрать jpg-файл. Вы выбрали не jpg-файл. Попробуйте ещё раз"
            emitter.onError(IllegalStateException("Нужно выбрать jpg-файл. Вы выбрали не jpg-файл. " +
                    "Попробуйте ещё раз."))
            return@create
        }
    }
    /** Получение bitmap картинки по uri-ссылке */
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
    //endregion

    /** Сохранение загруженной jpg-картики в png-файл */ //region
    fun saveImageToPNGFile() {
        bitmap?.let {
            /** Получение разрешения на запись информации */
            isStoragePermissionGranted()
            /** Сохранение картики */
            saveImageToPNGFileCompletable()
                .doOnComplete {
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mainPresenter.showMessage(message)
                }, {
                    Log.d("mylogs", "Ошибка сохранения png-файла: ${it.message}")
                })
        }
    }
    fun saveImageToPNGFileCompletable(): Completable = Completable.create {
            emitter ->
        if (bitmap == null) {
            message = "Не загружена картинка для сохранения в png-файл"
            emitter.onError(IllegalStateException("Не загружена картинка для сохранения в png-файл"))
            return@create
        }
        /** Сохранение загруженной картинки в png-файл */
        bitmap?.let {
            /** Сохранение картинки в png-файл */
            bitmapToFile(it, Constants.NAME_PNG_FILE)
            emitter.onComplete()
        }
    }
    /** Получение разрешений на запись и считывание информации с телефона */
    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mainPresenter.getContext().checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                mainPresenter.showMessage("Разрешение на запись и считывание данных получено")
                true
            } else {
                mainPresenter.showMessage("Разрешение на запись и считывание данных отсутствует")
                ActivityCompat.requestPermissions(
                    mainPresenter as MainActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1)
                false
            }
        } else {
            mainPresenter.showMessage("Разрешение на запись и считывание данных получено")
            true
        }
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
            message = "Картинка успешно сохранена по адресу: $file"
            file
        } catch (e: Exception) {
            message = "Ошибка при сохранении png-файла: ${e.message}"
            file
        }
    }
    //region
}