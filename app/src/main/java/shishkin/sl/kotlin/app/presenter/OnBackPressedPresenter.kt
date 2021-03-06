package shishkin.sl.kotlin.app.presenter

import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import shishkin.sl.kotlin.R
import shishkin.sl.kotlin.app.ApplicationSingleton
import shishkin.sl.kotlin.app.provider.Providers
import shishkin.sl.kotlin.app.screen.accounts.AccountsFragment
import shishkin.sl.kotlin.sl.action.ShowSnackbarAction
import shishkin.sl.kotlin.sl.presenter.AbsPresenter
import shishkin.sl.kotlin.sl.provider.ApplicationProvider
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
            ApplicationSingleton.instance.activityProvider.getCurrentFragment<Fragment>()
        if (fragment !is AccountsFragment) {
            return false
        }
        if (isValid()) {
            if (!doubleBackPressedOnce) {
                doubleBackPressedOnce = true
                ApplicationSingleton.instance.activityProvider.getCurrentSubscriber()?.addAction(
                    ShowSnackbarAction(ApplicationProvider.appContext.getString(R.string.double_back_pressed)).setDuration(
                        Snackbar.LENGTH_SHORT
                    ).setAction(ApplicationProvider.appContext.getString(R.string.exit))
                )
                startTimer()
                return true
            } else {
                Providers.exitApplication()
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
