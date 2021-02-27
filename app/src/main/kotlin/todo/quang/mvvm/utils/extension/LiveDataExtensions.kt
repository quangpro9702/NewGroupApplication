package todo.quang.mvvm.utils.extension

import SingleLiveEvent
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import kotlinx.coroutines.*

@AnyThread
fun <T> LiveData<T>?.postValue(t: T) {
    when (this) {
        is SingleLiveEvent<T> -> this.postValue(t)
        is MediatorLiveData<T> -> this.postValue(t)
        is MutableLiveData<T> -> this.postValue(t)
        is DebounceLiveData<T> -> this.postValue(t)
    }
}

fun <Y, X> LiveData<Map<Y, X>>.getOrEmpty(): Map<Y, X> {
    return getOrDefault(emptyMap())
}

fun <X> LiveData<List<X>>.getOrEmpty(): List<X> {
    return getOrDefault(emptyList())
}

fun <X> LiveData<X>.getOrDefault(default: X): X {
    return value ?: default
}

fun <X> LiveData<X>.getOrNull(): X? {
    return value
}
//
//@MainThread
//fun <T> LiveData<T>.combineSources(
//        sources: List<LiveData<*>>,
//        onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }
//): LiveData<T> {
//    return combineSources(*sources.toTypedArray()) {
//        onChanged.invoke(this)
//    }
//}
//
//@MainThread
//fun <T> LiveData<T>.combineSources(
//        vararg sources: LiveData<*>,
//        onChanged: MediatorLiveData<T>.() -> Unit = { postValue(null) }
//): LiveData<T> {
//    return addSources(*sources) {
//        for (source in sources) {
//            if (source.value == null) return@addSources
//        }
//        onChanged(this)
//    }
//}
//
//@MainThread
//fun <T> LiveData<T>.addSources(
//        sources: List<LiveData<*>>,
//        onChanged: MediatorLiveData<T>.() -> Unit = {}
//): LiveData<T> {
//    return addSources(*sources.toTypedArray()) {
//        onChanged.invoke(this)
//    }
//}

//@MainThread
//fun <T> LiveData<T>.addSources(
//        vararg sources: LiveData<*>,
//        onChanged: MediatorLiveData<T>.() -> Unit = {}
//): LiveData<T> {
//    sources.forEach { item ->
//        addSource(item) {
//            onChanged(this)
//        }
//    }
//    return this
//}

//@MainThread
//fun <T, S> LiveData<T>.addSource(
//        source: LiveData<S>,
//        onChanged: MediatorLiveData<T>.(S) -> Unit = {}
//): LiveData<T> {
//    (this as? MediatorLiveData<T>)?.addSource(source) {
//        onChanged(this, it)
//    }
//    return this
//}

@MainThread
fun <T, S> LiveData<T>.removeSource(source: LiveData<S>): LiveData<T> {
    (this as? MediatorLiveData<T>)?.removeSource(source)
    return this
}

//@MainThread
//fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, func: (T) -> (Unit)) {
//    removeObservers(owner)
//    observe(owner, Observer<T> { t -> func(t) })
//}

class DebounceLiveData<T>(private val scope: CoroutineScope, private val delay: Long = 300L) : MediatorLiveData<T>() {

    private var job: Job? = null

    override fun postValue(value: T) {
        postValue(value, null)
    }

    fun postValue(value: T, delay: Long? = null) {
        job?.cancel()
        job = scope.launch(Dispatchers.IO) {
            delay(delay ?: this@DebounceLiveData.delay)
            super.postValue(value)
        }
    }

    override fun onInactive() {
        super.onInactive()
        job?.cancel()
    }

}
