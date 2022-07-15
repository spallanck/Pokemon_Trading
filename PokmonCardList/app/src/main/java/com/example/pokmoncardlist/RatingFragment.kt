package com.example.pokmoncardlist

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import androidx.fragment.app.DialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [RatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RatingFragment : DialogFragment() {
    // TODO: Rename and change types of parameters


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_rating, container, false)

        val photo = view.findViewById<ImageView>(R.id.card_picture)
        photo.setImageBitmap(imageView)

        val xButton : ImageView = view.findViewById(R.id.imageButton)
        val centeringRatingBar = view.findViewById<RatingBar>(R.id.centering_rating)
        val cornerRatingBar = view.findViewById<RatingBar>(R.id.corners_rating)
        val edgeRatingBar = view.findViewById<RatingBar>(R.id.edges_rating)
        val surfaceRatingBar = view.findViewById<RatingBar>(R.id.surface_rating)
        centeringRatingBar.rating = centeringRating
        cornerRatingBar.rating = cornersRating
        edgeRatingBar.rating = edgesRating
        surfaceRatingBar.rating = surfaceRating
        xButton.setOnClickListener{
            (activity as CameraActivity).setRatings(centeringRatingBar.rating, cornerRatingBar.rating, edgeRatingBar.rating, surfaceRatingBar.rating, position)
            dismiss()

        }

        return view
    }


    companion object {

        lateinit var imageView : Bitmap
        var centeringRating : Float = 0f
        var cornersRating : Float = 0f
        var edgesRating : Float = 0f
        var surfaceRating : Float = 0f
        var position : Int = 0
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RatingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(image : Bitmap, center : Float, corner : Float, edge : Float, surface : Float, positionClick : Int) =
            RatingFragment().apply {
                arguments = Bundle().apply {
                    imageView = image
                    centeringRating = center
                    cornersRating = corner
                    edgesRating = edge
                    surfaceRating = surface
                    position = positionClick
                }
            }
    }
}