package shishkin.sl.kodeinpsb.sl.observe

import shishkin.sl.kodeinpsb.sl.provider.IObservableSubscriber

interface IObjectObservableSubscriber : IObservableSubscriber {
    /**
     * Получить список слушаемых объектов БД
     *
     * @return список слушаемых объектов БД
     */
    fun getListenObjects(): List<String>
}
