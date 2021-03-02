package todo.quang.mvvm.ui.post

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import todo.quang.mvvm.R
import todo.quang.mvvm.databinding.ActivityPostListBinding
import todo.quang.mvvm.ui.post.adapter.CategoryAdapter

@AndroidEntryPoint
class PostListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostListBinding
    private var errorSnackbar: Snackbar? = null
    private val viewModel: PostListViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
        setupObserve()
    }

    private fun setupUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list)
        binding.postList.setHasFixedSize(true)
        binding.postList.layoutManager = GridLayoutManager(this, 2)
        categoryAdapter = CategoryAdapter(packageManager) {
            openApp(it)
        }.apply {
            binding.postList.adapter = this
        }
    }

    private fun setupObserve() {
        viewModel.groupAppInfoDataItem.observe(this, {
            categoryAdapter.submitList(it)
        })
    }

    private fun openApp(packageName: String) {
        val launchApp = packageManager.getLaunchIntentForPackage(packageName)
        startActivity(launchApp)
    }
}