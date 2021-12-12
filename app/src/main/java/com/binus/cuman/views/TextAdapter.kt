package com.binus.cuman.views
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binus.cuman.R

class TextAdapter(private var texts:MutableList<Text>): RecyclerView.Adapter<TextAdapter.TextViewHolder>(){

    class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextViewHolder {
        return TextViewHolder(
             LayoutInflater.from(parent.context).inflate(
                     R.layout.profile_posted_curhat_card_item, parent, false
             )
        )
    }
    fun setList(nList:MutableList<Text>){
        texts=nList
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return texts.size
    }
    fun clearAllText(){
        texts.clear()
    }


    fun addText(text: Text){
        texts.add(text)
        notifyItemInserted(texts.size-1)
    }

    override fun onBindViewHolder(holder: TextViewHolder, position: Int) {
        val currTodo= texts[position]
//        val tvContent : TextView = holder.itemView.findViewById(R.id.tv_content)
//        tvContent.text=currTodo.content
    }
}