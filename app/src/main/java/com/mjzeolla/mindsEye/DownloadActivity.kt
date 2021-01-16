package com.mjzeolla.mindsEye

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class DownloadActivity : AppCompatActivity(), View.OnClickListener, GameNameAdapter.OnItemClickListener {

    private lateinit var rvGameNames: RecyclerView
    private lateinit var adapter: GameNameAdapter
    private lateinit var btnBack: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Select a game to load"



        val listOfGameNames = intent.getStringArrayListExtra("game_names")
        btnBack = findViewById(R.id.imgBtnClose)
        btnBack.setOnClickListener(this)



        if(listOfGameNames != null) {
            rvGameNames = findViewById(R.id.rvGameNames)

            rvGameNames.layoutManager = LinearLayoutManager(this@DownloadActivity)
            adapter = GameNameAdapter(listOfGameNames, this)
            rvGameNames.adapter = adapter

            rvGameNames.addItemDecoration(DividerItemDecoration(rvGameNames.getContext(), DividerItemDecoration.VERTICAL))

        }


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(view: View?) {
        if (view == btnBack) {
            finish();
        }
    }

    private fun showAlertDialog(item : String) {
        AlertDialog.Builder(this@DownloadActivity)
                .setTitle("Game Selected: $item")
                .setMessage("Did you mean to select this game?")
                .setPositiveButton(android.R.string.yes) { dialog, which ->
                    finish()
                }
                .setNegativeButton(android.R.string.no, null)
                .show()
    }

    override fun onItemClicked(position: Int) {
        val clickedItem = intent.getStringArrayListExtra("game_names")?.get(position)
            val resultIntent = Intent()
            resultIntent.putExtra("result", clickedItem)
            setResult(RESULT_OK, resultIntent)
        if (clickedItem != null) {
            showAlertDialog(clickedItem)
        }
    }

}