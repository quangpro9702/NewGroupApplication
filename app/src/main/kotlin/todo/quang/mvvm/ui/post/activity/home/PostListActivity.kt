package todo.quang.mvvm.ui.post.activity.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_post_list.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.state.RetrieveDataState
import todo.quang.mvvm.databinding.ActivityPostListBinding
import todo.quang.mvvm.ui.post.activity.search.SearchListActivity
import todo.quang.mvvm.ui.post.fragment.home.HomeCategoryFragment


@AndroidEntryPoint
class PostListActivity : FragmentActivity() {

    private lateinit var binding: ActivityPostListBinding

    private val viewModel: PostListViewModel by viewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = Firebase.analytics
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list)
        setContentView(binding.root)
        supportFragmentManager
                .beginTransaction()
                .add(R.id.nav_host_fragment, HomeCategoryFragment.newInstance())
                .addToBackStack("")
                .commitAllowingStateLoss()
        // Obtain the FirebaseAnalytics instance.

        setView()
        observeData()
        setOnClickListener()
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
            if (viewModel.loadingProgressBar.value != RetrieveDataState.Start) {
            val intent = Intent(this, SearchListActivity::class.java)
            startActivity(intent)
            }else{
                Toast.makeText(this, getString(R.string.click_failure_message), Toast.LENGTH_SHORT).show()
            }
        }

        bottomAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.reload -> {
                    if (viewModel.loadingProgressBar.value != RetrieveDataState.Start) {
                        showAlertDeleteDialog(getString(R.string.notify_reload)) {
                            viewModel.reloadData()
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.click_failure_message), Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun observeData() {
    }
}