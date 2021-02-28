package todo.quang.mvvm.ui.post

import android.app.Application
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import todo.quang.mvvm.model.AppInfoEntity
import todo.quang.mvvm.network.PostApi
import todo.quang.mvvm.utils.extension.postValue
import kotlin.coroutines.CoroutineContext

class PostListViewModel @ViewModelInject constructor(application: Application, val postApi: PostApi) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    val handler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        ExceptionBus.instance.bindException(throwable)
    }

    val genreLiveData: LiveData<List<List<AppInfoDataItem>>> = MutableLiveData()

    fun loadPosts() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val list: ArrayList<AppInfoDataItem> = arrayListOf()
            getInstalledApps().forEach {
                postApi.getGenre(it.packageName).apply {
                    this.body()?.data?.takeIf { data ->
                        data.size > 1
                    }?.let { data ->
                        list.add(AppInfoDataItem(AppInfoEntity(packageName = it.packageName,
                                genreType = null, genre = data[1]), it))
                    } ?: apply {
                        list.add(AppInfoDataItem(AppInfoEntity(packageName = it.packageName,
                                genreType = null, genre = ""), it))
                    }
                }
            }.apply {
                list.groupBy {
                    it.appInfoEntity.genre
                }.apply {
                    this.map { it.value }.apply {
                        genreLiveData.postValue(this)
                    }
                }
            }
        }
    }

    private fun getInstalledApps(): Set<PackageInfo> {
        val packageManager: PackageManager = context.packageManager
        val allInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        val filteredPackages: MutableSet<PackageInfo> = HashSet()
        val defaultActivityIcon = packageManager.defaultActivityIcon
        for (each in allInstalledPackages) {
            if (context.packageName == each.packageName) {
                continue  // skip own app
            }
            try {
                // add only apps with application icon
                val intentOfStartActivity =
                        packageManager.getLaunchIntentForPackage(each.packageName)
                                ?: continue
                val applicationIcon = packageManager.getActivityIcon(intentOfStartActivity)
                if (defaultActivityIcon != applicationIcon) {
                    filteredPackages.add(each)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.i("MyTag", "Unknown package name " + each.packageName)
            }
        }
        return filteredPackages
    }

    data class AppInfoDataItem(val appInfoEntity: AppInfoEntity, val packageInfo: PackageInfo)
}