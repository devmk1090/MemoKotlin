package com.devkproject.memokotlin.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.realm.Realm

class ListViewModel : ViewModel() {

    //Realm 인스턴스를 생성하여 사용하는 변수
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val memoDao: MemoDao by lazy {
        MemoDao(realm)
    }

    //MemoDao 에서 모든 메모를 가져와서 RealmLiveData 로 변환하여 사용하는 변수
    val memoLiveData: RealmLiveData<MemoData> by lazy {
        RealmLiveData<MemoData> (memoDao.getAllMemos())
    }

    //LiveViewModel 을 더이상 사용하지 않을때 Realm 인스턴스를 닫아줌
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}