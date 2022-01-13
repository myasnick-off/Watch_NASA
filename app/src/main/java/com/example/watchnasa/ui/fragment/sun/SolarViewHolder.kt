package com.example.watchnasa.ui.fragment.sun

import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.watchnasa.R
import com.example.watchnasa.repository.dto.SolarFlareResponseData

abstract class SolarViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(data: SolarFlareResponseData)

    // метод форматирования текста для текстовых полей Вьюхолдера
    fun spanText(data: String?, textSize: Float): SpannableString {
        data?.let {
            val spannableText = SpannableString(data).apply {
                setSpan(RelativeSizeSpan(textSize), 0, this.length, 0)
            }
            return spannableText
        }
        return SpannableString("")
    }

    // метод форматирования текста заголовков Вьюхолдера
    fun spanTitle(textView: TextView, textSize: Float) {
        val textColor = ContextCompat.getColor(itemView.context, R.color.explanation_title_color)
        val spannableText = SpannableString(textView.text).apply {
            setSpan(ForegroundColorSpan(textColor), 0, this.length, 0)
            setSpan(RelativeSizeSpan(textSize), 0, this.length, 0)
        }
        textView.text = spannableText
    }
}