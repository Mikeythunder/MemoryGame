package com.mjzeolla.MemoryMash.models

enum class BoardSize(val numCards:Int){
    EASY(8),
    MEDIUM(12),
    HARD(18),
    EXTREME(24);


    companion object{
        fun getByValue(value: Int) = values().first {it.numCards == value}
    }

    fun getWidth():Int{
        return when(this){
            EASY -> 2
            HARD -> 3
            EXTREME -> 4
            MEDIUM -> 3
        }
    }

    fun getHeight():Int{
       return numCards/getWidth()
    }

    fun getNumPairs(): Int{
        return numCards/2
    }
}