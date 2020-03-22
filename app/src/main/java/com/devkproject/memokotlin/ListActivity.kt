package com.devkproject.memokotlin

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.devkproject.memokotlin.data.ListViewModel
import com.devkproject.memokotlin.data.MemoData

import kotlinx.android.synthetic.main.activity_list.*
import java.util.*

class ListActivity : AppCompatActivity() {

    private var viewModel: ListViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)

        val fragmentTransition = supportFragmentManager.beginTransaction()
        fragmentTransition.replace(R.id.contentLayout, MemoListFragment())
        fragmentTransition.commit()

        //앱의 객체인 application 이 null 인지 먼저 체크
        viewModel = application!!.let {
            //ViewModel 을 가져오기 위해 ViewModelProvider 객체를 생성
            //viewModelStore : ViewModel 의 생성과 소멸의 기준
            //ViewModelFactory : ViewModel 을 실제로 생성하는 객체
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java) //get 함수를 통해 ListViewModel 을 얻을 수 있음
        }
        fab.setOnClickListener { view ->
            val intent = Intent(applicationContext, DetailActivity::class.java)
            startActivity(intent)
        }
    }
}
