package shishkin.sl.kodeinpsb.sl.request

import shishkin.sl.kodeinpsb.common.ApplicationUtils
import shishkin.sl.kodeinpsb.sl.data.ExtError
import shishkin.sl.kodeinpsb.sl.data.ExtResult
import java.lang.ref.WeakReference
import java.util.*


abstract class AbsResultRequest<T> : AbsRequest, IResultRequest {

    private lateinit var _ref: WeakReference<IResponseListener>
    private var _copyTo: List<String> = ArrayList<String>()
    private var _error: ExtError? = null

    private constructor() : super()

    constructor(owner: IResponseListener) : this() {
        _ref = WeakReference(owner)
    }

    override fun getOwner(): IResponseListener? {
        return _ref.get()
    }

    override fun validate(): Boolean {
        return _ref.get() != null && _ref.get()!!.validate() && !isCancelled()
    }

    override fun response() {
        if (validate()) {
            ApplicationUtils.runOnUiThread(
                Runnable {
                    getOwner()?.response(
                        ExtResult().setName(getName()).setData(
                            getData()
                        ).setError(getError())
                    )
                }
            )
        }
    }

    override fun getCopyTo(): List<String> {
        return _copyTo
    }

    override fun setCopyTo(copyTo: List<String>) {
        _copyTo = copyTo
    }

    abstract fun getData(): T?

    override fun getError(): ExtError? {
        return _error
    }

    override fun setError(error: ExtError?) {
        _error = error
    }

}