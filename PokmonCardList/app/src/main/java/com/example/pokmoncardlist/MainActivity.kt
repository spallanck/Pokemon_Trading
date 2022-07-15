package com.example.pokmoncardlist

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread


// This is a list of random numbers. It is the model for the recycler.
var listOfCards: MutableList<CardData> = mutableListOf()
var subListOfCards: MutableList<CardData> = mutableListOf()

// A view class that represents a single item in the list.
class MyListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cardImage: ImageView = view.findViewById(R.id.card_image)
    var checkBox: CheckBox = view.findViewById(R.id.checkbox)

    var checkBoxArr = SparseBooleanArray()
    init {
        checkBox.setOnClickListener {
            if (!checkBoxArr.get(adapterPosition, false)) {
                checkBox.isChecked = true
                checkBoxArr.put(adapterPosition, true)
                if (!subListOfCards.contains(listOfCards[adapterPosition])) {
                    subListOfCards.add(listOfCards[adapterPosition])
                }
            } else {
                checkBox.isChecked = false
                checkBoxArr.put(adapterPosition, false)
                if (subListOfCards.contains(listOfCards[adapterPosition])) {
                    subListOfCards.remove(listOfCards[adapterPosition])
                }
            }
        }
    }
}

class MyListAdapter(val activity: MainActivity, val model: MutableList<CardData>) : RecyclerView.Adapter<MyListItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return MyListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyListItemViewHolder, position: Int) {

        val urlstring = listOfCards[position].image
        Glide.with(activity).load(urlstring).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.cardImage)
        holder.checkBox.isChecked = holder.checkBoxArr.get(position, false)
        thread {
            activity.runOnUiThread {
                holder.cardImage.setOnClickListener {
                    Log.d("charizard", "clicked on card num ${position}")
                    (activity as MainActivity).cardPreview(listOfCards[position].image, listOfCards[position].name, listOfCards[position].set, listOfCards[position].rarity)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

}

class MainActivity : AppCompatActivity(), SearchFragment.SearchFragListener {

    val urlstring: String = "https://api.pokemontcg.io/v2/cards"
    var request: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbman = MyDatabaseManager(this) // the activity is the context that it runs in

        savedInstanceState?.let {
            request = it.getString("request").toString()
            listOfCards.clear()
            subListOfCards.clear()      // would love to delete this line if we can figure out how to keep checkboxes checked
            makeList(request)
        }

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val searchFragment: DialogFragment = SearchFragment.newInstance(this)
            searchFragment.show(supportFragmentManager, "ROB")
        }

        val saveButton: Button = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            if (subListOfCards.isEmpty()) {
                Toast.makeText(applicationContext, "No cards are selected to add to your collection.", Toast.LENGTH_SHORT).show()
            } else {
                for (card in subListOfCards) {
                    if (!dbman.containsCardId(card.cardId)) {
                        dbman.insert(
                            card.name,
                            card.id,
                            card.set,
                            card.type,
                            card.rarity,
                            card.image,
                            card.cardId
                        )
                    }
                }
                subListOfCards.clear()
                val allRows = dbman.readAllRows()
                runOnUiThread {
                    for (card in allRows) {
                        Log.d("NewHydra", "$card")
                    }
                }
                Toast.makeText(
                    applicationContext,
                    "The cards have been successfully added to your collection.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

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
        bottomNav.selectedItemId = R.id.search
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.collection->startActivity(Intent(this, CollectionActivity::class.java)).also { finish() }
                R.id.camera->startActivity(Intent(this, CameraActivity::class.java)).also { finish() }
            }
            true
        }

    }

    override fun makeList(id: String) {
        val recycler: RecyclerView = findViewById(R.id.recycler_view)
        val adapter = MyListAdapter(this, listOfCards)
        recycler.adapter = adapter
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recycler.layoutManager = GridLayoutManager(this, 3)
        } else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recycler.layoutManager = GridLayoutManager(this, 5)
        }

        if (!request.equals("")) {
            thread {
                var pageNum = 1
                var moreCards = true

                while (moreCards) {
                    val url = URL("$request&page=$pageNum")
                    val content = url.readText()

                    val json = JSONObject(content)
                    val numCards = json.getString("totalCount")
                    Log.d("ROB", "total cards: $numCards")

                    val data = json.getJSONArray("data")
                    for (i in 0 until data.length()) {
                        var dict = data.getJSONObject(i)
                        var name = dict.getString("name")
                        var setId = dict.getJSONObject("set").getString("id")
                        var cardId = dict.getString("id")
                        var setName = dict.getJSONObject("set").getString("name")
                        var type = ""
                        if (dict.getString("supertype").equals("Pokémon")) {
                            var numTypes = dict.getJSONArray("types").length()
                            for (i in 0 until numTypes) {
                                type += "${dict.getJSONArray("types").getString(i)}"
                                if (i + 1 < numTypes) {
                                    type += ", "
                                }
                            }
                        } else {
                            type = "N/A"
                        }
                        var rarity = ""
                        if (dict.has("rarity")) {
                            rarity = dict.getString("rarity")
                        } else {
                            rarity = "N/A"
                        }
                        var images = dict.getJSONObject("images")
                        val largeImage = images.getString("large")

                        // Add it to the model
                        var cardData =
                            CardData(name, setId, setName, type, rarity, largeImage, cardId)
                        listOfCards.add(cardData)

                        Log.d("ROB", "${listOfCards[i]}")
                    }

                    if (listOfCards.size == numCards.toInt()) {
                        moreCards = false
                    } else {
                        pageNum++
                    }
                }
                // Tell the adapter that something changed but we have to use the UI thread
                runOnUiThread {
                    // Then tell the adapter that the data has changed
                    adapter.notifyItemChanged(listOfCards.size - 1)
                }
            }
        }
    }

    fun cardPreview(image: String, name: String, set: String, rarity: String) {
        val cardFragment: DialogFragment = CardFragment.newInstance(image, name, set, rarity)
        cardFragment.show(supportFragmentManager, "ROB")
    }

    override fun buildQuery(argc: Int, name: String, id: String, type: String): String {
        // Clear the card URLs so the previous search results are removed
        listOfCards.clear()
        subListOfCards.clear()

        if (argc > 0) {
            request = "${urlstring}?q="
            if (!name.isEmpty()) {
                if (!name.contains(" ")) {
                    request = "${request}name:*${name}* "
                } else {
                    request = "${request}name:\"${name}\" "
                }
            }
            if (!id.isEmpty()) {
                request = "${request}set.id:${id} "
            }
            if (!type.isEmpty()) {
                request = "${request}types:${type} "
            }
        } else {
            request = "$urlstring?"
        }
        Log.d("charRequest", "$request.")
        return "$request"

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("request", request)
        super.onSaveInstanceState(outState)
    }

}
