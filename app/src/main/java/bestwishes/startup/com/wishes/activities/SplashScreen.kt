package bestwishes.startup.com.wishes.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import bestwishes.startup.com.wishes.MainActivity
import bestwishes.startup.com.wishes.R
import bestwishes.startup.com.wishes.customview.textAnimator.HTextView
import bestwishes.startup.com.wishes.firebase.FireBaseGetData
import java.util.*


class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val hTextView = findViewById<View>(R.id.text) as HTextView
        hTextView.animateText(getString(R.string.animationText))
        Timer().schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }
        }, 1500L)
    }
}
