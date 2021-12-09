package ru.geekbrains.popular.libraries.imageswork.logic.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import ru.geekbrains.popular.libraries.imageswork.R
import ru.geekbrains.popular.libraries.imageswork.logic.Logic
import ru.geekbrains.popular.libraries.imageswork.logic.MainPresenter

class CancelFileSaveDialogFragment(
    private val mainPresenter: MainPresenter
): DialogFragment(), DialogInterface.OnClickListener {
    /** ЗАДАНИЕ ПЕРЕМЕННЫХ */ //region
    // buttonNo
    private var buttonNo: Button? = null
    //endregion

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.dialog_fragment_cancel_file_save, null)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        buttonNo = view.findViewById(R.id.way_input_button_cancel)
        buttonNo?.let { buttonNo ->
            buttonNo.setOnClickListener(View.OnClickListener { view: View ->
                onNo(view)
            })
        }
    }

    // Результат нажатия на кнопку отмены действия
    private fun onNo(view: View) {
        mainPresenter.closeCancelFileSaveDialogFragmentAndCancelSave()
        dismiss()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {}
}