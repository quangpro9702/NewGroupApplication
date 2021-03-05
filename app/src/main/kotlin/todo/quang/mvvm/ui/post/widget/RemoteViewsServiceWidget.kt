package todo.quang.mvvm.ui.post.widget

import android.content.Intent
import android.widget.RemoteViewsService
import todo.quang.mvvm.model.AppInfoDao

class RemoteViewsServiceWidget(val appInfoDao: AppInfoDao) : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent): RemoteViewsFactory {
        return ListAppFactory(this.applicationContext, appInfoDao,p0)
    }
}