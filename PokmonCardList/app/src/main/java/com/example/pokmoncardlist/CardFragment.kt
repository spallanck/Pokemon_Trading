package com.example.pokmoncardlist

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import java.net.URL
import kotlin.concurrent.thread

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CardFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CardFragment : DialogFragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var image: String = ""
    private var name: String = ""
    private var set: String = ""
    private var rarity: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedInstanceState?.let {
            image = it.getString("image").toString()
            name = it.getString("name").toString()
            set = it.getString("set").toString()
            rarity = it.getString("rarity").toString()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_card, container, false)

        val largeCardImage: ImageView = view.findViewById(R.id.large_card_image)
        thread {
            val url = URL(image)
            // Open a connection as an input stream
            // Then use BitmapFactory to create a bitmap from the input stream
            val conn = url.openConnection()
            val inputStream = conn.getInputStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)
            // Update holder's image to be the newly downloaded bitmap
            // This must be done on the UI thread
            activity?.runOnUiThread {
                largeCardImage.setImageBitmap(bitmap)
            }
        }

        val cardName: TextView = view.findViewById(R.id.card_name)
        cardName.setText(name)
        val setName: TextView = view.findViewById(R.id.set_name)
        setName.setText(set)
        val cardRarity: TextView = view.findViewById(R.id.card_rarity)
        cardRarity.setText(rarity)
        val xButton : ImageView = view.findViewById(R.id.imageButton)
        xButton.setOnClickListener{
            dismiss()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CardFragment.
         */
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CardFragment.
         */

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(cardImageUrl: String, cardName: String, setName: String, cardRarity: String): CardFragment {
            val fragment = CardFragment().apply {
                image = cardImageUrl
                name = cardName
                set = setName
                rarity = cardRarity
            }
            return fragment
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("image", image)
        outState.putString("name", name)
        outState.putString("set", set)
        outState.putString("rarity", rarity)
        super.onSaveInstanceState(outState)
    }
}