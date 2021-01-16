package com.mjzeolla.mindsEye

import android.animation.ArgbEvaluator
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.jinatonic.confetti.CommonConfetti
import com.mjzeolla.mindsEye.models.BoardSize
import com.mjzeolla.mindsEye.models.MemoryGame
import com.mjzeolla.mindsEye.utils.EXTRA_BOARD_SIZE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.mjzeolla.mindsEye.models.UserImageList
import com.mjzeolla.mindsEye.utils.EXTRA_GAME_NAME
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    companion object{
        const val TAG = "MainActivity"
        private  const val CREATE_REQUEST_CODE = 248
    }

    private lateinit var adapter: MemoryBoardAdapter
    private lateinit var clRoot: CoordinatorLayout
    private lateinit var memoryGame: MemoryGame
    private lateinit var rvBoard: RecyclerView
    private lateinit var tvNumMoves: TextView
    private lateinit var tvNumPairs: TextView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private var customGameImages: List<String>? = null

    private val dataBase = Firebase.firestore
    private var gameName: String? = null
    private var category = "Random"
    //GET THE default board size from the class
    private var boardSize: BoardSize = BoardSize.EASY





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        

        rvBoard = findViewById(R.id.rvBoard)
        tvNumMoves = findViewById(R.id.tvNumMoves)
        clRoot = findViewById(R.id.clroot)
        tvNumPairs = findViewById(R.id.tvNumPairs)
        toolbar = findViewById(R.id.app_bar)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setBackgroundResource(R.drawable.gradient)
        setSupportActionBar(toolbar)

        setupBoard()

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.mi_refresh -> {
                if(memoryGame.getNumMoves() > 0 && !memoryGame.gameOver()){
                    showAlertDialog("Quit the current game", null, View.OnClickListener {
                        setupBoard()
                    })
                } else {
                    setupBoard()
                }
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
            R.id.mi_custom -> {
                showCreateDialog()
                return true
            }
            R.id.mi_download -> {
                showDownloadDialog()
                return true
            }
            R.id.mi_category -> {
                showChooseCategory()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showChooseCategory() {
        val boardSizeView =  LayoutInflater.from(this).inflate(R.layout.dialog_category, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (category){
            "Random" -> radioGroupSize.check(R.id.rbEasy)
            "Sports" -> radioGroupSize.check(R.id.rbMedium)
            "Video Games" -> radioGroupSize.check(R.id.rbHard)
            "Animals" -> radioGroupSize.check(R.id.rbExtreme)
        }
        showAlertDialog("Choose New Category", boardSizeView, View.OnClickListener {
            // Set a new value for the board size
            category = when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> "Random"
                R.id.rbMedium -> "Sports"
                R.id.rbHard -> "Video Games"
                else -> "Animals"

            }
            //If the user goes back to playing the default icons then need to make them null
            //To fix this just always make them null when creating a game
            gameName = null
            customGameImages = null
            setupBoard()
        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CREATE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val gameName = data?.getStringExtra(EXTRA_GAME_NAME)
            if(gameName == null){
                Log.e(TAG, "Got null game from CreateActivity")
                return
            }
            downloadGame(gameName)
        }
        if(requestCode == 1 && resultCode == RESULT_OK){
            val gameToDownload = data?.getStringExtra("result").toString().trim()
            downloadGame(gameToDownload)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showDownloadDialog() {
//        val boardDownloadView= LayoutInflater.from(this).inflate(R.layout.dialog_download_board, null)
//        showAlertDialog("Getting Memory Game", boardDownloadView, View.OnClickListener {
//            //Grab the text of the game name that the user downloads
//            val etDownloadGame = boardDownloadView.findViewById<EditText>(R.id.etDownloadGame)
//            val gameToDownload = etDownloadGame.text.toString().trim()
//           downloadGame(gameToDownload)
//        })

        getListOfCustomGames()
    }


    private fun downloadGame(customGameName: String) {
        dataBase.collection("games").document(customGameName).get().addOnSuccessListener { document ->
            val userImageList = document.toObject(UserImageList::class.java)
            if(userImageList?.images == null){
                Log.e(TAG, "Invalid custom game data from Firestore")
                Snackbar.make(clRoot, "Sorry, we couldn't find a game with that name, $customGameName", Snackbar.LENGTH_LONG).show()
                return@addOnSuccessListener
            }
            val numCards = userImageList.images.size * 2
            boardSize = BoardSize.getByValue(numCards)
            customGameImages = userImageList.images
            //This for loop makes it so the images auto load when using custom game and don't have a delay
            for(imageUrl in userImageList.images){
                Picasso.get().load(imageUrl).fetch()
            }
            Snackbar.make(clRoot, "Your're now playing $customGameName!", Snackbar.LENGTH_SHORT).show()
            gameName = customGameName
            setupBoard()

        }. addOnFailureListener { exception ->
            Log.e(TAG, "Exception when retrieving game", exception)
        }
    }

    private fun getListOfCustomGames() : ArrayList<String>{
        var listOfGameNamesFun = ArrayList<String>()
        dataBase.collection("games").get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        listOfGameNamesFun.add(document.id)
                        Log.d(TAG, "${document.id} => ${document.data}")
                    }
                    val intent = Intent(this, DownloadActivity::class.java)
                    intent.putExtra("game_names", listOfGameNamesFun)
                    startActivityForResult(intent, 1)
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "Error getting documents: ", exception)
                }
        Log.i(TAG, " ${listOfGameNamesFun.size}")
        return listOfGameNamesFun

    }

    private fun showCreateDialog() {
        val boardSizeView =  LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        showAlertDialog("Create Memory Board", boardSizeView, View.OnClickListener {
            // Set a new value for the board size
            val desiredBoardSize = when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                R.id.rbHard -> BoardSize.HARD
                else -> BoardSize.EXTREME
            }
            //Navigate to new activity
            val intent = Intent(this, CreateActivity::class.java)
            //For a startActivityForResult need to pass in a unique request code
            intent.putExtra(EXTRA_BOARD_SIZE, desiredBoardSize)
            startActivityForResult(intent, CREATE_REQUEST_CODE)
        })
    }

    private fun showNewSizeDialog() {
        val boardSizeView =  LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardSizeView.findViewById<RadioGroup>(R.id.radioGroup)
        when (boardSize){
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rbMedium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rbHard)
            BoardSize.EXTREME -> radioGroupSize.check(R.id.rbExtreme)
        }
        showAlertDialog("Choose New Size", boardSizeView, View.OnClickListener {
            // Set a new value for the board size
            boardSize = when(radioGroupSize.checkedRadioButtonId){
                R.id.rbEasy -> BoardSize.EASY
                R.id.rbMedium -> BoardSize.MEDIUM
                R.id.rbHard -> BoardSize.HARD
                else -> BoardSize.EXTREME

            }
            //If the user goes back to playing the default icons then need to make them null
            //To fix this just always make them null when creating a game
            gameName = null
            customGameImages = null
            setupBoard()
        })
    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this).setTitle(title).setView(view).setNegativeButton("Cancel", null)
                .setPositiveButton("OK") { _,_ ->
                    positiveClickListener.onClick(null)
                }.show()
    }

    private fun setupBoard() {
        if(gameName != null){
            supportActionBar?.title = "Playing: $gameName" ?: getString(R.string.app_name)
        }
        else {
            supportActionBar?.title = getString(R.string.app_name)
        }
        when(boardSize){
            BoardSize.EASY -> {
                tvNumMoves.text = "Moves: 0"
                tvNumPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                tvNumMoves.text = "Moves: 0"
                tvNumPairs.text = "Pairs: 0 / 6"
            }
            BoardSize.HARD -> {
                tvNumMoves.text = "Moves: 0"
                tvNumPairs.text = "Pairs: 0 / 9"
            }
            BoardSize.EXTREME -> {
                tvNumMoves.text = "Moves: 0"
                tvNumPairs.text = "Pairs: 0 / 12"
            }
        }
        //Include the following line if you want the default color for the text to be the no process color  tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.color_progress_none))
        tvNumPairs.setTextColor(ContextCompat.getColor(this,R.color.black))
        tvNumMoves.setTextColor(ContextCompat.getColor(this,R.color.black))
        memoryGame = MemoryGame(boardSize, customGameImages, category)

        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener{
            override fun onCardClicked(position: Int) {
                updateWithFlip(position)
            }
        });
        rvBoard.adapter = adapter
        rvBoard.setHasFixedSize(true);
        rvBoard.layoutManager = GridLayoutManager(this, boardSize.getWidth());

    }

    private fun updateWithFlip(position: Int) {
        //Error Checking
        //1. Game Over
        //2. Card is already face up
        if(memoryGame.gameOver()){
            Snackbar.make(clRoot, "The game is over! You Won!", Snackbar.LENGTH_SHORT).show()
            return
        }
        if(memoryGame.isCardFaceUp(position)){
            Snackbar.make(clRoot, "Invalid Move!", Snackbar.LENGTH_SHORT).show()
            return
        }
        //Flip over cards and check if a match is present
        if(memoryGame.flipCard(position)) {
            Log.i(TAG, "Found a match! Number of pairs found: ${memoryGame.numPairsFound}")

            //The following code makes the color change as actions are taken
            //Each time the user gets a pair it changes the color as a gradient
            val colorPairs = ArgbEvaluator().evaluate(
                    memoryGame.numPairsFound.toFloat() / boardSize.getNumPairs(),
                    ContextCompat.getColor(this, R.color.progress_none),
                    ContextCompat.getColor(this,R.color.progress_full)
            ) as Int
            tvNumPairs.setTextColor(colorPairs)
            tvNumMoves.setTextColor(colorPairs)



            tvNumPairs.text = "Pairs ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.gameOver()) {
                Snackbar.make(clRoot, "Congrats you won! The game is over!", Snackbar.LENGTH_SHORT).show()
                CommonConfetti.rainingConfetti(clRoot, intArrayOf(Color.BLUE, Color.GREEN, Color.RED)).oneShot()
                Log.i(TAG, "Found a match! Number of pairs found: ${memoryGame.numPairsFound}")
            }
        }

        tvNumMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }
}