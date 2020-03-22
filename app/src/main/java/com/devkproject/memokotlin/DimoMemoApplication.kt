package com.devkproject.memokotlin

import android.app.Application
import io.realm.Realm

class DimoMemoApplication () : Application() {
    override fun onCreate() {
        super.onCreate()
        //앱 시작시 Realm db 를 초기화
        Realm.init(this)
    }
}