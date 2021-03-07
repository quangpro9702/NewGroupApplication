package todo.quang.mvvm.ui.post.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import todo.quang.mvvm.R
import todo.quang.mvvm.ui.post.activity.home.PostListActivity


/*
class HomeScreenWidget : AppWidgetProvider() {
    override fun onUpdate(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            Intent(context, PostListActivity::class.java)
                    .let { intent ->
                        PendingIntent.getActivity(context, 0, intent, 0)
                    }
            RemoteViews(
                    context.packageName,
                    R.layout.widget_home_screen_layout
            ).apply {
                val intent = Intent(context, RemoteViewsServiceWidget::class.java)
                this.setRemoteAdapter(R.id.gridAppLayout, intent)
                appWidgetManager.updateAppWidget(appWidgetId, this)
            }
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)

    }
}
*/
