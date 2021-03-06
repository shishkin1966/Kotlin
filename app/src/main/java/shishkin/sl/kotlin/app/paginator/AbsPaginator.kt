package shishkin.sl.kotlin.app.paginator


import shishkin.sl.kotlin.app.ApplicationSingleton
import shishkin.sl.kotlin.common.recyclerview.IPageListener
import shishkin.sl.kotlin.sl.IProvider
import shishkin.sl.kotlin.sl.data.ExtResult
import shishkin.sl.kotlin.sl.message.IMessage
import shishkin.sl.kotlin.sl.message.ResultMessage
import shishkin.sl.kotlin.sl.provider.IMessengerSubscriber
import shishkin.sl.kotlin.sl.provider.MessengerUnion
import shishkin.sl.kotlin.sl.request.IResponseListener
import shishkin.sl.kotlin.sl.request.Request
import shishkin.sl.kotlin.sl.state.IStateListener
import shishkin.sl.kotlin.sl.state.State
import shishkin.sl.kotlin.sl.state.StateObserver
import java.util.*

/**
 * Класс, обеспечивающий постраничное считывание данных
 * Размер окна считываемых данных является нарастающим
 * Объект самостоятельно запускает запросы для получения всех данных
 */
abstract class AbsPaginator(private var listener: String, pageSize: Int = PAGE_SIZE) :
    IResponseListener, IMessengerSubscriber, IStateListener, IPageListener {

    companion object {
        const val PAGE_SIZE = 20
    }

    private var currentPageSize = 0
    private var currentPosition = 0
    private var pageSizes: List<Int>
    private var isEof = false
    private val lifecycle = StateObserver(this)

    init {
        pageSizes = setPageSizes(pageSize)
    }

    private fun getNextPageSize(): Int {
        if (pageSizes.isEmpty()) return PAGE_SIZE

        for (i in pageSizes.indices) {
            if (pageSizes[i] > currentPageSize) {
                return pageSizes[i]
            }
        }
        return pageSizes[pageSizes.size - 1]
    }

    open fun setPageSizes(initialPageSize: Int): List<Int> {
        var page = initialPageSize
        if (page <= 0) {
            page = PAGE_SIZE
        }
        val ps = ArrayList<Int>()
        ps.add(page)
        ps.add(page * 2)
        ps.add(page * 4)
        return ps
    }

    override fun hasData() {
        if (!isEof && isValid()) {
            currentPageSize = getNextPageSize()
            getData()
        }
    }

    private fun getData() {
        ApplicationSingleton.instance.execute(
            getRequest(
                getName(),
                currentPosition,
                currentPageSize
            )
        )
    }

    override fun response(result: ExtResult) {
        if (!isEof && isValid()) {
            if (!result.hasError()) {
                val list = result.getData() as List<*>
                if (list.isNotEmpty()) {
                    currentPosition += list.size
                } else {
                    isEof = true
                    result.setOrder(ExtResult.LAST)
                }
            }
            ApplicationSingleton.instance.addNotMandatoryMessage(ResultMessage(listener, result))
            afterResponse()
        }
    }

    open fun afterResponse() {
        hasData()
    }

    abstract fun getRequest(name: String, currentPosition: Int, pageSize: Int): Request

    override fun isValid(): Boolean {
        return getState() == State.STATE_READY
    }

    override fun read(message: IMessage) {
    }

    override fun getProviderSubscription(): List<String> {
        return listOf(MessengerUnion.NAME)
    }

    override fun onStopProvider(provider: IProvider) {
    }

    override fun getState(): Int {
        return lifecycle.getState()
    }

    override fun setState(state: Int) {
        lifecycle.setState(state)
    }

    override fun onCreateView() {
    }

    override fun onReadyView() {
        ApplicationSingleton.instance.registerSubscriber(this)
        hasData()
    }

    override fun onDestroyView() {
        cancel()
        ApplicationSingleton.instance.unregisterSubscriber(this)
    }

    private fun cancel() {
        ApplicationSingleton.instance.cancelRequests(getName())
    }

}
