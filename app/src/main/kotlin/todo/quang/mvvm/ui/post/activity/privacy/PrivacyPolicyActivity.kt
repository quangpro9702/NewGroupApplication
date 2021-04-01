package todo.quang.mvvm.ui.post.activity.privacy

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import todo.quang.mvvm.databinding.ActivityPrivacyPolicyBinding

class PrivacyPolicyActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityPrivacyPolicyBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.webPolicy.settings.javaScriptEnabled = true
        mBinding.webPolicy.loadUrl("https://sites.google.com/view/group-app-policy/home")
    }
}