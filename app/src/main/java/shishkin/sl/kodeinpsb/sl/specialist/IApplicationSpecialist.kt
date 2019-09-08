package shishkin.sl.kodeinpsb.sl.specialist

import shishkin.sl.kodeinpsb.sl.ISpecialist

interface IApplicationSpecialist : ISpecialist {
    /**
     * Флаг - выход из приложения
     *
     * @return true = приложение остановлено
     */
    fun isExit(): Boolean

    /**
     * Флаг -  выгружать(kill) приложение при остановке(finish)
     */
    fun isKillOnFinish(): Boolean

}
