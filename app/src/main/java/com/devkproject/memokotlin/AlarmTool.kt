package com.devkproject.memokotlin

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.TableRow
import androidx.core.app.NotificationCompat
import com.devkproject.memokotlin.data.MemoDao
import io.realm.Realm
import java.util.*

//알람을 추가 또는 삭제하는 기능 및 BroadcastReceiver 기능을 상속받아 알람관련 Receiver 기능도 겸함
class AlarmTool: BroadcastReceiver() {

    companion object {
        //알람 intent 를 분류하기 위한 action 값을 상수로 선언
        private const val ACTION_RUN_ALARM = "RUN_ALARM"

        private fun createAlarmIntent(context: Context, id: String): PendingIntent {
            val intent = Intent(context, AlarmTool::class.java)
            intent.data = Uri.parse("id:" + id) //시스템에서 intent 를 구별하는 기준이 됨
            intent.putExtra("MEMO_ID", id)
            intent.action = ACTION_RUN_ALARM

            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        //createAlarmIntent 함수로 알람 Intent 를 생성하여 AlarmManager 에 알람을 설정하는 함수
        fun addAlarm(context: Context, id: String, alarmTime: Date) {
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime.time, alarmIntent)
        }

        fun deleteAlarm(context: Context, id: String) {
            val alarmIntent = createAlarmIntent(context, id)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(alarmIntent)

        }
    }

    //BroadcastReceiver 가 broadcast 를 받았을때 동작하는 onReceive() 함수
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {

            //우리가 만든 Alarm Intent 의 action 값을 확인하여 분기함
            AlarmTool.ACTION_RUN_ALARM -> {

                //intent 에 넣었던 메모 id 를 받아 db 에서 MemoData 를 로드
                val memoId = intent.getStringExtra("MEMO_ID")
                val realm = Realm.getDefaultInstance()
                val memoData = MemoDao(realm).selectMemo(memoId)

                //Notification 에 연결할 Intent 를 생성. Notification 을 누르면 해당 메모의 상세화면으로 이동하도록 만들어줌
                val notificationIntent = Intent(context, DetailActivity::class.java)
                notificationIntent.putExtra("MEMO_ID", memoId)

                val pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)

                val builder= NotificationCompat.Builder(context!!, "alarm")
                    .setContentTitle(memoData.title)
                    .setContentText(memoData.content)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true) //누르면 notification 이 사라짐

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                //OREO 이상의 버전에서는 mipmap 아이콘을 사용할 수 없어 따로 지정해야하고 반드시 채널을 지정해야함
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setSmallIcon(R.drawable.ic_launcher_foreground)
                    val channel = NotificationChannel("alarm", "알람 메시지",
                        NotificationManager.IMPORTANCE_HIGH)
                    notificationManager.createNotificationChannel(channel)
                } else {
                    builder.setSmallIcon(R.mipmap.ic_launcher)
                }

                notificationManager.notify(1, builder.build())
            }

            //기기부팅시 알람을 재등록해야함
            //기기 부팅시 받을 수 있는 broadcast 의 action 인 Intent.ACTION ~ 를 추가
            Intent.ACTION_BOOT_COMPLETED -> {
                val realm = Realm.getDefaultInstance()
                val activeAlarms = MemoDao(realm).getActiveAlarms()

                //활성화된 알람을 for 문으로 돌려 재등록
                for(memoData in activeAlarms) {
                    addAlarm(context!!, memoData.id, memoData.alarmTime)
                }
            }
        }
    }
}