package todo.quang.mvvm.ui.post

import android.arch.lifecycle.MutableLiveData
import android.view.View
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import todo.quang.mvvm.R
import todo.quang.mvvm.base.BaseViewModel
import todo.quang.mvvm.model.Post
import todo.quang.mvvm.model.PostDao
import todo.quang.mvvm.network.PostApi
import javax.inject.Inject

class PostListViewModel(private val postDao: PostDao): BaseViewModel(){
    @Inject
    lateinit var postApi: PostApi
    val postListAdapter: PostListAdapter = PostListAdapter()

    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage:MutableLiveData<Int> = MutableLiveData()
    val errorClickListener = View.OnClickListener { loadPosts() }

    private lateinit var subscription: Disposable

    init{
        loadPosts()
    }

    override fun onCleared() {
        super.onCleared()
        subscription.dispose()
    }

    private fun loadPosts(){
        subscription = Observable.fromCallable { postDao.all }
                .concatMap {
                    dbPostList ->
                        if(dbPostList.isEmpty())
                            postApi.getPosts().concatMap {
                                            apiPostList -> postDao.insertAll(*apiPostList.toTypedArray())
                                 Observable.just(apiPostList)
                                       }
                        else
                            Observable.just(dbPostList)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { onRetrievePostListStart() }
                .doOnTerminate { onRetrievePostListFinish() }
                .subscribe(
                        { result -> onRetrievePostListSuccess(result) },
                        { onRetrievePostListError() }
                )
    }

    private fun onRetrievePostListStart(){
        loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    private fun onRetrievePostListFinish(){
        loadingVisibility.value = View.GONE
    }

    private fun onRetrievePostListSuccess(postList:List<Post>){
        postListAdapter.updatePostList(postList)
    }

    private fun onRetrievePostListError(){
        errorMessage.value = R.string.post_error
    }
}