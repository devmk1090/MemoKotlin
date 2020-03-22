package com.devkproject.memokotlin.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devkproject.memokotlin.R
import kotlinx.android.synthetic.main.item_memo.view.*
import java.text.SimpleDateFormat

class MemoListAdapter (private val list: MutableList<MemoData>) : RecyclerView.Adapter<ItemViewHolder>() {

    private val dateFormat = SimpleDateFormat("MM/dd HH:mm")
    lateinit var itemClickListener: (itemId: String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        //item_memo 를 불러 ViewHolder 를 생성
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_memo, parent, false)

        //아이템이 클릭될때 view 의 tag 에서 메모 id 를 받아서 리스너에 넘김
        view.setOnClickListener {
            itemClickListener?.run {
                val memoId = it.tag as String
                this(memoId)
            }
        }
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        //list 내의 MemoData 의 개수를 반환
        return list.count()
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        //제목이 있는 경우 화면에 표시하고 값을 보여줌
        if(list[position].title.isNotEmpty()) {
            holder.containerView.titleView.visibility = View.VISIBLE
            holder.containerView.titleView.text = list[position].title
        } else {
            holder.containerView.titleView.visibility = View.GONE
        }
        holder.containerView.summaryView.text = list[position].summary
        holder.containerView.dateView.text = dateFormat.format(list[position].createdAt)
        holder.containerView.tag = list[position].id
    }

}