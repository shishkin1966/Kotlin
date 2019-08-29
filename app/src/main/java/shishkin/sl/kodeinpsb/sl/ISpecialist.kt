package shishkin.sl.kodeinpsb.sl

/**
 * Интерфейс специалиста - объекта предоставлющий сервис
 */
interface ISpecialist<T> : ISubscriber, IValidated, Comparable<T> {
    /**
     * Получить тип специалиста
     *
     * @return true - не будет удаляться администратором
     */
    fun isPersistent(): Boolean

    /**
     * Событие - отключить регистрацию
     */
    fun onUnRegister()

    /**
     * Событие - регистрация
     */
    fun onRegister()

    /**
     * Остановитить работу специалиста
     */
    fun stop()
}