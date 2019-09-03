package shishkin.sl.kodeinpsb.sl.message

import shishkin.sl.kodeinpsb.sl.action.AbsAction
import java.util.*


abstract class AbsMessage() : AbsAction(), IMessage {
    private var id = -1
    private var address: String? = null
    private var copyTo: LinkedList<String> = LinkedList()
    private var endTime: Long = -1

    constructor(address: String): this() {
        this.address = address
    }

    constructor(message: IMessage) : this() {
            address = message.getAddress()
            copyTo.addAll(message.getCopyTo())
            setMessageId(message.getMessageId())
            endTime = message.getEndTime()
    }

    override fun getMessageId(): Int {
        return id
    }

    override fun setMessageId(id: Int): IMessage {
        this.id = id
        return this
    }

    override fun getAddress(): String? {
        return address
    }

    override fun setAddress(address: String): IMessage {
        this.address = address
        return this
    }

    override fun getCopyTo(): List<String> {
        return copyTo
    }

    override fun setCopyTo(copyTo: List<String>): IMessage {
        this.copyTo.clear()
        this.copyTo.addAll(copyTo)
        return this
    }

    override fun contains(address: String): Boolean {
        if (address.isNullOrEmpty()) {
            return false
        }

        if (address.equals(this.address)) {
            return true
        }

        for (copyto in copyTo) {
            if (copyto.equals(address)) {
                return true
            }
        }
        return false
    }

    override fun getEndTime(): Long {
        return endTime
    }

    override fun setEndTime(endTime: Long): IMessage {
        this.endTime = endTime
        return this
    }

    override fun isCheckDublicate(): Boolean {
        return false
    }

    override fun isSticky(): Boolean {
        return false
    }

}