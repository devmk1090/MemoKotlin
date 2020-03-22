package com.devkproject.memokotlin


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devkproject.memokotlin.data.ListViewModel
import com.devkproject.memokotlin.data.MemoListAdapter
import kotlinx.android.synthetic.main.fragment_memo_list.*


//RecyclerView 가 표시되는 곳
class MemoListFragment : Fragment() {

    private lateinit var listAdapter: MemoListAdapter
    private var viewModel: ListViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_memo_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //activity 속성은 ListActivity 를 가리킴
        //activity 의 viewModelStore 를 쓰는 이유는 activity 와 viewModel 을 공윻할 수 있기 때문.
        //만약 fragment 의 viewModelStore 를 사용한다면 MemoListFragment 만의 viewModel 이 따로 생성된다.
        viewModel = activity!!.application!!.let {
            ViewModelProvider(activity!!.viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(ListViewModel::class.java)
        }

        viewModel!!.let {
            it.memoLiveData.value?.let {
                listAdapter = MemoListAdapter(it)
                memoListView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                memoListView.adapter = listAdapter
                listAdapter.itemClickListener = {
                    val intent = Intent(activity, DetailActivity::class.java)
                    intent.putExtra("MEMO_ID", it)
                    startActivity(intent)
                }
            }
            //MemoLiveData 에 observer 함수를 통해 값이 변할때 동작할 observer 를 붙여줌
            //observer 내에서는 adapter 의 갱신 코드를 호출
            it.memoLiveData.observe(this,
                Observer {
                    listAdapter.notifyDataSetChanged()
                })
        }
    }

    override fun onResume() {
        super.onResume()
        listAdapter.notifyDataSetChanged()
    }
}
