package com.devkproject.memokotlin.data

import io.realm.Realm
import io.realm.RealmResults
import io.realm.Sort
import java.util.*

//DAO = Data Access Object
//DB 에 직접 접근하는 대신 필요한 쿼리를 함수로 미리 작성하여 쿼리의 재사용성을 높일 수 있다
class MemoDao (private val realm: Realm) {

    //db 에 담긴 MemoData 를 생성시간의 역순으로 정렬하여 받아온다
    fun getAllMemos(): RealmResults<MemoData> {
        return realm.where(MemoData::class.java)
            .sort("createdAt", Sort.DESCENDING)
            .findAll()
    }

    //지정된 id 의 메모를 가져와서 반환하는 함수
    fun selectMemo(id: String): MemoData {
        return realm.where(MemoData::class.java)
            .equalTo("id", id)
            .findFirst() as MemoData
    }

    //메모를 생성하거나 수정하는 함수
    fun addOrUpdateMemo(memoData: MemoData, title: String, content: String, alarmTime: Date) {
        //executeTransaction() 으로 감싸면 쿼리가 끝날때까지 db 를 안전하게 사용가능
        realm.executeTransaction {
            //메모 데이터의 각종 내용을 변경
            memoData.title = title
            memoData.content = content
            memoData.createdAt = Date()
            memoData.alarmTime = alarmTime

            if(content.length > 100)
                memoData.summary = content.substring(0..100)
            else
                memoData.summary = content

            //Managed 상태가 아닌 경우 copyToRealm() 함수로 db 에 추가
            if(!memoData.isManaged) {
                it.copyToRealm(memoData)
            }
        }
    }

    //활성화된 알람
    //전체 MemoData 중 alarmTime 이 현재시간 Date() 보다 큰 데이터만 가져오는 함수
    fun getActiveAlarms(): RealmResults<MemoData> {
        return realm.where(MemoData::class.java)
            .greaterThan("alarmTime", Date())
            .findAll()
    }
}