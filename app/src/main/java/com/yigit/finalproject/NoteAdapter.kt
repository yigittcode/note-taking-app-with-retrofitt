package com.yigit.finalproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yigit.finalproject.databinding.NotesBinding

class NoteAdapter(val noteList : ArrayList<String> , val idList: List<Int>) : RecyclerView.Adapter<NoteAdapter.NoteHolder>() {

    class NoteHolder(val binding: NotesBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
    val binding = NotesBinding.inflate(LayoutInflater.from(parent.context),parent,false);
        return NoteHolder(binding)
    }

    override fun getItemCount(): Int {
        return noteList.size;

    }

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        holder.binding.recylerViewTextView.text =  noteList.get(position).toString();

        holder.itemView.setOnClickListener {
           val intent = Intent(holder.itemView.context,NoteDetailActivity::class.java);
           // println(idList[position])
            intent.putExtra("id", idList[position]);
            intent.putExtra("position", position);
            holder.itemView.context.startActivity(intent)
        }
    }
}