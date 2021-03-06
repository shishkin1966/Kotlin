package shishkin.sl.kotlin.app.screen.digital_currencies

import com.google.common.collect.Collections2
import shishkin.sl.kotlin.app.data.Ticker
import shishkin.sl.kotlin.common.PreferencesUtils
import shishkin.sl.kotlin.sl.provider.ApplicationProvider
import java.util.*
import kotlin.collections.ArrayList


class TickerData {
    companion object {
        const val TickerDataFilter = "TickerDataFilter"
    }

    var tickers: ArrayList<Ticker>? = null
    var filter: String? = null


    init {
        filter = loadFilter()
    }

    fun getData(): List<Ticker> {
        if (tickers == null) return ArrayList()

        if (filter.isNullOrEmpty()) {
            tickers!!.sortWith(Comparator { o1, o2 -> o1.symbol!!.compareTo(o2.symbol!!, true) })
            return tickers!!
        } else {
            val list: ArrayList<Ticker> = ArrayList()
            list.addAll(Collections2.filter(tickers!!) { input ->
                input?.name?.contains(filter!!, true)!!
            })
            list.sortWith(Comparator { o1, o2 -> o1.symbol!!.compareTo(o2.symbol!!, true) })
            return list
        }
    }

    fun saveFilter() {
        PreferencesUtils.putString(ApplicationProvider.appContext, TickerDataFilter, filter)
    }

    private fun loadFilter(): String? {
        return PreferencesUtils.getString(ApplicationProvider.appContext, TickerDataFilter)
    }
}
