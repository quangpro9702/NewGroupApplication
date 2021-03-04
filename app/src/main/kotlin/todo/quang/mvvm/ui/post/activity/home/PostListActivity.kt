package todo.quang.mvvm.ui.post.activity.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_post_list.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.BottomAppBarCutCornersTopEdge
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

    private fun setView() {
        val topEdge = BottomAppBarCutCornersTopEdge(
                binding.bottomAppBar.fabCradleMargin,
                binding.bottomAppBar.fabCradleRoundedCornerRadius,
                binding.bottomAppBar.cradleVerticalOffset
        )
        val background = bottomAppBar.background as MaterialShapeDrawable
        background.shapeAppearanceModel = background.shapeAppearanceModel.toBuilder().setTopEdge(topEdge).build()
    }

    private fun setOnClickListener() {
        binding.btnSearch.setOnClickListener {
            /* supportFragmentManager
                     .beginTransaction()
                     .replace(R.id.nav_host_fragment, SearchListActivity.newInstance())
                     .addToBackStack(null)
                     .commit()*/
            val intent = Intent(this, SearchListActivity::class.java)
            startActivity(intent)
        }
    }
}