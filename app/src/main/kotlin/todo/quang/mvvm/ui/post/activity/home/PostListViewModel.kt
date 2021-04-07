package todo.quang.mvvm.ui.post.activity.home

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.state.RetrieveDataState
import todo.quang.mvvm.base.switchMapLiveData
import todo.quang.mvvm.base.switchMapLiveDataEmit
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.model.AppInfoEntity
import todo.quang.mvvm.network.PostApi
import todo.quang.mvvm.ui.post.ExceptionBus
import todo.quang.mvvm.utils.*
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

    val loadingTextShow: LiveData<String> = MutableLiveData()

    val positionPageChangeLiveData: LiveData<Int> = MutableLiveData()

    val requestPermissionInstallApps: LiveData<Boolean> = MutableLiveData()

    private val _doneGetData: LiveData<Boolean> = liveData(Dispatchers.IO) {
        loadingProgressBar.postValue(RetrieveDataState.Start)
        if (!sharedPreferences.getBoolean(FIRST_LOGIN, false)) {
            if (context.isNetworkAvailable()) {
                loadPosts()
            } else {
                loadingProgressBar.postValue(RetrieveDataState.Failure(Throwable(context.getString(R.string.required_network))))
            }
        } else {
            Log.d("loadpost", "emit true: ")
            emit(true)
        }
    }

    private val mapPackageInfoFromDataBase: LiveData<List<AppInfoDataItem>> = _doneGetData.switchMapLiveData(Dispatchers.IO + handler) {
        val list: ArrayList<AppInfoDataItem> = arrayListOf()
        Log.d("loadpost", "mapPackageInfoFromDataBase ")
        getInstalledApps().forEach { packageInfo ->
            appInfoDao.findAppByPackageNameData(packageInfo.packageName)?.apply {
                list.add(AppInfoDataItem(this, packageInfo))
            } ?: apply {
                kotlin.runCatching {
                    postApi.getGenre(packageInfo.packageName, locale)
                }.getOrNull()?.let { app ->
                    app.body()?.let { response ->
                        response.data.takeIf { data ->
                            data.size > 1
                        }?.let { data ->
                            val appInsert = AppInfoEntity(id = packageInfo.packageName, packageName = packageInfo.packageName,
                                    genreType = response.genre, genreName = data[1])
                            appInfoDao.insertAll(appInsert)
                            list.add(AppInfoDataItem(appInsert, packageInfo))
                        }
                    }
                } ?: apply {
                    val appInsert = AppInfoEntity(id = packageInfo.packageName, packageName = packageInfo.packageName,
                            genreType = APP_CONFIG, genreName = PACKAGE_OTHER)
                    appInfoDao.insertAll(appInsert)
                    list.add(AppInfoDataItem(appInsert, packageInfo))
                }
            }
        }
        emit(list)
    }

    private val listGameFilter: LiveData<List<AppInfoDataItem>> = mapPackageInfoFromDataBase.switchMapLiveDataEmit { it ->
        it.sortedBy {
            it.packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        }.filter {
            it.appInfoEntity.genreType == GAME_CONFIG
        }
    }

    private val listAppFilter: LiveData<List<AppInfoDataItem>> = mapPackageInfoFromDataBase.switchMapLiveDataEmit { it ->
        it.sortedBy {
            it.packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        }.filter {
            it.appInfoEntity.genreType != GAME_CONFIG
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
            this
                    .map {
                        it.value
                    }
                    .sortedBy {
                        it.getOrNull(0)?.appInfoEntity?.genreName
                    }
                    .apply {
                        listApp.addAll(this)
                        loadingProgressBar.postValue(RetrieveDataState.Success(true))
                        emit(listApp.filter { it.isNotEmpty() })
                    }
        }
    }

    val groupGameInfoDataItem: LiveData<List<List<AppInfoDataItem>>> = listGameFilter.switchMapLiveData { it ->
        val listGame: MutableList<List<AppInfoDataItem>> = mutableListOf()
        it.apply {
            //List recent
            listGame.add(this.sortedByDescending { it.appInfoEntity.timeRecent }.take(4))
            //List top used
            listGame.add(this.sortedByDescending { it.appInfoEntity.sumClick }.take(4))
        }.groupBy {
            it.appInfoEntity.genreName
        }.apply {
            this
                    .map {
                        it.value
                    }
                    .sortedBy {
                        it.getOrNull(0)?.appInfoEntity?.genreName
                    }
                    .apply {
                        listGame.addAll(this)
                        loadingProgressBar.postValue(RetrieveDataState.Success(true))
                        emit(listGame.filter { it.isNotEmpty() })
                    }
        }
    }

    private suspend fun loadPosts() {
        val list: ArrayList<AppInfoEntity> = arrayListOf()
        runBlocking {
            getInstalledApps().chunked(20).forEach {
                launch {
                    it.forEach {
                        kotlin.runCatching {
                            postApi.getGenre(it.packageName, locale)
                        }.getOrNull()?.let { response ->
                            response.body()?.let { response ->
                                response.data.takeIf { data ->
                                    data.size > 1
                                }?.let { data ->
                                    list.add(AppInfoEntity(id = it.packageName, packageName = it.packageName,
                                            genreType = response.genre, genreName = data[1]))
                                }
                            }
                        } ?: apply {
                            val appInsert = AppInfoEntity(id = it.packageName, packageName = it.packageName,
                                    genreType = APP_CONFIG, genreName = PACKAGE_OTHER)
                            list.add(appInsert)
                        }
                        loadingTextShow.postValue(it.applicationInfo.loadLabel(context.packageManager).toString())
                    }
                }
            }
        }
        appInfoDao.insertAll(*list.toTypedArray())
        sharedPreferences.edit().putBoolean(FIRST_LOGIN, true).apply()
        _doneGetData.postValue(true)
        Log.d("loadpost", "load data ")
    }

    fun reloadData() = viewModelScope.launch(Dispatchers.IO + handler) {
        val list: ArrayList<AppInfoEntity> = arrayListOf()
        loadingProgressBar.postValue(RetrieveDataState.Start)
        if (context.isNetworkAvailable()) {
            val listApp = appInfoDao.findListAppByGroupName(PACKAGE_OTHER)
            listApp.chunked(20).forEach {
                withContext(coroutineContext) {
                    it.forEach {
                        kotlin.runCatching {
                            postApi.getGenre(it.packageName, locale)
                        }.getOrNull()?.let { response ->
                            response.body()?.let { response ->
                                response.data.takeIf { data ->
                                    data.size > 1
                                }?.let { data ->
                                    list.add(AppInfoEntity(id = it.packageName, packageName = it.packageName,
                                            genreType = response.genre, genreName = data[1]))
                                }
                            }
                        } ?: apply {
                            val appInsert = AppInfoEntity(id = it.packageName, packageName = it.packageName,
                                    genreType = APP_CONFIG, genreName = PACKAGE_OTHER)
                            list.add(appInsert)
                        }
                    }
                }
            }.apply {
                appInfoDao.insertAll(*list.toTypedArray())
                _doneGetData.postValue(true)
            }
        } else {
            loadingProgressBar.postValue(RetrieveDataState.Failure(Throwable(context.getString(R.string.required_network))))
        }
    }

    fun reloadData(getData: Boolean) = _doneGetData.postValue(getData)

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