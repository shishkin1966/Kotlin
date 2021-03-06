package shishkin.sl.kotlin.app

import android.widget.Toast
import microservices.shishkin.example.net.NetApi
import shishkin.sl.kotlin.app.db.Dao
import shishkin.sl.kotlin.app.db.Db
import shishkin.sl.kotlin.app.provider.DbProvider
import shishkin.sl.kotlin.app.provider.ILocationUnion
import shishkin.sl.kotlin.app.provider.LocationUnion
import shishkin.sl.kotlin.app.provider.NetProvider
import shishkin.sl.kotlin.app.provider.notification.INotificationProvider
import shishkin.sl.kotlin.app.provider.notification.NotificationProvider
import shishkin.sl.kotlin.common.ApplicationUtils
import shishkin.sl.kotlin.sl.IProvider
import shishkin.sl.kotlin.sl.IProviderSubscriber
import shishkin.sl.kotlin.sl.IRouterProvider
import shishkin.sl.kotlin.sl.message.IMessage
import shishkin.sl.kotlin.sl.observe.ObjectObservable
import shishkin.sl.kotlin.sl.observe.ScreenObservableSubscriber
import shishkin.sl.kotlin.sl.presenter.IModelPresenter
import shishkin.sl.kotlin.sl.provider.*
import shishkin.sl.kotlin.sl.request.IRequest
import shishkin.sl.kotlin.sl.task.CommonExecutor
import shishkin.sl.kotlin.sl.task.DbExecutor
import shishkin.sl.kotlin.sl.ui.IActivity


object ApplicationSingleton {
    val instance = App()
}

class App : ApplicationProvider() {

    private val screenObservableSubscriber = ScreenObservableSubscriber()

    override fun onCreate() {
        super.onCreate()

        ServiceLocatorSingleton.instance.start()

        serviceLocator = ServiceLocatorSingleton.instance

        ServiceLocatorSingleton.instance.registerSubscriber(screenObservableSubscriber)
    }

    fun onScreenOn() {
        ApplicationUtils.showToast(
            appContext,
            "Screen on",
            Toast.LENGTH_SHORT,
            ApplicationUtils.MESSAGE_TYPE_INFO
        )
    }

    fun onScreenOff() {
    }

    fun <C : IProvider> get(name: String): C? {
        return serviceLocator?.get(name)
    }

    fun onError(source: String, message: String?, isDisplay: Boolean) {
        val union = serviceLocator?.get<IErrorProvider>(ErrorProvider.NAME)
        union?.onError(source, message, isDisplay)
    }

    fun onError(source: String, e: Exception) {
        val union = serviceLocator?.get<IErrorProvider>(ErrorProvider.NAME)
        union?.onError(source, e)
    }

    fun <C : IModelPresenter> getPresenter(name: String): C? {
        val union = serviceLocator?.get<IPresenterUnion>(PresenterUnion.NAME)
        return union?.getPresenter(name)
    }

    var dbProvider: IDbProvider
        get() = get(DbProvider.NAME)!!
        private set(value) {}

    var observableProvider: IObservableUnion
        get() = get(ObservableUnion.NAME)!!
        private set(value) {}

    var activityProvider: IActivityUnion
        get() = get(ActivityUnion.NAME)!!
        private set(value) {}

    var locationProvider: ILocationUnion
        get() = get(LocationUnion.NAME)!!
        private set(value) {}

    var cacheProvider: ICacheProvider
        get() = get(CacheProvider.NAME)!!
        private set(value) {}

    fun cancelRequests(name: String) {
        get<CommonExecutor>(CommonExecutor.NAME)!!.cancelRequests(name)
    }

    fun execute(request: IRequest) {
        get<CommonExecutor>(CommonExecutor.NAME)!!.execute(request)
    }

    fun executeDb(request: IRequest) {
        get<DbExecutor>(DbExecutor.NAME)!!.execute(request)
    }

    var notificationProvider: INotificationProvider
        get() = get(NotificationProvider.NAME)!!
        private set(value) {}

    var routerProvider: IRouterProvider
        get() = activityProvider.getActivity<IActivity>() as IRouterProvider
        private set(value) {}

    fun addNotMandatoryMessage(message: IMessage) {
        get<IMessengerUnion>(MessengerUnion.NAME)!!.addNotMandatoryMessage(message)
    }

    fun registerSubscriber(subscriber: IProviderSubscriber): Boolean {
        return serviceLocator!!.registerSubscriber(subscriber)
    }

    fun unregisterSubscriber(subscriber: IProviderSubscriber): Boolean {
        return serviceLocator!!.unregisterSubscriber(subscriber)
    }

    fun onChange(observable: String, obj: Any) {
        observableProvider.getObservable(observable)?.onChange(obj)
    }

    fun onChange(name: String) {
        ApplicationSingleton.instance.observableProvider.getObservable(ObjectObservable.NAME)
            ?.onChange(name)
    }

    fun getDao(): Dao {
        return dbProvider.getDb<Db>()!!.getDao()
    }

    fun getApi(): NetApi {
        return ApplicationSingleton.instance.get<NetProvider>(NetProvider.NAME)!!.getApi()
    }
}
