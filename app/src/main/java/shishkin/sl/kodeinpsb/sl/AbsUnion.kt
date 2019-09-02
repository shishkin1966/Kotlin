package shishkin.sl.kodeinpsb.sl

import java.lang.ref.WeakReference


abstract class AbsUnion<T : ISpecialistSubscriber> : AbsSmallUnion<T>(), IUnion<T> {
    private var currentSubscriber: WeakReference<T>? = null

    override fun register(subscriber: T): Boolean {
        if (super.register(subscriber)) {
            if (currentSubscriber != null) {
                val oldSubscriber = currentSubscriber?.get()
                if (subscriber.getName().equals(oldSubscriber?.getName())) {
                    currentSubscriber = WeakReference(subscriber)
                }
            }
            return true
        }
        return false
    }

    override fun unregister(subscriber: T) {
        super.unregister(subscriber)

        if (currentSubscriber != null) {
            val oldSubscriber = currentSubscriber?.get()
            if (subscriber.getName().equals(oldSubscriber?.getName())) {
                currentSubscriber?.clear()
                currentSubscriber = null
            }
        }
    }

    override fun setCurrentSubscriber(subscriber: T): Boolean {
        if (!subscriber.validate()) return false

        currentSubscriber = WeakReference(subscriber)
        return true
    }

    override fun getCurrentSubscriber(): T? {
        if (currentSubscriber != null) {
            return currentSubscriber?.get();
        }
        return null;
    }

}