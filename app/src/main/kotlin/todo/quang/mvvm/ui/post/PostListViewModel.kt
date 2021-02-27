package todo.quang.mvvm.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import todo.quang.mvvm.base.BaseViewModel
import todo.quang.mvvm.model.AppInfo
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.network.PostApi
import todo.quang.mvvm.network.model.AppInfoData
import todo.quang.mvvm.utils.extension.postValue
import javax.inject.Inject

class PostListViewModel @ViewModelInject constructor(val postApi: PostApi): ViewModel() {
    val genreLiveData: LiveData<AppInfoData> = MutableLiveData()
    init {
        loadPosts()
    }

    override fun onCleared() {
        super.onCleared()
    }

    private fun loadPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            postApi.getGenre("com.quangtd.todosimple").apply {
                genreLiveData.postValue(this.body() ?: AppInfoData(listOf("a"),"a"))
            }
        }
    }
}