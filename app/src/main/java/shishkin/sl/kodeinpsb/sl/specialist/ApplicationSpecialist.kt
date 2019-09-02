package shishkin.sl.kodeinpsb.sl.specialist

import android.app.Application
import android.content.Context
import shishkin.sl.kodeinpsb.BuildConfig
import shishkin.sl.kodeinpsb.sl.ISpecialist


open class ApplicationSpecialist : Application(), IApplicationSpecialist {
    private var isExit = false

    companion object {
        val instance = ApplicationSpecialist()
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
    }

    override fun isPersistent(): Boolean {
        return true
    }

    override fun onUnRegister() {
    }

    override fun onRegister() {
    }

    override fun stop() {
    }

    override fun getName(): String {
        return BuildConfig.APPLICATION_ID
    }

    override fun validate(): Boolean {
        return !isExit
    }

    override fun compareTo(other: ISpecialist): Int {
        return if (other is IApplicationSpecialist) 0 else 1
    }

    override fun isExit(): Boolean {
        return isExit
    }

    /**
     * выйти из приложеня
     */
    override fun exit() {
        stop()
    }
}