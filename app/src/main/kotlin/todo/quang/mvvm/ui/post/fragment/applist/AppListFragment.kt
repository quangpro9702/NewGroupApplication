package todo.quang.mvvm.ui.post.fragment.applist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_app_list.*
import kotlinx.android.synthetic.main.layout_loading.view.*
import todo.quang.mvvm.R
import todo.quang.mvvm.base.state.RetrieveDataState
import todo.quang.mvvm.databinding.FragmentAppListBinding
import todo.quang.mvvm.ui.post.activity.home.PostListViewModel
import todo.quang.mvvm.ui.post.adapter.CategoryAdapter
import todo.quang.mvvm.ui.post.fragment.dialoglist.DialogListAppFragment
import todo.quang.mvvm.utils.extension.gone
import todo.quang.mvvm.utils.extension.visible

@AndroidEntryPoint
class AppListFragment : Fragment() {
    private val viewModelShare: PostListViewModel by activityViewModels()

    private val viewModel: AppListViewModel by viewModels()

    private lateinit var binding: FragmentAppListBinding

    private lateinit var categoryAdapter: CategoryAdapter

    private lateinit var materialAlertDialogBuilder: MaterialAlertDialogBuilder

    private lateinit var customAlertDialogView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_app_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAppListBinding.bind(requireView())
        setupUI()
        setupObserve()
    }

    private fun setupUI() {
        tvTitle.text = requireContext().getText(R.string.application_title)
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(requireContext())

        binding.postList.setHasFixedSize(true)

        binding.postList.layoutManager = GridLayoutManager(requireActivity(), 2)

        categoryAdapter = CategoryAdapter(requireActivity().packageManager, { packageName, app ->
            viewModel.updateAppChangeRecentInfo(app)
            openApp(packageName)
        }, {
            /*  val emailCardDetailTransitionName = getString(R.string.transition_to_detail)
              val extras = FragmentNavigatorExtras(view to emailCardDetailTransitionName)
              navigate(HomeFragmentDirections.actionFirstFragmentToSecondFragment(id, title), null, extras)
              exitTransition = MaterialElevationScale(false).apply {
                  duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
              }
              reenterTransition = MaterialElevationScale(true).apply {
                  duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
              }*/
            DialogListAppFragment.newInstance(it).show(childFragmentManager, "TAG")
        }).apply {
            binding.postList.adapter = this
        }
    }

    private fun setupObserve() {
        viewModelShare.groupAppInfoDataItem.observe(viewLifecycleOwner, {
            categoryAdapter.submitList(it)
        })
        viewModelShare.loadingProgressBar.observe(viewLifecycleOwner, {
            when (it) {
                is RetrieveDataState.Start -> {
                    layoutLoading.rootView.tvAnimation.text = getString(R.string.waiting_sync)
                    layoutLoading.visible()
                }
                is RetrieveDataState.Success -> {
                    layoutLoading.gone()
                }

                is RetrieveDataState.Failure -> {
                    layoutLoading.gone()
                    Toast.makeText(context, it.throwable.message, Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun openApp(packageName: String) {
        val launchApp = requireActivity().packageManager.getLaunchIntentForPackage(packageName)
        startActivity(launchApp)
    }

    companion object {
        fun newInstance(): AppListFragment {
            return AppListFragment()
        }
    }
}