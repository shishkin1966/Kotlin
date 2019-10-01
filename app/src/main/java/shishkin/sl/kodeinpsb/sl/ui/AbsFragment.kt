package shishkin.sl.kodeinpsb.sl.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import shishkin.sl.kodeinpsb.R
import shishkin.sl.kodeinpsb.common.ApplicationUtils
import shishkin.sl.kodeinpsb.sl.ISpecialistSubscriber
import shishkin.sl.kodeinpsb.sl.action.IAction
import shishkin.sl.kodeinpsb.sl.model.IModel
import shishkin.sl.kodeinpsb.sl.specialist.ApplicationSpecialist
import shishkin.sl.kodeinpsb.sl.state.IStateable
import shishkin.sl.kodeinpsb.sl.state.State
import shishkin.sl.kodeinpsb.sl.state.StateObservable
import java.util.*


abstract class AbsFragment : Fragment(), IFragment {

    private val stateObservable = StateObservable(State.STATE_CREATE)
    private lateinit var model: IModel
    private val actions = LinkedList<IAction>()

    override fun <T : IModel> getModel(): T {
        if (!::model.isInitialized) {
            model = createModel()
        }
        return model as T
    }

    override fun <T : IModel> setModel(model: T) {
        this.model = model
    }

    abstract fun createModel(): IModel

    override fun <V : View> findView(@IdRes id: Int): V? {
        val root = view
        return if (root != null) {
            ApplicationUtils.findView(root, id)
        } else null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setModel(createModel())

        stateObservable.setState(State.STATE_CREATE)
    }

    override fun onStart() {
        super.onStart()

        doActions()

        if (this is ISpecialistSubscriber) {
            ApplicationSpecialist.serviceLocator?.registerSpecialistSubscriber(this)
        }

        stateObservable.setState(State.STATE_READY)
    }

    override fun onDestroy() {
        super.onDestroy()

        stateObservable.setState(State.STATE_DESTROY)
        stateObservable.clear()

        if (this is ISpecialistSubscriber) {
            ApplicationSpecialist.serviceLocator?.unregisterSpecialistSubscriber(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stateObservable.setState(State.STATE_NOT_READY)
    }

    override fun getState(): Int {
        return stateObservable.getState()
    }

    override fun setState(state: Int) {}

    override fun stop() {
        if (activity != null) {
            (activity as AppCompatActivity).onBackPressed()
        }
    }

    override fun isValid(): Boolean {
        return getState() != State.STATE_DESTROY
    }

    override fun getRootView(): View? {
        val view = findView<View>(R.id.root)
        return view ?: getView()
    }

    override fun addStateObserver(stateable: IStateable) {
        stateObservable.addObserver(stateable)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permissions[i])
            } else if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                onPermissionDenied(permissions[i])
            }
        }
    }

    override fun onPermissionGranted(permission: String) {}

    override fun onPermissionDenied(permission: String) {}

    override fun addAction(action: IAction) {
        when (getState()) {
            State.STATE_DESTROY -> return

            State.STATE_CREATE, State.STATE_NOT_READY -> {
                actions.add(action)
                return
            }

            else -> {
                actions.add(action)
                doActions()
            }
        }
    }

    private fun doActions() {
        val deleted = ArrayList<IAction>()
        for (i in actions.indices) {
            if (getState() != State.STATE_READY) {
                break
            }
            onAction(actions[i])
            deleted.add(actions[i])
        }
        for (event in deleted) {
            actions.remove(event)
        }
    }

}
