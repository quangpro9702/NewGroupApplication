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
        getAllApplication()
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
        viewModel.genreLiveData.observe(this, {
            categoryAdapter.submitList(it)
        })
    }

    private fun getAllApplication() {
        val packages = getInstalledApps(applicationContext)
        viewModel.loadPosts()
    }

    private fun openApp(packageName: String) {
        val launchApp = packageManager.getLaunchIntentForPackage(packageName)
        startActivity(launchApp)
    }

    private fun getInstalledApps(ctx: Context): Set<PackageInfo> {
        val packageManager: PackageManager = ctx.packageManager
        val allInstalledPackages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
        val filteredPackages: MutableSet<PackageInfo> = HashSet()
        val defaultActivityIcon = packageManager.defaultActivityIcon
        for (each in allInstalledPackages) {
            if (ctx.packageName == each.packageName) {
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
}