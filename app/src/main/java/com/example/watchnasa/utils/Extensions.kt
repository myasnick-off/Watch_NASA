package com.example.watchnasa.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import com.example.watchnasa.R

const val DURATION_500 = 500L

// метод отображения View-компонента
fun View.show(): View {
    if (visibility != View.VISIBLE)
        visibility = View.VISIBLE
    return this
}

// метод скрытия View-компонента
fun View.hide(): View {
    if (visibility != View.GONE)
        visibility = View.GONE
    return this
}

// метод отображения диалога с ошибкой загрузки контента
fun showErrorDialog(context: Context, action: DialogInterface.OnClickListener) {
    AlertDialog.Builder(context)
        .setTitle(R.string.loading_error)
        .setIcon(R.drawable.ic_baseline_error_24)
        .setPositiveButton(R.string.retry, action)
        .setNeutralButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
        .create()
        .show()
}
