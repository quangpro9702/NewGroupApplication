package todo.quang.mvvm.ui.post.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import todo.quang.mvvm.R
import todo.quang.mvvm.ui.post.activity.home.PostListActivity


class HomeScreenWidget : AppWidgetProvider() {
    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val pendingIntent: PendingIntent = Intent(context, PostListActivity::class.java)
                    .let { intent ->
                        PendingIntent.getActivity(context, 0, intent, 0)
                    }

            val views: RemoteViews = RemoteViews(
                    context.packageName,
                    R.layout.widget_home_screen_layout
            ).apply {
                val intent = Intent(context, RemoteViewsServiceWidget::class.java)
                this.setRemoteAdapter(R.id.gridAppLayout, intent)
                appWidgetManager.updateAppWidget(appWidgetId, this)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val action = intent!!.action
        if (action == AppWidgetManager.ACTION_APPWIDGET_UPDATE) {
            // refresh all your widgets
            val mgr = AppWidgetManager.getInstance(context)
            val cn = ComponentName(context, HomeScreenWidget::class.java)
            mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.gridAppLayout)
        }
        super.onReceive(context, intent)
    }

    fun sendRefreshBroadcast(context: Context) {
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        intent.component = ComponentName(context, HomeScreenWidget::class.java)
        context.sendBroadcast(intent)
    }
}