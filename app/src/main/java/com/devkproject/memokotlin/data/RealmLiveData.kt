package com.devkproject.memokotlin.data

import androidx.lifecycle.LiveData
import io.realm.RealmChangeListener
import io.realm.RealmObject
import io.realm.RealmResults

//RealmResults 를 LiveData 로 사용할 수 있는 클래스
//LiveData 를 상속받아 class 를 만들고 생성자에서 RealmResults 를 받음
class RealmLiveData<T: RealmObject> (private val realmResults: RealmResults<T>)
    : LiveData<RealmResults<T>> () {
    init {
        //받아온 realmResults 를 value 에 추가(observe 가 동작하도록 하기 위해)
        value = realmResults
    }

    //RealmResults 가 갱신될때 동작할 리스너(갱신되는 값을 value 에 할당)
    private val listener = RealmChangeListener<RealmResults<T>> {
        value = it
    }

    //LiveData 가 활성화 될때 realmResults 에 리스너를 붙여줌
    override fun onActive() {
        super.onActive()
        realmResults.addChangeListener(listener)
    }

    //LiveData 가 비활성화 될때 리스너 제거
    override fun onInactive() {
        super.onInactive()
        realmResults.removeChangeListener(listener)
    }
}