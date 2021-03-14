package todo.quang.mvvm.ui.post.fragment.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import todo.quang.mvvm.R
import todo.quang.mvvm.databinding.FragmentCategoryHomeBinding
import todo.quang.mvvm.ui.post.activity.home.PostListViewModel
import todo.quang.mvvm.ui.post.fragment.applist.AppListFragment
import todo.quang.mvvm.ui.post.fragment.gamelist.GameListFragment
import todo.quang.mvvm.utils.extension.postValue

@AndroidEntryPoint
class HomeCategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryHomeBinding

    private val viewModelShared: PostListViewModel by activityViewModels()

    lateinit var onBackPressedDispatcher : OnBackPressedCallback

    companion object {
        fun newInstance(): HomeCategoryFragment {
            return HomeCategoryFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_category_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onBackPressedDispatcher = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (binding.pager.currentItem == 0) {
                requireActivity().finish()
            } else {
                binding.pager.currentItem = binding.pager.currentItem - 1
            }
        }
        setView()
        setOnClickListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedDispatcher.remove()
        //TODO remove onPageChangePager
    }

    private fun setView() {
        binding = FragmentCategoryHomeBinding.bind(requireView())
        binding.pager.adapter = ScreenSlidePagerAdapter(requireActivity())
    }

    private fun setOnClickListener() {
        binding.pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModelShared.positionPageChangeLiveData.postValue(position)
            }
        })
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> AppListFragment.newInstance()
            1 -> GameListFragment.newInstance()
            else -> AppListFragment.newInstance()
        }
    }
}