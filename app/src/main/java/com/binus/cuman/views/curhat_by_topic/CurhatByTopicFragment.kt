package com.binus.cuman.views.curhat_by_topic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binus.cuman.R
import com.binus.cuman.databinding.FragmentCurhatByTopicBinding
import com.binus.cuman.utils.InputUtil
import com.binus.cuman.views.CurhatAdapter

class CurhatByTopicFragment : Fragment() {

    private lateinit var binding: FragmentCurhatByTopicBinding
    private var isScrollingUp = false
    private lateinit var viewModel: CurhatByTopicViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_curhat_by_topic, container, false)
        viewModel = CurhatByTopicViewModel(requireActivity().application)

        val curhatAdapter = setFilteredCurhatRecyclerView()
        viewModel.setTopicAutocomplete(binding.filterTopicAutoComplete)

        binding.filterTopicBtn.setOnClickListener {
            viewModel.OnFilter(binding.filterTopicAutoComplete.text.toString())
            InputUtil.hideKeyboardFrom(binding.root.context, binding.root)
            isScrollingUp = true
            binding.filterTopicAutoComplete.setText("")
        }

        binding.filterTopicAutoComplete.setOnClickListener {
            isScrollingUp = false
        }

        binding.swipeContainer.setOnRefreshListener {
            binding.filterTopicCard.startAnimation(AnimationUtils.loadAnimation(
                binding.root.context, R.anim.trans_down
            ))
            isScrollingUp = true
            binding.swipeContainer.isRefreshing = false
        }

        viewModel.isSizeZero.observe(viewLifecycleOwner, Observer { isZero ->
            if (isZero) {
                binding.curhatByTopicNoCurhat.visibility = View.VISIBLE
            } else {
                binding.curhatByTopicNoCurhat.visibility = View.GONE
            }
        })

        viewModel.topics.observe(viewLifecycleOwner, Observer {
            binding.exampleTopicsLabel.text = it.map { topic -> topic.name }.toString()
        })

        viewModel.filteredCurhats.observe(viewLifecycleOwner, Observer {
            curhatAdapter.submitList(it)
        })

        return binding.root
    }

    private fun setFilteredCurhatRecyclerView(): CurhatAdapter {
        val adapter = CurhatAdapter()

        binding.filterTopicCurhatRecycler.adapter = adapter
        binding.filterTopicCurhatRecycler.layoutManager =
            object : LinearLayoutManager(activity, VERTICAL, false) {
                override fun canScrollVertically(): Boolean = true
            }

        binding.filterTopicCurhatRecycler.addOnScrollListener(object:
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (dy < 0) { //scroll down
                    if (!isScrollingUp) {
                        binding.filterTopicCard.startAnimation(AnimationUtils.loadAnimation(
                            binding.root.context, R.anim.trans_down
                        ))
                        isScrollingUp = true
                    }
                } else {
                    if (isScrollingUp) {
                        binding.filterTopicCard.startAnimation(AnimationUtils.loadAnimation(
                            binding.root.context, R.anim.trans_up
                        ))
                        isScrollingUp = false
                    }
                }
            }
        })

        return adapter
    }

    override fun onResume() {
        super.onResume()
        if (viewModel != null) {
            viewModel.updateTopicList(binding.filterTopicAutoComplete)
        }
    }
}