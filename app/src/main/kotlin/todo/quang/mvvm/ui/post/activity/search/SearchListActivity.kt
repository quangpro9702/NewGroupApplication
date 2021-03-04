package todo.quang.mvvm.ui.post.activity.search

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import dagger.hilt.android.AndroidEntryPoint
import todo.quang.mvvm.R
import todo.quang.mvvm.databinding.FragmentSearchListBinding
import todo.quang.mvvm.ui.post.adapter.SearchListAdapter

@AndroidEntryPoint
class SearchListActivity : FragmentActivity() {
    private val viewModel: SearchListViewModel by viewModels()
    private lateinit var searchAdapter: SearchListAdapter
    private lateinit var binding: FragmentSearchListBinding

    companion object {
        fun newInstance(): SearchListActivity {
            return SearchListActivity()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_search_list)
        setContentView(binding.root)
        setupView()
        setupObserve()
    }

    private fun setupView() {
        searchAdapter = SearchListAdapter(this.packageManager) {
            openApp(it)
        }.apply {
            binding.recyclerFast.adapter = this
        }
    }

    private fun setupObserve() {
        viewModel.mergeListAppDataItem.observe(this, {
            searchAdapter.submitList(it)
        })
    }

    private fun openApp(packageName: String) {
        val launchApp = this.packageManager.getLaunchIntentForPackage(packageName)
        startActivity(launchApp)
    }
}