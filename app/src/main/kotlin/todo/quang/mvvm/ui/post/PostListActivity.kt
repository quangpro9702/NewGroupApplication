package todo.quang.mvvm.ui.post

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_post_list.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.BottomAppBarCutCornersTopEdge
import todo.quang.mvvm.databinding.ActivityPostListBinding
import todo.quang.mvvm.ui.post.fragment.applist.AppListFragment

private const val NUM_PAGES = 2

@AndroidEntryPoint
class PostListActivity : FragmentActivity() {

    private lateinit var binding: ActivityPostListBinding

    private val viewModel: PostListViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_list)
        setContentView(binding.root)

        binding.pager.adapter = ScreenSlidePagerAdapter(this)

        val topEdge = BottomAppBarCutCornersTopEdge(
                binding.bottomAppBar.fabCradleMargin,
                binding.bottomAppBar.fabCradleRoundedCornerRadius,
                binding.bottomAppBar.cradleVerticalOffset
        )
        val background = bottomAppBar.background as MaterialShapeDrawable
        background.shapeAppearanceModel = background.shapeAppearanceModel.toBuilder().setTopEdge(topEdge).build()
    }

    override fun onBackPressed() {
        if (binding.pager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.pager.currentItem = binding.pager.currentItem - 1
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> AppListFragment.newInstance()
            1 -> AppListFragment.newInstance()
            else -> AppListFragment.newInstance()
        }
    }
}