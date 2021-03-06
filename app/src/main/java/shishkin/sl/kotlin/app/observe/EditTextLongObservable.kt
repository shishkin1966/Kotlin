package shishkin.sl.kotlin.app.observe

import android.text.Editable
import android.widget.EditText
import shishkin.sl.kotlin.common.left
import shishkin.sl.kotlin.common.toLong
import shishkin.sl.kotlin.common.token
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


class EditTextLongObservable(observer: Observer, view: EditText, delay: Long = 500) :
    EditTextObservable(observer, view, delay) {
    private var isEditing = false

    override fun afterTextChanged(s: Editable) {
        if (isEditing) return
        isEditing = true

        var str = s.toString().token("\\.", 1)
        str = str.left(10)
        val format = DecimalFormat("###,###")
        val customSymbol = DecimalFormatSymbols()
        customSymbol.decimalSeparator = '.'
        customSymbol.groupingSeparator = ' '
        format.decimalFormatSymbols = customSymbol
        val ss = format.format(str.toLong())
        if (ss != view.text.toString()) {
            view.setText(ss)
            view.setSelection(ss.length)
        }
        super.afterTextChanged(s)
        isEditing = false
    }
}
