package com.devkproject.memokotlin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devkproject.memokotlin.data.DetailViewModel
import com.takisoft.datetimepicker.DatePickerDialog
import com.takisoft.datetimepicker.TimePickerDialog
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import java.util.*

class DetailActivity : AppCompatActivity() {

    private var viewModel: DetailViewModel? = null
    private val dialogCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        viewModel = application!!.let {
            //ViewModel 을 가져오기 위해 ViewModelProvider 객체를 생성
            //viewModelStore : ViewModel 의 생성과 소멸의 기준
            //ViewModelFactory : ViewModel 을 실제로 생성하는 객체
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(DetailViewModel::class.java) //get 함수를 통해 ViewModel 을 얻을 수 있음
        }

        //제목과 내용에 observer 를 걸어 화면을 갱신
        viewModel!!.let {
            it.title.observe(this, Observer { supportActionBar?.title = it })
            it.content.observe(this, Observer { contentEdit.setText(it) })

            //alarmInfoView 에 값을 넘겨 표시해줌
            it.alarmTime.observe(this, Observer { alarmInfoView.setAlarmDate(it) })
        }


        //ListActivity 에서 아이템을 선택했을 때 보내주는 메모 id 로 데이터를 로드
        val memoId = intent.getStringExtra("MEMO_ID")
        if(memoId != null)
            viewModel!!.loadMemo(memoId)

        toolbarLayout.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog_title, null)
            val titleEdit = view.findViewById<EditText>(R.id.titleEdit)

            AlertDialog.Builder(this)
                .setTitle("제목을 입력하세요")
                .setView(view) //다이얼로그의 내용이 되는 view 설정
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    supportActionBar?.title = titleEdit.text.toString()
                }).show()
        }
    }

    //뒤로가기를 누르면 viewModel 의 addOrUpdateMemo() 를 호출하여 메모를 db 갱신
    override fun onBackPressed() {
        super.onBackPressed()
        viewModel?.addOrUpdateMemo(
            this,
            supportActionBar?.title.toString(),
            contentEdit.text.toString()
        )
    }

    //날짜 다이어로그를 여는 함수
    private fun openDateDialog() {
        val datePickerDialog = DatePickerDialog(this)

        //사용자에 의해 날짜가 입력되면 실행되는 Listener
        datePickerDialog.setOnDateSetListener { view, year, month, dayOfMonth ->
            dialogCalendar.set(year, month, dayOfMonth)
            openTimeDialog()
        }
        datePickerDialog.show()
    }

    //사용자가 입력한 시간을 임시 캘린더 변수에 설정하고 캘린더 변수의 time 값(Date 객체) 를 ViewModel 에 새 알람 값으로 설정
    private fun openTimeDialog() {
        val timePickerDialog = TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                dialogCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                dialogCalendar.set(Calendar.MINUTE, minute)

                viewModel?.setAlarm(dialogCalendar.time)
            },
            0, 0, false) //초기시간은 0시 0분으로 설정. 24시간제는 사용하지 않음
        timePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_share -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, supportActionBar?.title)
                intent.putExtra(Intent.EXTRA_TEXT, contentEdit.text.toString())

                startActivity(intent)
            }
            R.id.menu_alarm -> {
                //기존의 알람값이 현재 시간 기준으로 유효한지 체크
                if(viewModel?.alarmTime?.value!!.after(Date())) {
                    AlertDialog.Builder(this)
                        .setTitle("안내")
                        .setMessage("기존에 알람이 설정되어 있습니다. 삭제 또는 재설정할 수 있습니다")
                        .setPositiveButton("재설정", DialogInterface.OnClickListener { dialog, which ->
                            openDateDialog() //재설정 버튼에서는 날짜 다이어로그를 띄움
                        })
                        .setNegativeButton("삭제", DialogInterface.OnClickListener { dialog, which ->
                            viewModel?.deleteAlarm() //삭제 버튼에서는 alarmTime 을 초기화
                        })
                        .show()
                }
                else {
                    openDateDialog() //알람값이 유효하지 않다면 날짜 다이어로그를 띄워 알람값을 설정
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
