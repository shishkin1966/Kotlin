package shishkin.sl.kodeinpsb.app.paginator


import java.util.*


/**
 * Класс, обеспечивающий постраничное считывание данных
 * Размер окна считываемых данных является постоянным
 * Объект требут внешнего запуска чтения данных - вызова функции hasData()
 */
abstract class AbsPermanentPaginator(private var listener: String, pageSize: Int = PAGE_SIZE) :
    AbsPaginator(listener, pageSize) {

    companion object {
        const val PAGE_SIZE = 20
    }

    override fun setPageSizes(initialPageSize: Int): List<Int> {
        var page = initialPageSize
        if (page <= 0) {
            page = PAGE_SIZE
        }
        val ps = ArrayList<Int>()
        ps.add(page)
        return ps
    }

    override fun afterResponse() {
    }
}