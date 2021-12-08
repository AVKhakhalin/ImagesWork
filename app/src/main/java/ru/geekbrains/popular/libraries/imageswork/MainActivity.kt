package ru.geekbrains.popular.libraries.imageswork

import android.Manifest
import android.R.attr
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import ru.geekbrains.popular.libraries.imageswork.databinding.ActivityMainBinding
import java.io.*


class MainActivity: AppCompatActivity() {
    //region ИСХОДНЫЕ ДАННЫЕ
    // binding
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!
    private var path: String = ""
    // fragment
    private var resultCurrentFragment: ResultCurrentFragment = ResultCurrentFragment()
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentResultContainer.id, resultCurrentFragment)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 111 && resultCode == RESULT_OK) {
            val selectedFile: Uri? = data?.data //The uri with the location of the file
            Toast.makeText(this, "${selectedFile}", Toast.LENGTH_SHORT).show()
            Log.d("mylogs", "${selectedFile}")

            isStoragePermissionGranted()

            // Загрузка выбранной картинки в ImageView
//            binding.fragmentResultContainer.findViewById<ImageView>(R.id.result_image_feels).setImageURI(selectedFile)
//            resultCurrentFragment.getBinding()?.let {
//                it.resultImageFeels.setImageURI(selectedFile)
//            }

//            val imgFile: File = File(selectedFile.toString())
//            if (imgFile.exists()) {
//                val myBitmap: Bitmap = BitmapFactory.decodeFile(imgFile.toString())
//                //Drawable d = new BitmapDrawable(getResources(), myBitmap);
////                val myImage = findViewById<View>(R.id.imageviewTest) as ImageView
////                myImage.setImageBitmap(myBitmap)
//                resultCurrentFragment.getBinding()?.let {
//                    it.imageViewBitmap.setImageBitmap(myBitmap)
//                }
//            }

            val myBitmap: Bitmap? = getBitmapFromUri(selectedFile, this)
            myBitmap?.let { bitmap ->
                resultCurrentFragment.getBinding()?.let {
                    it.resultImageFeels.setImageBitmap(bitmap)
                }
            }

            // Конвертирование jpg в png
            myBitmap?.let { myBitmap ->
                val compressedBitmap = myBitmap.compress(Bitmap.CompressFormat.PNG)
                //val compressedBitmap = bitmap.compress(Bitmap.CompressFormat.WEBP)
                //val compressedBitmap = bitmap.compress(Bitmap.CompressFormat.JPEG)
                //val compressedBitmap = bitmap.compress(Bitmap.CompressFormat.JPEG, 10)
                //val compressedBitmap = bitmap.compress(quality = 10) // Compress only
                resultCurrentFragment.getBinding()?.let {
                    it.imageViewBitmap.setImageBitmap(compressedBitmap)
                }

                // Сохранение png-файла
                    val pngFile: String = "${selectedFile.toString().substring(0, selectedFile.toString().length - 3)}png"
                    val pngPath: String = selectedFile.toString().substring(0, selectedFile.toString().lastIndexOf("/"))
                    myBitmap?.let {
                        bitmapToFile(it, "proba3.png")
                    }
            }

//            var path: String = selectedFile.toString().substring(0, selectedFile.toString().lastIndexOf("/"))
            path = Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).absolutePath
//            path = path.toString().substring(0, path.toString().lastIndexOf("/"))
            Log.d("mylogs", "$path")
//            val path = Environment.getExternalStorageDirectory().toString() + selectedFile?.path
            Log.d("mylogs", "Path: $path")
            val directory = File(path)
            val files = directory.listFiles()
            files?.let {
                Log.d("mylogs", "Size: " + it.size)
                for (i in it.indices) {
                    Log.d("mylogs", "FileName:" + it[i].name)
                }
            }

            writeFileSD()


//            val filename = "file.txt"
//            val outputString = "Hello world!"
//
//            try {
//                val outputStream = openFileOutput(filename, Context.MODE_PRIVATE)
//                outputStream.write(outputString.toByteArray())
//                outputStream.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//
//            try {
//                val inputStream = openFileInput(filename)
//                val r = BufferedReader(InputStreamReader(inputStream))
//                val total = StringBuilder()
//                var line: String?
//                while (r.readLine().also { line = it } != null) {
//                    total.append(line)
//                }
//                r.close()
//                inputStream.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }

