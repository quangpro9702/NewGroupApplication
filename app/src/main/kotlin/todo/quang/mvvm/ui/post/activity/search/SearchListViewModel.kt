package todo.quang.mvvm.ui.post.activity.search

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import todo.quang.mvvm.base.switchMapLiveData
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.model.AppInfoEntity
import todo.quang.mvvm.network.PostApi
import todo.quang.mvvm.ui.post.ExceptionBus
import todo.quang.mvvm.utils.FIRST_LOGIN
import todo.quang.mvvm.utils.SHARED_NAME
import todo.quang.mvvm.utils.exception.KtException
import todo.quang.mvvm.utils.exception.KvException
import todo.quang.mvvm.utils.extension.getOrEmpty
import todo.quang.mvvm.utils.extension.postValue
import kotlin.coroutines.CoroutineContext

class SearchListViewModel @ViewModelInject constructor(
        application: Application, private val appInfoDao: AppInfoDao, private val postApi: PostApi) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    private val handler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        ExceptionBus.instance.bindException(throwable)
    }

    private val doneGetData: LiveData<Boolean> = liveData {
        if (!sharedPreferences.getBoolean(FIRST_LOGIN, false)) {
            loadPosts()
        } else {
            emit(true)
        }
    }

    private val queryLiveData: LiveData<String> = MutableLiveData()

    private val mapPackageInfoFromDataBase: LiveData<List<AppInfoDataItem>> = doneGetData.switchMapLiveData(Dispatchers.IO + handler) {
        val list: ArrayList<AppInfoDataItem> = arrayListOf()
        getInstalledApps().forEach {
            appInfoDao.findAppByPackageNameData(it.packageName)?.apply {
                list.add(AppInfoDataItem(this, it))
            } ?: apply {
                val app = postApi.getGenre(it.packageName) ?: throw KtException(0, "Không tìm thấy Privileges")
                app.body()?.data?.takeIf { data ->
                    data.size > 1
                }?.let { data ->
                    /*Truong hop khong tim thay trong database*/
                    val appInsert = AppInfoEntity(packageName = it.packageName,
                            genreType = null, genreName = data[1])
                    appInfoDao.insertAll(appInsert)
                    list.add(AppInfoDataItem(appInsert, it))
                } ?: apply {
                    val appInsert = AppInfoEntity(packageName = it.packageName,
                            genreType = null, genreName = "Other")
                    appInfoDao.insertAll(appInsert)
                    list.add(AppInfoDataItem(appInsert, it))
                }
            }
        }
        emit(list)
    }

    val mergeListAppDataItem: MediatorLiveData<List<AppInfoDataItem>> = MediatorLiveData<List<AppInfoDataItem>>().apply {
        addSource(mapPackageInfoFromDataBase) { it ->
            postValue(it.sortedBy {
                it.packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
            })
        }
        addSource(queryLiveData) { query ->
            if (query.isNullOrBlank()) {
                postValue(mapPackageInfoFromDataBase.getOrEmpty())
            } else {
                postValue(mapPackageInfoFromDataBase.getOrEmpty()
                        .filter {
                            it.packageInfo.applicationInfo.loadLabel(context.packageManager)
                                    .toString().toUpperCase().contains(query.trim().toUpperCase())
                        }
                        .sortedBy {
                            it.packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
                        })
            }

        }
    }

    fun searchApp(query: String) = queryLiveData.postValue(query)

    private fun loadPosts() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val list: ArrayList<AppInfoEntity> = arrayListOf()
            getInstalledApps().forEach {
                postApi.getGenre(it.packageName).apply {
                    this?.body()?.data?.takeIf { data ->
                        data.size > 1
                    }?.let { data ->
                        list.add(AppInfoEntity(packageName = it.packageName,
                                genreType = null, genreName = data[1]))
                    } ?: apply {
                        list.add(AppInfoEntity(packageName = it.packageName,
                                genreType = null, genreName = "Other"))
                    }
                }
            }.apply {
                appInfoDao.insertAll(*list.toTypedArray())
                sharedPreferences.edit().putBoolean("FIRST_LOGIN", true).apply()
                doneGetData.postValue(true)
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

    fun updateAppChangeRecentInfo(app: AppInfoEntity) {
        GlobalScope.launch(Dispatchers.IO + handler) {
            app.sumClick += 1
            app.timeRecent = System.currentTimeMillis()
            appInfoDao.insertAll(app)
        }
    }

    data class AppInfoDataItem(val appInfoEntity: AppInfoEntity, val packageInfo: PackageInfo)
}