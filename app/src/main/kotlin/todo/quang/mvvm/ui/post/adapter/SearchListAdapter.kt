package todo.quang.mvvm.ui.post.adapter

import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.l4digital.fastscroll.FastScroller
import kotlinx.android.synthetic.main.item_app_search.view.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.BaseAdapter
import todo.quang.mvvm.model.AppInfoEntity
import todo.quang.mvvm.ui.post.activity.search.SearchListViewModel
import todo.quang.mvvm.utils.extension.inflate

class SearchListAdapter(private val packageManager: PackageManager,
                        private val openApp: (namePackage: String, app: AppInfoEntity) -> Unit) : FastScroller.SectionIndexer,
        BaseAdapter<SearchListViewModel.AppInfoDataItem>(object : DiffUtil.ItemCallback<SearchListViewModel.AppInfoDataItem>() {
            override fun areItemsTheSame(oldItem: SearchListViewModel.AppInfoDataItem, newItem: SearchListViewModel.AppInfoDataItem): Boolean {
                return false
            }

            override fun areContentsTheSame(
                    oldItem: SearchListViewModel.AppInfoDataItem,
                    newItem: SearchListViewModel.AppInfoDataItem
            ): Boolean {
                return false
            }
        }) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_app_search)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: SearchListViewModel.AppInfoDataItem) {
        Glide
                .with(view)
                .load(item.packageInfo.applicationInfo.loadIcon(packageManager))
                .centerInside()
                .into(view.imgIcon)
        view.tvName.text = item.packageInfo.applicationInfo.loadLabel(packageManager).toString()
        view.rootView.setOnClickListener {
            openApp.invoke(item.packageInfo.packageName, item.appInfoEntity)
        }
    }

    override fun getSectionText(position: Int): CharSequence {
        return currentList[position].packageInfo.applicationInfo.loadLabel(packageManager)[0].toString()
    }
}