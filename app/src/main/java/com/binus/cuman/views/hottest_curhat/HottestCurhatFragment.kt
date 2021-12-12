package com.binus.cuman.views.hottest_curhat

import HottestCurhatViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.binus.cuman.R
import com.binus.cuman.views.CurhatAdapter
import com.binus.cuman.databinding.FragmentHottestCurhatBinding

class HottestCurhatFragment : Fragment() {

    private lateinit var binding: FragmentHottestCurhatBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_hottest_curhat, container, false)

        val viewModel = HottestCurhatViewModel()
        val curhatAdapter = CurhatAdapter()
        val manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.curhatRecyclerView.adapter = curhatAdapter
        binding.curhatRecyclerView.layoutManager = manager

        viewModel.curhats.observe(viewLifecycleOwner, {curhats ->
            curhatAdapter.submitList(curhats)
            if (curhats.size > 0) {
                viewModel.lastCurhat = curhats.get(curhats.size - 1)
            }
        })

        viewModel.isSizeZero.observe(viewLifecycleOwner, {isZero ->
            if (isZero) {
                binding.hottestNoCurhatImg.visibility = View.VISIBLE
            } else {
                binding.hottestNoCurhatImg.visibility = View.GONE
            }
        })

        viewModel.isFetchingData.observe(viewLifecycleOwner, { isFetchingData ->
            if (!isFetchingData) {
                binding.hottestGettingCurhatImg.visibility = View.GONE
                binding.detailCurhatLoadIndicator.visibility = View.GONE

            } else {

                binding.hottestGettingCurhatImg.visibility = View.VISIBLE
                binding.detailCurhatLoadIndicator.visibility = View.VISIBLE
            }
        })

        binding.swipeContainer.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                viewModel.loadData { binding.swipeContainer.isRefreshing = false }
            }
        })
        viewModel.handleOnScrollListener(binding.curhatRecyclerView, manager)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        viewModel.loadData()
    }
}