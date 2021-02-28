package todo.quang.mvvm.ui.post

import SingleLiveEvent
import androidx.lifecycle.LiveData
import todo.quang.mvvm.utils.extension.postValue

class ExceptionBus private constructor() {

    private object HOLDER {
        val INSTANCE = ExceptionBus()
    }

    companion object {
        val instance: ExceptionBus by lazy { HOLDER.INSTANCE }
    }

    val exception: LiveData<Throwable> = SingleLiveEvent<Throwable>()

    fun bindException(e: Throwable) = exception.postValue(e)
}