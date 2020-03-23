package com.devkproject.memokotlin

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

//
//안드로이드 시스템에서 View 를 생성할때 JAVA 생성자 형태로 호출하기 때문에 default arguments 를 호환되도록 만듦
open class InfoView @JvmOverloads constructor(context: Context,
                                              attrs: AttributeSet? = null,
                                              defStyleArr: Int = 0)
    : LinearLayout(context, attrs, defStyleArr) { //LinearLayout 을 상속받아 변수들을 넘겨줌

    init {
        View.inflate(context, R.layout.view_info, this)
    }
}