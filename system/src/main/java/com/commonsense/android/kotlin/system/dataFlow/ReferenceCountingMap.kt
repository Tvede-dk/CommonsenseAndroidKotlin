package com.commonsense.android.kotlin.system.dataFlow

import com.commonsense.android.kotlin.base.FunctionUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Kasper Tvede on 23-07-2017.
 */
data class ReferenceItem<out T>(val item: T, var counter: AtomicInteger)

class ReferenceCountingMap {
    private val map: HashMap<String, ReferenceItem<*>> = hashMapOf()


    fun <T> addItem(item: T, forKey: String) {
        if (hasItem(forKey)) {
            throw RuntimeException("Disallowed to add an element to an already existing index;" +
                    " did you mean increment ?")
        }
        map[forKey] = ReferenceItem(item, AtomicInteger(1))
    }

    fun addOrIncrement(item: Any, forKey: String) {
        if (hasItem(forKey)) {
            incrementCounter(forKey)
        } else {
            addItem(item, forKey)
        }
    }

    fun incrementCounter(forKey: String) = getReference(forKey) {
        it.counter.incrementAndGet()
    }

    fun decrementCounter(forKey: String) = getReference(forKey) {
        val after = it.counter.decrementAndGet()
        if (after <= 0) {
            map.remove(forKey)
        }
    }

    fun hasItem(key: String): Boolean = map.containsKey(key)

    inline fun <reified T> getItemAs(forKey: String): T? {
        val item = getItemOr(forKey)
        if (item is T) {
            return item
        }
        return null
    }

    fun getItemOr(forKey: String): Any? {
        if (hasItem(forKey)) {
            return map[forKey]?.item
        }
        return null
    }

    private inline fun getReference(forKey: String,
                                    crossinline useWith: FunctionUnit<ReferenceItem<*>>) {
        if (hasItem(forKey)) {
            map[forKey]?.let(useWith)
        }
    }

    val count: Int
        get() = map.size


}