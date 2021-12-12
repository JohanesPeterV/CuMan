package com.binus.cuman.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.LayoutRes

class TopicAutoCompleteAdapter(
    context: Context,
    @LayoutRes private val layoutResource: Int,
    private val allTopics: List<String>
) : ArrayAdapter<String>(context, layoutResource, allTopics), Filterable {

    var topics = allTopics
    var allTopicsContainer = allTopics
    private val MAX_RESULT = 5

    override fun getCount(): Int {
        return topics.size
    }

    override fun getItem(position: Int): String {
        return topics[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView? ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = topics[position]
        return view
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString().toLowerCase()

                val filterResults = FilterResults()

                if (query.isNotEmpty()) {
                    filterResults.values = allTopicsContainer.filter{t -> t.contains(query)}.take(MAX_RESULT)
                } else {
                    filterResults.values = allTopicsContainer
                }



                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                topics = results?.values as List<String>
                notifyDataSetChanged()
            }
        }
    }
}