package com.astadevs.sympathyapp.ui.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.animation.OvershootInterpolator
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.base.BaseActivity
import com.astadevs.sympathyapp.databinding.ActivitySplashBinding
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity
import com.astadevs.sympathyapp.ui.login.LoginActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(
    ActivitySplashBinding::inflate
) {
    override fun initializeControls() {
        binding.ivSplashImage.scaleX = 0F
        binding.ivSplashImage.scaleY = 0F

        binding.ivSplashImage
            .animate()
            .scaleX(1F)
            .scaleY(1F)
            .setDuration(1000)
            .setInterpolator(OvershootInterpolator())
            .setStartDelay(200)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    callNextActivity()
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            }).start()
    }

    override fun attachListeners() {

    }

    private fun callNextActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (AppSettings.userUID.isEmpty()) {
                startSpecificActivity(LoginActivity::class.java)
            } else {
                startSpecificActivity(DashboardActivity::class.java)
            }
            finish()
        }, 1000)
    }
}