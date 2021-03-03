package todo.quang.mvvm.ui.post

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import todo.quang.mvvm.base.switchMapLiveData
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.model.AppInfoEntity
import todo.quang.mvvm.network.PostApi
import todo.quang.mvvm.utils.FIRST_LOGIN
import todo.quang.mvvm.utils.SHARED_NAME
import todo.quang.mvvm.utils.extension.postValue
import kotlin.coroutines.CoroutineContext


class PostListViewModel @ViewModelInject constructor(
        application: Application, private val appInfoDao: AppInfoDao, private val postApi: PostApi) : AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext

    private var sharedPreferences: SharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)

    private val doneGetData: LiveData<Boolean> = liveData {
        if (!sharedPreferences.getBoolean(FIRST_LOGIN, false)) {
            loadPosts()
        } else {
            emit(true)
        }
    }

    private val mapPackageInfoFromDataBase: LiveData<List<AppInfoDataItem>> = doneGetData.switchMapLiveData {
        val list: ArrayList<AppInfoDataItem> = arrayListOf()
        getInstalledApps().forEach {
            appInfoDao.findAppByPackageNameData(it.packageName)?.apply {
                list.add(AppInfoDataItem(this, it))
            } ?: apply {
                val app = postApi.getGenre(it.packageName)
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
        /*.filter {
            val list = listOf("ADVENTURE", "ARCADE", "BOARD", "CARD", "CASINO", "CASUAL", "EDUCATIONAL", "MUSIC", "PUZZLE", "RACING",
                    "ROLE_PLAYING", "SIMULATION", "SPORTS", "STRATEGY", "TRIVIA", "WORD")
            list0fit.value.get(0).appInfoEntity.genreName
        }*/
    }

    val groupAppInfoDataItem: LiveData<List<List<AppInfoDataItem>>> = mapPackageInfoFromDataBase.switchMapLiveData { it ->
        it.groupBy {
            it.appInfoEntity.genreName
        }.apply {
            this.map { it.value }.sortedBy { it.getOrNull(0)?.appInfoEntity?.genreName }.apply {
                emit(this)
            }
        }
    }

    private val handler = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
        ExceptionBus.instance.bindException(throwable)
    }

    private fun loadPosts() {
        viewModelScope.launch(Dispatchers.IO + handler) {
            val list: ArrayList<AppInfoEntity> = arrayListOf()
            getInstalledApps().forEach {
                postApi.getGenre(it.packageName).apply {
                    this.body()?.data?.takeIf { data ->
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

    data class AppInfoDataItem(val appInfoEntity: AppInfoEntity, val packageInfo: PackageInfo)
}