package shishkin.sl.kotlin.sl.request

import shishkin.sl.kotlin.BuildConfig
import shishkin.sl.kotlin.sl.task.RequestThreadPoolExecutor


abstract class AbsRequest() : IRequest {
    private var rank: Int = Rank.MIDDLE_RANK
    private var isCanceled = false
    private var id = 0
    private var version: String = "Android=" + BuildConfig.VERSION_NAME

    override fun getRank(): Int {
        return rank
    }

    override fun setRank(rank: Int): IRequest {
        this.rank = rank
        return this
    }


    override fun isCancelled(): Boolean {
        return isCanceled
    }

    override fun setCanceled() {
        isCanceled = true
    }

    override fun isValid(): Boolean {
        return !isCancelled()
    }

    override fun getId(): Int {
        return id
    }

    override fun setId(id: Int): IRequest {
        this.id = id
        return this
    }

    override operator fun compareTo(other: IRequest): Int {
        return other.getRank() - getRank()
    }

    override fun getAction(oldRequest: IRequest): Int {
        return RequestThreadPoolExecutor.ACTION_DELETE
    }

    override fun getVersion(): String {
        return version
    }

    override fun setVersion(version: String): IRequest {
        this.version = version
        return this
    }


}
