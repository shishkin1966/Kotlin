package shishkin.sl.kodeinpsb.sl.model

import shishkin.sl.kodeinpsb.sl.presenter.IModelPresenter


abstract class AbsPresenterModel(modelView: IModelView) : AbsModel(modelView), IPresenterModel {
    private lateinit var presenter: IModelPresenter

    override fun setPresenter(presenter: IModelPresenter) {
        this.presenter = presenter
        super.addStateObserver(this.presenter)
    }

    override fun <C> getPresenter(): C {
        return presenter as C
    }
}
