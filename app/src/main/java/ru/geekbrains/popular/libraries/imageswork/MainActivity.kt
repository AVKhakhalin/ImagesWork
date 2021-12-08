package ru.geekbrains.popular.libraries.imageswork

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.geekbrains.popular.libraries.imageswork.databinding.ActivityMainBinding
import ru.geekbrains.popular.libraries.imageswork.logic.Constants
import ru.geekbrains.popular.libraries.imageswork.logic.MainPresenter
import ru.geekbrains.popular.libraries.imageswork.logic.PresenterView
import java.io.*

class MainActivity: AppCompatActivity(), PresenterView {
    /** ИСХОДНЫЕ ДАННЫЕ */ //region
    // binding
    private var _binding: ActivityMainBinding? = null
    private val binding
        get() = _binding!!
    // Presenter
    private val presenter: MainPresenter = MainPresenter(this@MainActivity)
    //endregion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /** Установка слушателя на кнопку выбора jpg картинки на телефоне */
        binding.buttonLoadInitialImage.setOnClickListener {
            binding.initialImage.visibility = View.INVISIBLE
            binding.buttonSaveToPng.visibility = View.INVISIBLE
            binding.buttonCancelSaveToPng.visibility = View.INVISIBLE
            presenter.chooseImageOnPhone()
        }

        /** Установка слушателя на кнопку сохранения картинки в png */
        binding.buttonSaveToPng.setOnClickListener {
            presenter.saveImageToPNG()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ((requestCode == Constants.REQUEST_CODE) && (resultCode == RESULT_OK)) {
            presenter.loadAndShowImage(data)
        }
    }

    override fun showImage(bitmap: Bitmap) {
        /** Отображение выбранной jpg картинки и кнопок с шагами 2 и 3 */
        binding.initialImage.setImageBitmap(bitmap)
        binding.initialImage.visibility = View.VISIBLE
        binding.buttonSaveToPng.visibility = View.VISIBLE
        binding.buttonCancelSaveToPng.visibility = View.VISIBLE
    }

    override fun showToastLogMessage(newText: String) {
        Toast.makeText(this, newText, Toast.LENGTH_LONG).show()
        Log.d(Constants.LOG_TAG, newText)
    }
}