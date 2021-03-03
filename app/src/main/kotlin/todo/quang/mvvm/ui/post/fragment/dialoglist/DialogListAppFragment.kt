package todo.quang.mvvm.ui.post.fragment.dialoglist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import todo.quang.mvvm.R
import todo.quang.mvvm.ui.post.PostListViewModel

class DialogListAppFragment : DialogFragment() {
    private val viewModelShare: PostListViewModel by activityViewModels()

    companion object {
        private const val KEY_POSITION = "KEY_POSITION"

        fun newInstance(position: Int): DialogListAppFragment {
            val args = Bundle()
            args.putInt(KEY_POSITION, position)
            val fragment = DialogListAppFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dialog_list_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupClickListeners(view)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupView() {
    }

    private fun setupClickListeners(view: View) {
    }
}