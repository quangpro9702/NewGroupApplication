package todo.quang.mvvm.ui.post.adapter

import android.content.pm.PackageManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_category.view.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.BaseAdapter
import todo.quang.mvvm.ui.post.activity.home.PostListViewModel
import todo.quang.mvvm.utils.extension.visibleOrGone

class CategoryAdapter(
        private val packageManager: PackageManager,
        private val openApp: (namePackage: String) -> Unit,
        private val showListApp: (position: Int) -> Unit
) : BaseAdapter<List<PostListViewModel.AppInfoDataItem>>(object : DiffUtil.ItemCallback<List<PostListViewModel.AppInfoDataItem>>() {
    override fun areItemsTheSame(oldItem: List<PostListViewModel.AppInfoDataItem>, newItem: List<PostListViewModel.AppInfoDataItem>): Boolean {
        return false
    }

    override fun areContentsTheSame(
            oldItem: List<PostListViewModel.AppInfoDataItem>,
            newItem: List<PostListViewModel.AppInfoDataItem>
    ): Boolean {
        return false
    }
}) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_category)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: List<PostListViewModel.AppInfoDataItem>) {
        var itemApp = item[0]
        Glide
                .with(view)
                .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                .centerInside()
                .into(view.imgFirst)
        view.imgFirst.setOnClickListener {
            openApp.invoke(item[0].packageInfo.packageName)
        }
        view.tvTitle.text = itemApp.appInfoEntity.genreName

        view.imgSecond.visibleOrGone(item.size > 1)
        if (item.size > 1) {
            itemApp = item[1]
            Glide
                    .with(view)
                    .load(itemApp.packageInfo.applicationInfo.loadIcon(packageManager))
                    .centerInside()
                    .into(view.imgSecond)
            view.imgSecond.setOnClickListener {
                openApp.invoke(item[1].packageInfo.packageName)
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
                openApp.invoke(item[2].packageInfo.packageName)
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
                openApp.invoke(item[3].packageInfo.packageName)
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
    }

    private fun ViewGroup.inflate(@LayoutRes l: Int): View {
        return LayoutInflater.from(context).inflate(l, this, false)
    }
}
