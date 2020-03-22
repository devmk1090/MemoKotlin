package com.devkproject.memokotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View

//MVVM 모델
//View : UI/UX 를 담당, Model : data 를 다룸
//ViewMode : View 와 Model 을 연결하는 중재자. 액티비티나 프래그먼트의 Lifecycle 과 연동하여 생성 및 소멸될 수 있다
class IntroActivity : AppCompatActivity() {

    var handler: Handler? = null //Runnable 을 실행하는 클래스
    var runnable: Runnable?= null //병렬 실행이 가능한 Thread 를 만들어주는 클래스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        //안드로이드 앱을 띄우는 Window 의 속성을 변경하여 시스템 UI 를 숨기고 전체화면으로 표시
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }

    override fun onResume() {
        super.onResume()

        runnable = Runnable {
            val intent = Intent(applicationContext, ListActivity::class.java)
            startActivity(intent)
        }
        handler = Handler()
        handler?.run {
            postDelayed(runnable, 2000) //2초후 runnable 실행
        }
    }

    override fun onPause() {
        super.onPause()

        //Activity Pause 상태일 때는 runnable 중단
        handler?.removeCallbacks(runnable)
    }
}
