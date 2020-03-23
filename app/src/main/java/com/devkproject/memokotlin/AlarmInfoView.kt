package com.devkproject.memokotlin

import android.content.Context
import android.util.AttributeSet
import kotlinx.android.synthetic.main.view_info.view.*
import java.text.SimpleDateFormat
import java.util.*

class AlarmInfoView  @JvmOverloads constructor(context: Context,
                                               attrs: AttributeSet? = null,
                                               defStyleAttr: Int = 0)
    : InfoView(context, attrs, defStyleAttr) { //InfoView 를 상속받고 값을 넘겨줌
    companion object {
        private val dateFormat = SimpleDateFormat("yy/MM/dd HH:mm")
    }

    init { //클래스 초기화를 담당하는 init 안에서 View 에 표시할 초기값을 지정
        typeImage.setImageResource(R.drawable.ic_alarm)
        infoText.setText("")
    }

    //외부에서 알람 시간을 Date 타입으로 입력받아 표시하는 함수
    fun setAlarmDate(alarmDate: Date) {
        if(alarmDate.before(Date())) { //알람시간이 현재 시간보다 이전이면 알람이 없다고 표시
            infoText.setText("알람이 없습니다")
        } else {
            infoText.setText(dateFormat.format(alarmDate))
        }
    }
}