package todo.quang.mvvm.base

import android.arch.lifecycle.ViewModel
import todo.quang.mvvm.injection.component.DaggerViewModelInjector
import todo.quang.mvvm.injection.component.ViewModelInjector
import todo.quang.mvvm.injection.module.NetworkModule
import todo.quang.mvvm.ui.post.PostListViewModel
import todo.quang.mvvm.ui.post.PostViewModel

abstract class BaseViewModel:ViewModel(){
    private val injector: ViewModelInjector = DaggerViewModelInjector
            .builder()
            .networkModule(NetworkModule)
            .build()

    init {
        inject()
    }

    /**
     * Injects the required dependencies
     */
    private fun inject() {
        when (this) {
            is PostListViewModel -> injector.inject(this)
            is PostViewModel -> injector.inject(this)
        }
    }
}