package todo.quang.mvvm.ui.post.activity.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import todo.quang.mvvm.R
import todo.quang.mvvm.databinding.ActivityPostListBinding
import todo.quang.mvvm.ui.post.activity.search.SearchListActivity
import todo.quang.mvvm.ui.post.fragment.home.HomeCategoryFragment


@AndroidEntryPoint
class PostListActivity : FragmentActivity() {

    private lateinit var binding: ActivityPostListBinding

    private val viewModel: PostListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list)
        setContentView(binding.root)
        supportFragmentManager
                .beginTransaction()
                .add(R.id.nav_host_fragment, HomeCategoryFragment.newInstance())
                .addToBackStack("")
                .commitAllowingStateLoss()
        setView()
        setOnClickListener()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.reload -> {
                showAlertDeleteDialog(getString(R.string.notify_reload)) {
                    viewModel.reloadData(true)
                }
            }
        }
    }

    private fun showAlertDeleteDialog(message: String, block: () -> Unit) {
        MaterialAlertDialogBuilder(this)
                .setTitle(resources.getString(R.string.notify_title))
                .setMessage(message)
                .setNegativeButton(resources.getString(R.string.cancel_action)) { _, _ ->
                }
                .setPositiveButton(resources.getString(R.string.accept_action)) { _, _ ->
                    block.invoke()
                }
                .show()
    }

    private fun setView() {
    }

    private fun setOnClickListener() {
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            startActivity(intent)
        }
    }
}