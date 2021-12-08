package ru.geekbrains.popular.libraries.imageswork

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ru.geekbrains.popular.libraries.imageswork.databinding.ActivityMainBinding
import java.io.*

class MainActivity: AppCompatActivity() {
    /** ИСХОДНЫЕ ДАННЫЕ */ //region
    // binding
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!
    // bitmap
    private var myBitmap: Bitmap? = null
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** Установка слушателя на кнопку выбора jpg картинки на телефоне */
        binding.buttonLoadInitialImage.setOnClickListener {
            binding.initialImage.visibility = View.INVISIBLE
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(
                    Intent.createChooser(intent, "Выберите файл"),111)
        }

        /** Установка слушателя на кнопку сохранения картинки в png */
        binding.buttonSaveToPng.setOnClickListener {
            if (myBitmap == null) {
                Toast.makeText(this, "Не загружена картинка для сохранения в png.",
                    Toast.LENGTH_LONG).show()
                Log.d("mylogs", "Не загружена картинка для сохранения в png.")
            }
            // Сохранение картинки png-файл
            myBitmap?.let {
                bitmapToFile(it, "youPngImage.png")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == 111) && (resultCode == RESULT_OK)) {
            val selectedFile: Uri? = data?.data
            selectedFile?.let { selectedFile ->
                if (selectedFile.toString().substring(selectedFile.toString().length - 3,
                        selectedFile.toString().length).lowercase() == "jpg") {
                    Toast.makeText(this, "${selectedFile}", Toast.LENGTH_LONG).show()
                    Log.d("mylogs", "${selectedFile}")

                    /** Получение разрешения на запись информации */
                    isStoragePermissionGranted()

                    /** Отображение выбранной jpg картинки */
                    myBitmap = getBitmapFromUri(selectedFile, this)
                    myBitmap?.let { myBitmap ->
                        binding.initialImage.setImageBitmap(myBitmap)
                        binding.initialImage.visibility = View.VISIBLE
                        binding.buttonSaveToPng.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this, "Вы выбрали не jpg файл. Попробуйте ещё раз.",
                        Toast.LENGTH_LONG).show()
                    Log.d("mylogs", "Вы выбрали не jpg файл. Попробуйте ещё раз.")
                }
            }
        }
    }

    /** Получение разрешений на запись и считывание информации с телефона */
    fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("mylogs", "Permission is granted")
                true
            } else {
                Log.v("mylogs", "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("mylogs", "Permission is granted")
            true
        }
    }

    /** Получение bitmap картинки по uri-ссылке */
    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri?, context: Context): Bitmap? {
        uri?.let { uri ->
            val parcelFileDescriptor: ParcelFileDescriptor? =
                context.getContentResolver().openFileDescriptor(uri, "r")
            parcelFileDescriptor?.let {
                val fileDescriptor = it.fileDescriptor
                val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
                it.close()
                return image
            }
        }
        return null
    }


    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        var file: File? = null
        return try {
            // Сохранение в корневом каталоге Internal storage
            // Сохранение в Internal storage в папке приложения:
            // /storage/emulated/0/Android/data/ru.geekbrains.popular.libraries.less_1_homework/files/Pictures/
            file = File("${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}")

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
            Toast.makeText(this, "Картинка успешно сохранена по адресу: $file",
                Toast.LENGTH_LONG).show()
            Log.d("mylogs", "Картинка успешно сохранена по адресу: $file")
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file
        }
    }
}