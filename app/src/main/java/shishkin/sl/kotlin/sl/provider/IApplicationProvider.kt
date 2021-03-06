package shishkin.sl.kotlin.sl.provider

import shishkin.sl.kotlin.sl.IProvider

interface IApplicationProvider : IProvider {
    /**
     * Флаг - выход из приложения
     *
     * @return true = приложение остановлено
     */
    fun isExit(): Boolean

    /**
     * Отправить приложение в фон
     *
     */
    fun toBackground()
}
