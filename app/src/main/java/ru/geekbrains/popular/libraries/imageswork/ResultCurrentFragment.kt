package ru.geekbrains.popular.libraries.imageswork

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color.red
import android.os.Bundle
import android.os.ParcelFileDescriptor.open
import android.renderscript.ScriptGroup
import android.system.Os.open
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import ru.geekbrains.popular.libraries.imageswork.databinding.FragmentResultCurrentBinding
import java.io.IOException
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.DatagramChannel.open
import java.nio.channels.FileChannel.open
import java.nio.channels.Pipe.open
import java.nio.channels.ServerSocketChannel.open
import java.nio.channels.SocketChannel.open
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream

class ResultCurrentFragment: Fragment() {

    //region ИСХОДНЫЕ ДАННЫЕ
    // Подключение binding
    private var bindingReal: FragmentResultCurrentBinding? = null
    private val bindingNotReal: FragmentResultCurrentBinding
        get() {
            return bindingReal!!
        }
    //endregion

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingReal = FragmentResultCurrentBinding.inflate(inflater, container, false)
        return bindingNotReal.root
    }

    fun getBinding(): FragmentResultCurrentBinding? {
        return bindingReal
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderData()
    }

    private fun renderData() {

        bindingReal?.let { it ->
            it.resultImageFeels.setOnClickListener {
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
                activity?.let { fragmentActivity ->
                    fragmentActivity.startActivityForResult(
                        Intent.createChooser(intent, "Выберите файл"),111)
                }
            }
        }

        // Get the bitmap from assets and display into image view
//        val bitmap = assetsToBitmap("red.png")
//        // If bitmap is not null
//        bitmap?.let {
//            bindingReal?.let { bindingReal ->
//                bindingReal.imageViewBitmap.setImageBitmap(bitmap)
//            }
//        }

        // СПОСОБ ЗАГРУЗКИ РАСТРОВОГО JPG, PNG
//        bindingReal?.resultImageFeels?.let {
//            Glide
//                .with(it)
////                .load("https://c1.staticflickr.com/1/186/31520440226_175445c41a_b.jpg")
//                // ИЛИ
//                .load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
//                .into(it)
//        }

        // СПОСОБ ЗАГРУЗКИ РАСТРОВОГО JPG, PNG

        // Рисование обысной звезды
        bindingReal?.let {
            Picasso
                .get()
//                .load("https://c1.staticflickr.com/1/186/31520440226_175445c41a_b.jpg")
                // ИЛИ
//                .load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
                .load(R.drawable.red)
//                .error(R.drawable.red)
    //            .transform(CircleTransformation())
    //            .transform(starTransformation)
    //            .rotate(90f)
                .into(it.resultImageFeels)
        }

        // Рисование звезды с изменёнными радиусами
//        Picasso
//            .get()
//            .load(R.drawable.red_inverted)
//            .transform(starTransformation)
//            .into(bindingReal?.resultImageFeelsInvert)

        // СПОСОБ ЗАГРУЗКИ РАСТРОВОГО PNG
//        bindingReal?.resultImageFeels?.load("https://freepngimg.com/thumb/city/36275-3-city-hd.png")
//        bindingReal?.resultImageFeels?.load("https://c1.staticflickr.com/1/186/31520440226_175445c41a_b.jpg")

    }

//    // Method to get a bitmap from assets
//    private fun assetsToBitmap(fileName:String): Bitmap?{
//        return try{
//            val stream = assets.open(fileName)
//            BitmapFactory.decodeStream(stream)
//        }catch (e: IOException){
//            e.printStackTrace()
//            null
//        }
//    }

    // Удаление binding при закрытии фрагмента
    override fun onDestroy() {
        super.onDestroy()
        bindingReal = null
    }
}