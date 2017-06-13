/**
 * Created by Kasper Tvede on 06-12-2016.
 */

/**
 * performs the action if the boolean is true.
 */
inline fun Boolean.onTrue(crossinline action: () -> Unit): Boolean {
    if (this) {
        action()
    }
    return this
}

/**
 * performs the action if the boolean is false.
 */
inline fun Boolean.onFalse(crossinline action: () -> Unit): Boolean {
    if (!this) {
        action()
    }
    return this
}


inline fun <reified T : kotlin.Enum<T>> valueOfOrUnsafe(type: String?, orValue: T?): T? {
    return java.lang.Enum.valueOf(T::class.java, type) ?: orValue
}

inline fun <reified T : kotlin.Enum<T>> enumFromOr(type: String?, orValue: T): T {
    return java.lang.Enum.valueOf(T::class.java, type) ?: orValue
}
inline fun <reified T : kotlin.Enum<T>> enumFromOrNull(type: String?): T? {
    return java.lang.Enum.valueOf(T::class.java, type) ?: null
}