//            writeFileSD()
//
//            var fos: FileOutputStream? = null
//            try {
//                val text = "Проба"
//
//                fos = openFileOutput("${selectedFile?.path}/content.txt", MODE_PRIVATE)
//                fos.write(text.toByteArray())
//                Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show()
//            } catch (ex: IOException) {
//                Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
//            } finally {
//                try {
//                    if (fos != null) fos.close()
//                } catch (ex: IOException) {
//                    Toast.makeText(this, ex.message, Toast.LENGTH_SHORT).show()
//                }
//            }
        }
    }

    fun writeFileSD() {
        // проверяем доступность SD
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Log.d("mylogs", "SD-карта не доступна: " + Environment.getExternalStorageState())
            return
        }
        // получаем путь к SD
//        var sdPath = Environment.getExternalStorageDirectory()

//        val sdcard = File(Environment.getExternalStorageDirectory(), "newFolder")
//        sdcard.mkdir()
//        val file = File(sdcard, "/ans.txt") //line2

        var sdPath: String = path
        // добавляем свой каталог к пути
//        sdPath = File(sdPath.absolutePath + "/" + "proba")
//        sdPath = File(sdPath)
        // создаем каталог
//        sdPath.mkdirs()
        // формируем объект File, который содержит путь к файлу
        val sdFile = File(sdPath, "proba.txt")
//        val sdFile = file
        Log.d("mylogs", "file = $sdFile")
        try {
            // открываем поток для записи
            val bw = BufferedWriter(FileWriter(sdFile))
            // пишем данные
            bw.write("Содержимое файла на SD")
            // закрываем поток
            bw.close()
            Log.d("mylogs", "Файл записан на SD: " + sdFile.absolutePath)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

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

    // Получение bitmap картинки по uri-ссылке
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


    // Extension function to compress and change bitmap image format programmatically
    fun Bitmap.compress(format:Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG, quality:Int = 100):Bitmap{
        // Initialize a new ByteArrayStream
        val stream = ByteArrayOutputStream()

        /*
            **** reference source developer.android.com ***

            public boolean compress (Bitmap.CompressFormat format, int quality, OutputStream stream)
                Write a compressed version of the bitmap to the specified outputstream.
                If this returns true, the bitmap can be reconstructed by passing a
                corresponding inputstream to BitmapFactory.decodeStream().

                Note: not all Formats support all bitmap configs directly, so it is possible
                that the returned bitmap from BitmapFactory could be in a different bitdepth,
                and/or may have lost per-pixel alpha (e.g. JPEG only supports opaque pixels).

                Parameters
                format : The format of the compressed image
                quality : Hint to the compressor, 0-100. 0 meaning compress for small size,
                    100 meaning compress for max quality. Some formats,
                    like PNG which is lossless, will ignore the quality setting
                stream: The outputstream to write the compressed data.

                Returns
                    true if successfully compressed to the specified stream.


            Bitmap.CompressFormat
                Specifies the known formats a bitmap can be compressed into.

                    Bitmap.CompressFormat  JPEG
                    Bitmap.CompressFormat  PNG
                    Bitmap.CompressFormat  WEBP
        */

        // Compress the bitmap with JPEG format and quality 50%
        this.compress(
            format,
            quality,
            stream
        )

        val byteArray = stream.toByteArray()

        // Finally, return the compressed bitmap
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun bitmapToFile(bitmap: Bitmap, fileNameToSave: String): File? {
        var file: File? = null
        return try {
            // Сохранение в корневом каталоге Internal storage
//            file = File("${Environment.getExternalStorageDirectory().toString()}")
            // /storage/emulated/0/Android/data/ru.geekbrains.popular.libraries.less_1_homework/files/storage/emulated/0/
//            file = File("${getExternalFilesDir(Environment.getExternalStorageDirectory().toString())}")
            // Сохранение в Internal storage в папке приложения:
            // /storage/emulated/0/Android/data/ru.geekbrains.popular.libraries.less_1_homework/files/Pictures/
//            file = File("${getExternalFilesDir(Environment.DIRECTORY_PICTURES)}")
            // Сохранение в Internal storage в папке приложения:
            // /storage/emulated/0/Android/data/ru.geekbrains.popular.libraries.less_1_homework/files/DCIM/
            file = File("${getExternalFilesDir(Environment.DIRECTORY_DCIM)}")

            // Сохранение в Internal storage:
            // Создать директорию, если она ещё не создана
            if (!file.exists()) {
                file.mkdirs()
            }

            file = File(file, "${File.separator}$fileNameToSave")
            Log.d("mylogs", "!!!!: $file")
            file.createNewFile()

            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }
}