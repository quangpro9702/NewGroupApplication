package todo.quang.mvvm.ui.post.activity.splash

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import todo.quang.mvvm.databinding.ActivitySplashScreenBinding
import todo.quang.mvvm.ui.post.activity.home.PostListActivity
import todo.quang.mvvm.ui.post.activity.privacy.PrivacyPolicyActivity
import todo.quang.mvvm.utils.ACCEPT_POLICY
import todo.quang.mvvm.utils.SHARED_NAME
import todo.quang.mvvm.utils.extension.gone
import todo.quang.mvvm.utils.extension.visible


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySplashScreenBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = mBinding.root
        setContentView(view)
        checkShowPrivacy()
    }

    private fun checkShowPrivacy() {
        sharedPreferences = this.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean(ACCEPT_POLICY, false)) {
            mBinding.layoutPolicy.root.visible()
            mBinding.imgApp.gone()
        } else {
            mBinding.layoutPolicy.root.gone()
            mBinding.imgApp.visible()
            Handler().postDelayed({
                val intent = Intent(this, PostListActivity::class.java)
                startActivity(intent)
                finish()
            }, 1500)
        }

        mBinding.layoutPolicy.tvLink.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        mBinding.layoutPolicy.btnCancel.setOnClickListener {
            finishAffinity()
        }

        mBinding.layoutPolicy.tvLink2.setOnClickListener {
            val intent = Intent(this, PrivacyPolicyActivity::class.java)
            startActivity(intent)
        }

        mBinding.layoutPolicy.btnAgree.setOnClickListener {
            sharedPreferences.edit().putBoolean(ACCEPT_POLICY, true).apply()
            val intent = Intent(this, PostListActivity::class.java)
            startActivity(intent)
        }
    }
}
