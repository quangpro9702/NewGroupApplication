package todo.quang.mvvm.utils.exception

open class KtException (val code: Int, override val message: String = "") : Throwable(message)