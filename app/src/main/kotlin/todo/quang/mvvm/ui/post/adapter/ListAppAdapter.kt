package todo.quang.mvvm.ui.post.adapter

import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_app.view.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.BaseAdapter
import todo.quang.mvvm.ui.post.PostListViewModel
import todo.quang.mvvm.utils.extension.inflate

class ListAppAdapter(private val packageManager: PackageManager,
                     private val openApp: (namePackage: String) -> Unit) : BaseAdapter<PostListViewModel.AppInfoDataItem>(object : DiffUtil.ItemCallback<PostListViewModel.AppInfoDataItem>() {
    override fun areItemsTheSame(oldItem: PostListViewModel.AppInfoDataItem, newItem: PostListViewModel.AppInfoDataItem): Boolean {
        return false
    }

    override fun areContentsTheSame(
            oldItem: PostListViewModel.AppInfoDataItem,
            newItem: PostListViewModel.AppInfoDataItem
    ): Boolean {
        return false
    }
}) {
    override fun createView(parent: ViewGroup, viewType: Int?): View {
        return parent.inflate(R.layout.item_app)
    }

    override fun bind(view: View, viewType: Int, position: Int, item: PostListViewModel.AppInfoDataItem) {
        Glide
                .with(view)
                .load(item.packageInfo.applicationInfo.loadIcon(packageManager))
                .centerInside()
                .into(view.imgIcon)
        view.tvName.text = item.packageInfo.applicationInfo.loadLabel(packageManager).toString()
        view.rootView.setOnClickListener {
            openApp.invoke(item.packageInfo.packageName)
        }
    }
}