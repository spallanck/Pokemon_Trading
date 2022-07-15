package com.example.pokmoncardlist

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import kotlin.concurrent.thread

// This is a list of random numbers. It is the model for the recycler.
var cardurls: MutableList<String> = mutableListOf()

// A view class that represents a single item in the list.
class MyListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cardImage: ImageView = view.findViewById(R.id.card_image)
}

class MyListAdapter(val activity: MainActivity, val model: MutableList<String>) : RecyclerView.Adapter<MyListItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyListItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return MyListItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyListItemViewHolder, position: Int) {
        // Get the URL
        // Load it from the internet as/ a bitmap
        // Change the image to the bitmap that is read from the internet
        val urlstring = cardurls[position]
        thread {
            val url = URL(urlstring)
            // Open a connection as an input stream
            // Then use BitmapFactory to create a bitmap from the input stream
            val conn = url.openConnection()
            val inputStream = conn.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Update holder's image to be the newly downloaded bitmap
            // This must be done on the UI thread
            activity.runOnUiThread {
                holder.cardImage.setImageBitmap(bitmap)
            }
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val searchFragment: DialogFragment = SearchFragment.newInstance()
            searchFragment.show(supportFragmentManager, "ROB")
        }

        thread {
            val url = URL("https://api.pokemontcg.io/v2/sets")
            val content = url.readText()
            val json = JSONObject(content)
            Log.d("ROB", "$json")
            val data = json.getJSONArray("data").length()
            Log.d("ROB", "$data")
        }

        /*
        var cardNum = 0

        val recycler: RecyclerView = findViewById(R.id.recycler_view)
        val adapter = MyListAdapter(this, cardurls)
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(this, 3)

        val addButton: Button = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            thread {
                cardNum++
                val url = URL("https://api.pokemontcg.io/v2/cards/ex14-${cardNum}")
                val content = url.readText()
                Log.d("ROB", "$content")

                val json = JSONObject(content)
                val data = json.getJSONObject("data")
                val images = data.getJSONObject("images")
                val largeImage = images.getString("large")
                Log.d("ROB", "$largeImage")

                // Add it to the model
                cardurls.add(largeImage)
                // Tell the adapter that something changed but we have to use the UI thread
                runOnUiThread {
                    // Then tell the adapter that the data has changed
                    adapter.notifyItemChanged(cardurls.size - 1)
                    recycler.scrollToPosition(cardurls.size - 1)
                }
            }
        }
         */


    }
}