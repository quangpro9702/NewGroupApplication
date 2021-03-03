package todo.quang.mvvm.ui.post.fragment.gamelist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import todo.quang.mvvm.R

class GameListFragment : Fragment() {

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

}