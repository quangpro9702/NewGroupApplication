package todo.quang.mvvm.ui.post

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import todo.quang.mvvm.R
import todo.quang.mvvm.databinding.ItemPostBinding
import todo.quang.mvvm.model.AppInfo

class PostListAdapter: RecyclerView.Adapter<PostListAdapter.ViewHolder>() {
    private lateinit var appInfoList:List<AppInfo>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemPostBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_post, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return if(::appInfoList.isInitialized) appInfoList.size else 0
    }

    fun updatePostList(appInfoList:List<AppInfo>){
        this.appInfoList = appInfoList
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root){
    }
}