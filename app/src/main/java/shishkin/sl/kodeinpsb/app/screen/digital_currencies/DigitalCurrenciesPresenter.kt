package shishkin.sl.kodeinpsb.app.screen.digital_currencies

import microservices.shishkin.example.data.Ticker
import shishkin.sl.kodeinpsb.app.ApplicationSingleton
import shishkin.sl.kodeinpsb.app.provider.Provider
import shishkin.sl.kodeinpsb.common.ApplicationUtils
import shishkin.sl.kodeinpsb.sl.action.*
import shishkin.sl.kodeinpsb.sl.data.ExtResult
import shishkin.sl.kodeinpsb.sl.presenter.AbsPresenter
import shishkin.sl.kodeinpsb.sl.request.IResponseListener


class DigitalCurrenciesPresenter(model: DigitalCurrenciesModel) : AbsPresenter(model),
    IResponseListener {
    companion object {
        const val NAME = "DigitalCurrenciesPresenter"
    }

    private var data: TickerData? = null

    override fun isRegister(): Boolean {
        return true
    }

    override fun getName(): String {
        return NAME
    }

    override fun onStart() {
        if (data == null) {
            data = TickerData()
            getData()
        } else {
            getView<DigitalCurrenciesFragment>()
                ?.addAction(DataAction(Actions.RefreshViews, data))
        }
    }

    override fun onAction(action: IAction): Boolean {
        if (!isValid()) return false

        if (action is ApplicationAction) {
            when (action.getName()) {
                Actions.OnSwipeRefresh -> {
                    getData();
                    return true;
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

    private fun getData() {
        getView<DigitalCurrenciesFragment>()?.addAction(ShowProgressBarAction())
        Provider.getTickers(getName())
    }

    override fun response(result: ExtResult) {
        getView<DigitalCurrenciesFragment>()?.addAction(HideProgressBarAction())
        if (!result.hasError()) {
            val list = ArrayList<Ticker>()
            list.addAll(result.getData() as List<Ticker>)
            data?.tickers = list
            getView<DigitalCurrenciesFragment>()
                ?.addAction(DataAction(Actions.RefreshViews, data))
        } else {
            getView<DigitalCurrenciesFragment>()?.addAction(
                ShowMessageAction(result.getErrorText()!!).setType(
                    ApplicationUtils.MESSAGE_TYPE_ERROR
                )
            )
        }
    }

}
