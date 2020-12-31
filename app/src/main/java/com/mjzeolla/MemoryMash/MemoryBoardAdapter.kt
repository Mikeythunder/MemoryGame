package com.mjzeolla.MemoryMash

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mjzeolla.MemoryMash.models.BoardSize
import com.mjzeolla.MemoryMash.models.MemoryCard
import com.squareup.picasso.Picasso
import kotlin.math.min

class MemoryBoardAdapter(
        private val context: Context,
        private val boardSize: BoardSize,
        private val cards: List<MemoryCard>,
        private val cardClickListener: CardClickListener
) :
        RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {


    //This is like a static variable in java
    companion object{
        private  const val MARGIN_SIZE = 10;
        private  const val TAG = "MemoryBoardAdapter";
    }

    interface CardClickListener{
        fun onCardClicked(position: Int)
    }


    //When the viewHolder is created need to set what the item is going to be holding
        //In this case it is the memory card layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width/boardSize.getWidth() - (2* MARGIN_SIZE)
        val cardHeight = parent.height/boardSize.getHeight() - (2* MARGIN_SIZE)
        //Assume it to be a square
            //Use the layout Parameters to set how the data is filled into the screen
        val cardSideLength = min(cardWidth,cardHeight)
            //parent is the passed in the views aka the recycler view
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card,parent,false);
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        return ViewHolder(view);
    }

    //responsible for taking the data at a position and binding it to the viewHolder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    //Method to return the number of pieces on the board
    override fun getItemCount() = boardSize.numCards;

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position: Int) {
            val memoryCard = cards[position]
            if(memoryCard.isFaceUp){
                if(memoryCard.imageUrl != null){
                    Picasso.get().load(memoryCard.imageUrl).placeholder(R.drawable.ic_image).into(imageButton)
                }
                else{
                    imageButton.setImageResource(memoryCard.identifier)
                }
            }
            else {
                imageButton.setImageResource(R.drawable.ic_launcher_background)
            }

            imageButton.alpha = if (memoryCard.isMatch) .4f else 1.0f
            val colorStateList = if(memoryCard.isMatch) ContextCompat.getColorStateList(context, R.color.black) else null
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)

            imageButton.setOnClickListener{
                Log.i(TAG,"Clicked on position $position")
                cardClickListener.onCardClicked(position)
            }
        }
    }
}
