package todo.quang.mvvm.ui.post.activity.home

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import todo.quang.mvvm.base.state.RetrieveDataState
import todo.quang.mvvm.base.switchMapLiveData
import todo.quang.mvvm.base.switchMapLiveDataEmit
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.model.AppInfoEntity
import todo.quang.mvvm.network.PostApi
import todo.quang.mvvm.ui.post.ExceptionBus
import todo.quang.mvvm.utils.FIRST_LOGIN
import todo.quang.mvvm.utils.SHARED_NAME
import todo.quang.mvvm.utils.exception.KvException
import todo.quang.mvvm.utils.exception.isNetworkAvailable
import todo.quang.mvvm.utils.extension.postValue
import java.util.*
import kotlin.collections.HashSet
import kotlin.coroutines.CoroutineContext


class PostListViewModel @ViewModelInject constructor(
        application: Application, private val appInfoDao: AppInfoDao, private val postApi: PostApi) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val locale = Locale.getDefault().language

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    private val handler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        ExceptionBus.instance.bindException(throwable)
    }

    val loadingProgressBar: LiveData<RetrieveDataState<Boolean>> = MutableLiveData()

    private val _doneGetData: LiveData<Boolean> = liveData(Dispatchers.IO + handler) {
        loadingProgressBar.postValue(RetrieveDataState.Start)
        if (!sharedPreferences.getBoolean(FIRST_LOGIN, false)) {
            if (context.isNetworkAvailable()) {
                loadPosts()
            } else {
                loadingProgressBar.postValue(RetrieveDataState.Failure(Throwable("Đồng bộ data yêu cầu kết nối mạng")))
            }
        } else {
            emit(true)
        }
    }

    private val mapPackageInfoFromDataBase: LiveData<List<AppInfoDataItem>> = _doneGetData.switchMapLiveData(Dispatchers.IO + handler) {
        val list: ArrayList<AppInfoDataItem> = arrayListOf()
        getInstalledApps().forEach {
            appInfoDao.findAppByPackageNameData(it.packageName)?.apply {
                list.add(AppInfoDataItem(this, it))
            } ?: apply {
                val app = postApi.getGenre(it.packageName, locale)
                        ?: throw KvException(0, "Không tìm thấy App")
                app.body()?.let { response ->
                    response.data.takeIf { data ->
                        data.size > 1
                    }?.let { data ->
                        val appInsert = AppInfoEntity(packageName = it.packageName,
                                genreType = response.genre, genreName = data[1])
                        appInfoDao.insertAll(appInsert)
                        list.add(AppInfoDataItem(appInsert, it))
                    }
                } ?: apply {
                    val appInsert = AppInfoEntity(packageName = it.packageName,
                            genreType = "app", genreName = "Other")
                    appInfoDao.insertAll(appInsert)
                    list.add(AppInfoDataItem(appInsert, it))
                }
            }
        }
        emit(list)
    }

    private val listGameFilter: LiveData<List<AppInfoDataItem>> = mapPackageInfoFromDataBase.switchMapLiveDataEmit { it ->
        it.sortedBy {
            it.packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        }.filter {
            it.appInfoEntity.genreType == "game"
        }
    }

    private val listAppFilter: LiveData<List<AppInfoDataItem>> = mapPackageInfoFromDataBase.switchMapLiveDataEmit { it ->
        it.sortedBy {
            it.packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        }.filter {
            it.appInfoEntity.genreType != "game"
        }
    }

    val groupAppInfoDataItem: LiveData<List<List<AppInfoDataItem>>> = listAppFilter.switchMapLiveData { it ->
        val listApp: MutableList<List<AppInfoDataItem>> = mutableListOf()
        it.apply {
            //List recent
            listApp.add(this.sortedByDescending { it.appInfoEntity.timeRecent }.take(4))
            //List top used
            listApp.add(this.sortedByDescending { it.appInfoEntity.sumClick }.take(4))
        }.groupBy {
            it.appInfoEntity.genreName
        }.apply {
            this.map { it.value }.sortedBy {
                it.getOrNull(0)?.appInfoEntity?.genreName
            }.apply {
                listApp.addAll(this)
                loadingProgressBar.postValue(RetrieveDataState.Success(true))
                emit(listApp)
            }
        }
    }

    val groupGameInfoDataItem: LiveData<List<List<AppInfoDataItem>>> = listGameFilter.switchMapLiveData { it ->
        it.groupBy {
            it.appInfoEntity.genreName
        }.apply {
            this.map { it.value }.sortedBy {
                it.getOrNull(0)?.appInfoEntity?.genreName
            }.apply {
                loadingProgressBar.postValue(RetrieveDataState.Success(true))
                emit(this)
            }
        }
    }

    private suspend fun loadPosts() {
        val list: ArrayList<AppInfoEntity> = arrayListOf()
        getInstalledApps().forEach {
            kotlin.runCatching { postApi.getGenre(it.packageName, locale) }.getOrNull()?.let { response ->
                response.body()?.let { response ->
                    response.data.takeIf { data ->
                        data.size > 1
                    }?.let { data ->
                        list.add(AppInfoEntity(packageName = it.packageName,
                                genreType = response.genre, genreName = data[1]))
                    }
                }
            } ?: apply {
                val appInsert = AppInfoEntity(packageName = it.packageName,
                        genreType = "app", genreName = "Other")
                appInfoDao.insertAll(appInsert)
                list.add(appInsert)
            }
        }.apply {
            appInfoDao.insertAll(*list.toTypedArray())
            sharedPreferences.edit().putBoolean("FIRST_LOGIN", true).apply()
            _doneGetData.postValue(true)
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

    fun reloadData(reload : Boolean) = _doneGetData.postValue(reload)

    data class AppInfoDataItem(val appInfoEntity: AppInfoEntity, val packageInfo: PackageInfo)
}