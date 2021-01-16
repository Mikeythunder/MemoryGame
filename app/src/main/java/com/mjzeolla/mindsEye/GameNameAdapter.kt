package com.mjzeolla.mindsEye

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mjzeolla.mindsEye.models.UserImageList

class GameNameAdapter (private val dataSet: ArrayList<String>, private val listener : OnItemClickListener) : RecyclerView.Adapter<GameNameAdapter.ViewHolder>()  {

    companion object{
        val TAG = "GameNameAdapter"
    }




    private val dataBase = Firebase.firestore

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.game_name_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position]
        dataBase.collection("games").document(dataSet[position]).get().addOnSuccessListener { document ->
            val userImageList = document.toObject(UserImageList::class.java)
            if(userImageList?.images == null){
                Log.e(MainActivity.TAG, "Invalid custom game data from Firestore")
                return@addOnSuccessListener
            }
            val size = userImageList?.images?.size
            if(size == 4){
                viewHolder.tvDifficulty.text = "Difficulty: Easy"
                viewHolder.tvDifficulty.setTextColor(Color.parseColor("#35BA01"))
            }
            else if(size == 6) {
                viewHolder.tvDifficulty.text = "Difficulty: Medium"
                viewHolder.tvDifficulty.setTextColor(Color.parseColor("#45731E"))
            }
            else if(size == 9) {
                viewHolder.tvDifficulty.text = "Difficulty: Hard"
                viewHolder.tvDifficulty.setTextColor(Color.parseColor("#B13433"))
            }
            else{
                viewHolder.tvDifficulty.text = "Difficulty: Extreme"
                viewHolder.tvDifficulty.setTextColor(Color.parseColor("#DC1E0B"))
            }
        }. addOnFailureListener { exception ->
            Log.e(MainActivity.TAG, "Exception when retrieving game", exception)
        }

    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val textView: TextView = view.findViewById(R.id.tvGameName)
        val tvDifficulty: TextView = view.findViewById(R.id.tv_difficulty)
        private val layout: RelativeLayout = view.findViewById(R.id.r_layout)

        init{
            layout.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if(position != RecyclerView.NO_POSITION) {
                listener.onItemClicked(position)
            }

        }

    }

    interface OnItemClickListener {
        fun onItemClicked(position: Int)
    }
}
