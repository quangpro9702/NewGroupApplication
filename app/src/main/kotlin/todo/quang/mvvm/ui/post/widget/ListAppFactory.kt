package todo.quang.mvvm.ui.post.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import todo.quang.mvvm.R
import todo.quang.mvvm.model.AppInfoDao
import todo.quang.mvvm.model.AppInfoEntity


private const val REMOTE_VIEW_COUNT: Int = 10

class ListAppFactory(
        private val context: Context, val appInfoDao: AppInfoDao, private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {
    private val widgetItems: MutableList<List<AppInfoDataItem>> = mutableListOf()
    private var appWidgetId = 0
    private val genresGame = listOf("ADVENTURE", "ARCADE", "BOARD", "CARD", "CASINO", "CASUAL", "EDUCATIONAL", "MUSIC", "PUZZLE", "RACING",
            "ROLE_PLAYING", "SIMULATION", "SPORTS", "STRATEGY", "TRIVIA", "WORD")


    override fun onCreate() {
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID)
        mapPackageInfo()
    }

    override fun onDataSetChanged() {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        widgetItems.clear()
    }

    override fun getCount(): Int {
        return widgetItems.size
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    override fun getViewAt(p0: Int): RemoteViews {
        val view = RemoteViews(context.packageName,
                R.layout.item_category)

        val item = widgetItems[p0]
        val itemApp = item[0]
        val icon = drawableToBitmap(itemApp.packageInfo.applicationInfo.loadIcon(context.packageManager))
        view.setImageViewBitmap(R.id.imgFirst, icon)
        /*Glide
                .with(view)
                .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                .centerInside()
                .into(view.imgFirst)*/

        val i = Intent()
        val extras = Bundle()

        extras.putString("PACKAGE_NAME", item[p0].packageInfo.packageName)
        i.putExtras(extras)
        view.setOnClickFillInIntent(R.id.imgFirst, i)

        return view
/*

        view.imgSecond.visibleOrGone(item.size > 1)
        if (item.size > 1) {
            itemApp = item[1]
            Glide
                    .with(view)
                    .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                    .centerInside()
                    .into(view.imgSecond)
            view.imgSecond.setOnClickListener {
                openApp.invoke(item[1].packageInfo.packageName, item[1].appInfoEntity)
            }
        }
        view.imgThird.visibleOrGone(item.size > 2)
        if (item.size > 2) {
            itemApp = item[2]
            Glide
                    .with(view)
                    .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                    .centerInside()
                    .into(view.imgThird)
            view.imgThird.setOnClickListener {
                openApp.invoke(item[2].packageInfo.packageName, item[2].appInfoEntity)
            }
        }
        view.layoutGroup.visibleOrGone(item.size > 4)
        view.imgFourth.visibleOrGone(item.size == 4)
        if (item.size == 4) {
            itemApp = item[3]
            Glide
                    .with(view)
                    .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                    .centerInside()
                    .into(view.imgFourth)
            view.imgFourth.setOnClickListener {
                openApp.invoke(item[3].packageInfo.packageName, item[3].appInfoEntity)
            }
        } else if (item.size > 4) {
            view.layoutGroup.setOnClickListener {
                showListApp.invoke(position)
            }

            itemApp = item[3]
            Glide
                    .with(view)
                    .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                    .centerInside()
                    .into(view.imgFirstt)
            view.imgSecondd.visibleOrGone(item.size > 5)
            if (item.size > 5) {
                itemApp = item[4]
                Glide
                        .with(view)
                        .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                        .centerInside()
                        .into(view.imgSecondd)
            }
            view.imgThirdd.visibleOrGone(item.size > 6)
            if (item.size > 6) {
                itemApp = item[5]
                Glide
                        .with(view)
                        .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                        .centerInside()
                        .into(view.imgThirdd)
            }
            view.imgFourthh.visibleOrGone(item.size > 7)
            if (item.size > 7) {
                itemApp = item[6]
                Glide
                        .with(view)
                        .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                        .centerInside()
                        .into(view.imgFourthh)
            }
        }
*/
    }

    override fun getLoadingView(): RemoteViews {
        TODO("Not yet implemented")
    }

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun hasStableIds(): Boolean = true

    private fun mapPackageInfo() {
        val list: MutableList<AppInfoDataItem> = mutableListOf()
        getInstalledApps().forEach {
            appInfoDao.findAppByPackageNameData(it.packageName)?.apply {
                list.add(AppInfoDataItem(this, it))
            }
        }
        filterOnlyApp(list)
        val listApp: MutableList<List<AppInfoDataItem>> = mutableListOf()
        list.apply {
            //List recent
            listApp.add(this.sortedByDescending { it.appInfoEntity.timeRecent }.take(4))
            //List top used
            listApp.add(this.sortedByDescending { it.appInfoEntity.sumClick }.take(4))
        }.groupBy {
            it.appInfoEntity.genreName
        }.apply {
            this.map { it.value }.sortedBy { it.getOrNull(0)?.appInfoEntity?.genreName }.apply {
                listApp.addAll(this)
                widgetItems.addAll(listApp)
            }
        }
    }

    private fun filterOnlyApp(list: List<AppInfoDataItem>): List<AppInfoDataItem> {
        return list.sortedBy {
            it.packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
        }.filter {
            !genresGame.contains(it.appInfoEntity.genreName.toUpperCase())
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
