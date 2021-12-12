package com.binus.cuman.views.newest_curhat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.binus.cuman.R
import com.binus.cuman.databinding.FragmentNewestCurhatBinding
import com.binus.cuman.views.CurhatAdapter

class NewestCurhatFragment : Fragment() {
    private lateinit var manager: LinearLayoutManager
    private lateinit var binding: FragmentNewestCurhatBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_newest_curhat, container, false)
        val viewModel = NewestCurhatViewModel()
        val curhatAdapter = CurhatAdapter()
        manager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding.newestCurhatRecycler.adapter = curhatAdapter
        binding.newestCurhatRecycler.layoutManager = manager

        viewModel.handleOnScrollListener(binding.newestCurhatRecycler, manager)

        viewModel.curhats.observe(viewLifecycleOwner, Observer {curhats ->
            curhatAdapter.submitList(curhats)

            if (curhats.size > 0) {
                viewModel.lastCurhat = curhats.get(curhats.size - 1)
            }
        })

        viewModel.isSizeZero.observe(viewLifecycleOwner, Observer { isSizeZero ->
            if (isSizeZero) {
                binding.newestNoCurhatImg.visibility = View.VISIBLE
            } else {
                binding.newestNoCurhatImg.visibility = View.GONE
            }
        })

        viewModel.isFetchingData.observe(viewLifecycleOwner, Observer { isFetchingData ->
            if (!isFetchingData) {
                binding.newestGettingCurhatImg.visibility = View.GONE
                binding.detailCurhatLoadIndicator.visibility = View.GONE
            } else {
                binding.newestGettingCurhatImg.visibility = View.VISIBLE
                binding.detailCurhatLoadIndicator.visibility = View.VISIBLE
            }
        })

        binding.swipeContainer.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                viewModel.loadData { binding.swipeContainer.isRefreshing = false }
            }

        })

        return binding.root
    }

}