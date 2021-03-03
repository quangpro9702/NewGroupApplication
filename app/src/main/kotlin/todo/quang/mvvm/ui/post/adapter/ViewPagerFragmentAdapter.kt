package todo.quang.mvvm.ui.post.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class ViewPagerFragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val arrayList: ArrayList<Fragment> = ArrayList()
    fun getItem(position: Int): Fragment {
        return arrayList[position]
    }

    fun addFragment(fragment: Fragment) {
        arrayList.add(fragment)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun createFragment(position: Int): Fragment {
        return getItem(position)
    }
}