package com.devkproject.memokotlin.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.devkproject.memokotlin.AlarmTool
import io.realm.Realm
import java.util.*

//ViewModel 을 상속받아 DetailViewModel 클래스를 작성
class DetailViewModel : ViewModel () {

    //제목, 내용, 알람시간을 가져와서 갱신
    val title: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val content: MutableLiveData<String> = MutableLiveData<String>().apply { value = "" }
    val alarmTime: MutableLiveData<Date> = MutableLiveData<Date>().apply { value = Date(0) }

    //MemoData 를 저장할때 사용할 변수를 선언해둠
    private var memoData = MemoData()

    //초기화
    private val realm: Realm by lazy {
        Realm.getDefaultInstance()
    }

    private val memoDao: MemoDao by lazy {
        MemoDao(realm)
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }

    //메모를 수정할때 사용하기 위해 메모의 id 를 받아 memoData 를 로드하는 함수
    fun loadMemo(id: String) {
        memoData = memoDao.selectMemo(id)
        title.value = memoData.title
        content.value = memoData.content
        alarmTime.value = memoData.alarmTime
    }

    //alarmTime 값을 0으로 초기화
    fun deleteAlarm() {
        alarmTime.value = Date(0)
    }

    //사용자가 입력한 알람 시간을 받아 갱신
    fun setAlarm(time: Date) {
        alarmTime.value = time
    }

    //메모의 추가나 수정시 사용하기 위해 MemoDao 의 기능과 연결
    fun addOrUpdateMemo(context: Context, title: String, content: String) {

        //alarmTime 을 memoDao 에 넘겨주도록 수정
        val alarmTimeValue = alarmTime.value!!
        memoDao.addOrUpdateMemo(memoData, title, content, alarmTimeValue)

        //AlarmTool 을 통해 메모와 연결된 기존 알람정보를 삭제하고 새 알람시간이 현재시간 이후라면 새로 등록
        AlarmTool.deleteAlarm(context, memoData.id)
        if(alarmTimeValue.after(Date())) {
            AlarmTool.addAlarm(context, memoData.id, alarmTimeValue)
        }
    }
}