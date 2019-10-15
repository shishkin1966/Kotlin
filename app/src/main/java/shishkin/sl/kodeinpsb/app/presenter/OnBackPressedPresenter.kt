package shishkin.sl.kodeinpsb.app.presenter

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import shishkin.sl.kodeinpsb.R
import shishkin.sl.kodeinpsb.app.ApplicationSingleton
import shishkin.sl.kodeinpsb.app.provider.UseCasesProvider
import shishkin.sl.kodeinpsb.app.screen.accounts.AccountsFragment
import shishkin.sl.kodeinpsb.sl.action.ApplicationAction
import shishkin.sl.kodeinpsb.sl.action.ShowSnackbarAction
import shishkin.sl.kodeinpsb.sl.presenter.AbsPresenter
import shishkin.sl.kodeinpsb.sl.provider.ActivityUnion
import shishkin.sl.kodeinpsb.sl.provider.ApplicationProvider
import java.util.*


class OnBackPressedPresenter : AbsPresenter() {
    companion object {
        const val NAME = "OnBackPressedPresenter"
    }

    private var doubleBackPressedOnce = false
    private var timer: Timer? = null

    override fun getName(): String {
        return NAME
    }

    fun onClick(): Boolean {
        val fragment =
            ApplicationSingleton.instance.getActivityUnion().getCurrentFragment<Fragment>()
        if (fragment !is AccountsFragment) {
            return false
        }
        if (isValid()) {
            if (!doubleBackPressedOnce) {
                val context = ApplicationProvider.appContext
                doubleBackPressedOnce = true
                val union =
                    ApplicationProvider.serviceLocator?.get<ActivityUnion>(
                        ActivityUnion.NAME
                    )
                if (union != null) {
                    union.getCurrentSubscriber()?.addAction(
                        ShowSnackbarAction(context.getString(R.string.double_back_pressed)).setDuration(
                            Snackbar.LENGTH_SHORT
                        ).setAction(ApplicationProvider.appContext.getString(R.string.exit))
                    )
                }
                startTimer()
                return true
            } else {
                ApplicationSingleton.instance.getUseCase().addAction(
                    ApplicationAction(
                        UseCasesProvider.OnExitAction
                    )
                )
                return true
            }
        }
        return false
    }

    private fun startTimer() {
        if (timer != null) {
            stopTimer()
        }
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                doubleBackPressedOnce = false
            }
        }, 2000)
    }

    private fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        stopTimer()
    }

    override fun isRegister(): Boolean {
        return false
    }
}
