package todo.quang.mvvm.ui.post.fragment.gamelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_game_list.*
import todo.quang.mvvm.R
import todo.quang.mvvm.databinding.FragmentGameListBinding
import todo.quang.mvvm.ui.post.activity.home.PostListViewModel
import todo.quang.mvvm.ui.post.adapter.CategoryAdapter
import todo.quang.mvvm.ui.post.fragment.dialoglist.DialogListAppFragment
import todo.quang.mvvm.utils.extension.gone
import todo.quang.mvvm.utils.extension.visible

@AndroidEntryPoint
class GameListFragment : Fragment() {
    private val viewModelShare: PostListViewModel by activityViewModels()
    private val viewModel: GameListViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var binding: FragmentGameListBinding

    companion object {
        fun newInstance(): GameListFragment {
            return GameListFragment()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_game_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObserve()
    }

    private fun setupView() {
//        tvTitleGame.text = requireContext().getText(R.string.game_title)

        binding = FragmentGameListBinding.bind(requireView())
        binding.postList.setHasFixedSize(true)

        binding.postList.layoutManager = GridLayoutManager(requireActivity(), 2)

        categoryAdapter = CategoryAdapter(requireActivity().packageManager, { packageName, game ->
            viewModel.updateGameChangeRecentInfo(game)
            openApp(packageName)
        }, {
            DialogListAppFragment.newInstance(it).show(childFragmentManager, "TAG")
        }).apply {
            binding.postList.adapter = this
        }
    }

    private fun setupObserve() {
        viewModelShare.groupGameInfoDataItem.observe(viewLifecycleOwner, {
            when (it.isNotEmpty()) {
                true -> {
                    layoutEmpty.gone()
                    categoryAdapter.submitList(it)
                }
                else -> {
                    layoutEmpty.visible()
                }
            }
        })
    }

    private fun openApp(packageName: String) {
        val launchApp = requireActivity().packageManager.getLaunchIntentForPackage(packageName)
        startActivity(launchApp)
    }
}