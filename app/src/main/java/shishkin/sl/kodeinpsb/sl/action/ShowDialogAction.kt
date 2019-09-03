package shishkin.sl.kodeinpsb.sl.action

import microservices.shishkin.sl.ui.MaterialDialogExt
import shishkin.sl.kodeinpsb.R


class ShowDialogAction() : AbsAction(){
    private var id = -1
    private var title: String? = null
    private var message: String? = null
    private var buttonPositive = R.string.ok_upper
    private var buttonNegative = MaterialDialogExt.NO_BUTTON
    private var cancelable = false
    private var listener: String? = null


    constructor(id: Int, listener: String): this() {
        this.id = id
        this.listener = listener
    }

    constructor(id: Int, listener: String, title: String?, message: String): this(id, listener) {
        this.title = title
        this.message = message
    }

    fun getMessage(): String? {
        return message
    }

    fun getTitle(): String? {
        return title
    }

    fun getButtonPositive(): Int {
        return buttonPositive
    }

    fun getButtonNegative(): Int {
        return buttonNegative
    }

    fun isCancelable(): Boolean {
        return cancelable
    }

    fun getListener(): String? {
        return listener
    }

    fun setPositiveButton(button: Int): ShowDialogAction {
        buttonPositive = button
        return this
    }

    fun setNegativeButton(button: Int): ShowDialogAction {
        buttonNegative = button
        return this
    }

    fun setCancelable(cancelable: Boolean): ShowDialogAction {
        this.cancelable = cancelable
        return this
    }

    fun setMessage(message: String): ShowDialogAction {
        this.message = message
        return this
    }

    fun setTitle(title: String): ShowDialogAction {
        this.title = title
        return this
    }

    fun getId(): Int {
        return id
    }

}