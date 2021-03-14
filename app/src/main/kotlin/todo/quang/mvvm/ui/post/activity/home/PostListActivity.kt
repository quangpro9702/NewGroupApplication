package todo.quang.mvvm.ui.post.activity.home

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
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
import todo.quang.mvvm.utils.FIRST_LOGIN
import todo.quang.mvvm.utils.SHARED_NAME


@AndroidEntryPoint
class PostListActivity : FragmentActivity() {

    private lateinit var binding: ActivityPostListBinding

    private val viewModel: PostListViewModel by viewModels()

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var sharedPreferences: SharedPreferences

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

    override fun onResume() {
        super.onResume()
        sharedPreferences = this.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean(FIRST_LOGIN, false)) {
            viewModel.reloadData(true)
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
                .show().apply {
                    this.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
                    this.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
                }
    }

    private fun setView() {
    }

    private fun setOnClickListener() {
        binding.btnSearch.setOnClickListener {
            if (viewModel.loadingProgressBar.value != RetrieveDataState.Start) {
                val intent = Intent(this, SearchListActivity::class.java)
                startActivity(intent)
            } else {
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
        viewModel.positionPageChangeLiveData.observe(this, {
            when (it) {
                0 -> {
                    appTitle.text = getString(R.string.application_title)
                }
                1 -> {
                    appTitle.text = getString(R.string.game_title)
                }
                else -> {
                    appTitle.text = getString(R.string.application_title)
                }

            }
        })
    }
}