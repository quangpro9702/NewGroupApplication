package todo.quang.mvvm.ui.post.fragment.gamelist

import androidx.hilt.lifecycle.ViewModelInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import todo.quang.mvvm.base.BaseViewModel
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.model.AppInfoEntity

class GameListViewModel @ViewModelInject constructor(private val appInfoDao: AppInfoDao) : BaseViewModel() {
    fun updateGameChangeRecentInfo(app: AppInfoEntity) {
        GlobalScope.launch(Dispatchers.IO + handler) {
            app.sumClick += 1
            app.timeRecent = System.currentTimeMillis()
            appInfoDao.insertAll(app)
        }
    }
}
