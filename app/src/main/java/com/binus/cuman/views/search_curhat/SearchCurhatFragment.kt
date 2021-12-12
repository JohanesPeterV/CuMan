package com.binus.cuman.views.search_curhat


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binus.cuman.R
import com.binus.cuman.repositories.UserRepository
import com.binus.cuman.views.CurhatAdapter


class SearchCurhatFragment : Fragment() {
    val viewModel=SearchCurhatViewModel()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_search_curhat, container, false)
        var searchButton: Button= rootView.findViewById(R.id.search_curhat_btn)
        var filterButton: ImageButton= rootView.findViewById(R.id.filter_button)
        val searchTextView: EditText= rootView.findViewById(R.id.search_auto_complete)
        val rv: RecyclerView = rootView.findViewById(R.id.search_curhat_recycler_view)
        val sortedByTv: TextView = rootView.findViewById(R.id.search_sorted_by)

        rv.layoutManager = object: LinearLayoutManager(rootView.context) {
//            override fun canScrollVertically(): Boolean {
//                return false
//            }
        }

        setFilterPopUp(rootView,viewModel, filterButton, sortedByTv,searchTextView)

        searchButton.setOnClickListener{
            viewModel.searchString=searchTextView.text.toString()
            viewModel.search()
        }
        val curhatAdapter=CurhatAdapter()
        rv.adapter=curhatAdapter
        viewModel.curhats.observe(viewLifecycleOwner, Observer {curhats->
            curhatAdapter.submitList(curhats.toList())
            rv.smoothScrollToPosition(0)
        })




        return rootView
    }
//    private fun showRadioButtonDialog(rootView: View, viewModel: SearchCurhatViewModel) {
//
//        // custom dialog
//
//        val dialog = Dialog(rootView.context)
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
//        dialog.setContentView(R.layout.filter_popup_choice)
//        val stringList: MutableList<String> = ArrayList() // here is list
//        stringList.add("Sort search by most like")
//        stringList.add("Sort search by most dislike")
//        stringList.add("Filter curhats posted last 24 hour")
//        stringList.add("Filter curhats posted last week")
//        stringList.add("Filter curhats posted last month")
//        stringList.add("Filter curhats posted last year")
//
//        val rg = dialog.findViewById(R.id.radio_group) as RadioGroup
//        for (i in stringList.indices) {
//            val rb = RadioButton(rootView.context) // dynamically creating RadioButton and adding to RadioGroup.
//            rb.text = stringList[i]
//            rg.addView(rb)
//        }
//
//        dialog.show()
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("RestrictedApi")
    fun setFilterPopUp(
        rootView: View,
        viewModel: SearchCurhatViewModel,
        filterButton: ImageButton,
        sortedByTv: TextView,
        searchTextView: EditText
    ){
        val userId = UserRepository.getCurrentUserId()
        filterButton.setOnClickListener {
            viewModel.searchString=searchTextView.text.toString()
            val popupMenu = PopupMenu(rootView.context, it)
            popupMenu.inflate(R.menu.filter_menu)

            popupMenu.setOnMenuItemClickListener {
                it.isChecked = true
                when (it.itemId) {
                    R.id.like_menu->{
                        sortedByTv.text = getString(R.string.most_liked)
                        viewModel.searchType=1
                        true
                    }
                    R.id.dislike_menu->{
                        sortedByTv.text = getString(R.string.most_disliked)
                        viewModel.searchType=2
                        true
                    }
                    R.id.day_menu->{
                        sortedByTv.text = getString(R.string.posted_24_hours)
                        viewModel.searchType=3
                        true
                    }
                    R.id.week_menu->{
                        sortedByTv.text = getString(R.string.posted_one_week)
                        viewModel.searchType=4
                        true
                    }
                    R.id.month_menu->{
                        sortedByTv.text = getString(R.string.posted_one_month)
                        viewModel.searchType=5
                        true
                    }
                    R.id.year_menu->{
                        sortedByTv.text = getString(R.string.posted_one_year)
                        viewModel.searchType=6
                        true
                    }
                    else -> false
                }

                viewModel.search()
                true
            }
            val popupHelper = MenuPopupHelper(rootView.context, popupMenu.menu as MenuBuilder, it)
            popupHelper.setForceShowIcon(true)
            popupHelper.show()
        }
    }
}

