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
