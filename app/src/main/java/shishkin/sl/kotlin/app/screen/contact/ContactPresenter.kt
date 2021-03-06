package shishkin.sl.kotlin.app.screen.contact

import shishkin.sl.kotlin.app.ApplicationSingleton
import shishkin.sl.kotlin.app.provider.Providers
import shishkin.sl.kotlin.sl.action.ApplicationAction
import shishkin.sl.kotlin.sl.action.IAction
import shishkin.sl.kotlin.sl.presenter.AbsModelPresenter


class ContactPresenter(model: ContactModel) : AbsModelPresenter(model) {
    companion object {
        const val NAME = "ContactPresenter"

        const val WebAction = "WebAction"
        const val MailAction = "MailAction"
    }

    override fun isRegister(): Boolean {
        return true
    }

    override fun getName(): String {
        return NAME
    }

    override fun onAction(action: IAction): Boolean {
        if (!isValid()) return false

        if (action is ApplicationAction) {
            when (action.getName()) {
                WebAction -> {
                    Providers.showProjectWeb()
                    return true
                }

                MailAction -> {
                    Providers.sendErrorReport()
                    return true
                }
            }
        }

        ApplicationSingleton.instance.onError(
            getName(),
            "Unknown action:$action",
            true
        )
        return false
    }

}
