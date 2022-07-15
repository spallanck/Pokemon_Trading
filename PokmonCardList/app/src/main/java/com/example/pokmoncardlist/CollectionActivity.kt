package com.example.pokmoncardlist

import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.net.URL
import kotlin.concurrent.thread

var listOfMyCards: MutableList<CardData> = mutableListOf()
var listOfDelCards: MutableList<CardData> = mutableListOf()

// A view class that represents a single item in the list.
class MyCollectionItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cardImage: ImageView = view.findViewById(R.id.card_image)
    var checkBox: CheckBox = view.findViewById(R.id.checkbox)

    var checkBoxArr = SparseBooleanArray()
    init {
        checkBox.setOnClickListener {
            if (!checkBoxArr.get(adapterPosition, false)) {
                checkBox.isChecked = true
                checkBoxArr.put(adapterPosition, true)
                if (!listOfDelCards.contains(listOfMyCards[adapterPosition])) {
                    listOfDelCards.add(listOfMyCards[adapterPosition])
                }
            } else {
                checkBox.isChecked = false
                checkBoxArr.put(adapterPosition, false)
                if (listOfDelCards.contains(listOfMyCards[adapterPosition])) {
                    listOfDelCards.remove(listOfMyCards[adapterPosition])
                }
            }
        }
    }
}

class MyCollectionAdapter(val activity: CollectionActivity, val model: MutableList<CardData>) : RecyclerView.Adapter<MyCollectionItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCollectionItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.collection_item, parent, false)
        return MyCollectionItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyCollectionItemViewHolder, position: Int) {
        // Get the URL
        // Load it from the internet as a bitmap
        // Change the image to the bitmap that is read from the internet
        val urlstring = listOfMyCards[position].image
        Glide.with(activity).load(urlstring).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.cardImage)
        holder.checkBox.isChecked = holder.checkBoxArr.get(position, false)
        thread {
            activity.runOnUiThread {
                holder.cardImage.setOnClickListener {
                    Log.d("charizard", "clicked on card num ${position + 1}")
                    (activity as CollectionActivity).myCardPreview(listOfMyCards[position].image, listOfMyCards[position].name, listOfMyCards[position].set, listOfMyCards[position].rarity)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

}


class CollectionActivity : AppCompatActivity() {
    val dbman = MyDatabaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)

        val toggle: ToggleButton = findViewById(R.id.toggle_button)
        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // The toggle is enabled
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // The toggle is disabled
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)
        bottomNav.selectedItemId = R.id.collection
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search->startActivity(Intent(this, MainActivity::class.java)).also { finish() }
                R.id.camera->startActivity(Intent(this, CameraActivity::class.java)).also { finish() }
            }
            true
        }
        showList()

        val deleteButton: Button = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            if (listOfDelCards.isEmpty()) {
                Toast.makeText(applicationContext, "No cards are selected to delete from your collection.", Toast.LENGTH_SHORT).show()
            } else {
                val deleteFragment: DialogFragment = DeleteFragment.newInstance()
                deleteFragment.show(supportFragmentManager, "ROB")
            }
        }
    }

    fun myCardPreview(image: String, name: String, set: String, rarity: String) {
        val cardFragment: DialogFragment = CardFragment.newInstance(image, name, set, rarity)
        cardFragment.show(supportFragmentManager, "ROB")
    }


    fun deletePrompt(result : Int): Int {
        if (result == 1) {
            for (card in listOfDelCards) {
                dbman.delete(card.name, card.id, card.set, card.type, card.rarity, card.image, card.cardId)
                listOfMyCards.remove(card)
            }
            listOfDelCards.clear()
            Toast.makeText(applicationContext, "The cards have been successfully deleted from your collection.", Toast.LENGTH_SHORT).show()
            showList()
        }
        return listOfDelCards.size
    }

    fun showList() {
        listOfMyCards.clear()
        val allRows = dbman.readAllRows()
        runOnUiThread {
            for (card in allRows) {
                Log.d("HiSophie", "database saved: $card")
                listOfMyCards.add(card)
            }
        }
        val adapter = MyCollectionAdapter(this, listOfMyCards)
        var recycler: RecyclerView = findViewById(R.id.recycler_view)

        recycler.adapter = adapter
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recycler.layoutManager = GridLayoutManager(this, 3)
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recycler.layoutManager = GridLayoutManager(this, 5)
        }
    }

    // This method isn't being called (delete later)
    override fun onConfigurationChanged(newConfig: Configuration) {
        listOfDelCards.clear()
        Log.d("rotate", "the device has been rotated")
        super.onConfigurationChanged(newConfig)
    }

}