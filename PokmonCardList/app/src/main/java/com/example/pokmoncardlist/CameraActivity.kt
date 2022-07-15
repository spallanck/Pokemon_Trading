package com.example.pokmoncardlist

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


var listOfMyPhotos: MutableList<Bitmap> = mutableListOf()
var listOfMyPhotoData: MutableList<PhotoData> = mutableListOf()
var listOfDelPhotos: MutableList<Bitmap> = mutableListOf()
var listOfDelPhotoData: MutableList<PhotoData> = mutableListOf()

data class PhotoData(var photo : Bitmap, var centering : Float, var corners : Float, var edges : Float, var surface : Float)

// A view class that represents a single item in the list.
class MyCameraItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var cardImage: ImageView = view.findViewById(R.id.card_image)
    var checkBox: CheckBox = view.findViewById(R.id.checkbox)

    var checkBoxArr = SparseBooleanArray()
    init {
        checkBox.setOnClickListener {
            if (!checkBoxArr.get(adapterPosition, false)) {
                checkBox.isChecked = true
                checkBoxArr.put(adapterPosition, true)
                if (!listOfDelPhotos.contains(listOfMyPhotos[adapterPosition])) {
                    listOfDelPhotos.add(listOfMyPhotos[adapterPosition])
                    listOfDelPhotoData.add(listOfMyPhotoData[adapterPosition])
                }
            } else {
                checkBox.isChecked = false
                checkBoxArr.put(adapterPosition, false)
                if (listOfDelPhotos.contains(listOfMyPhotos[adapterPosition])) {
                    listOfDelPhotos.remove(listOfMyPhotos[adapterPosition])
                    listOfDelPhotoData.remove(listOfMyPhotoData[adapterPosition])
                }
            }
        }
    }
}

class MyCameraAdapter(val activity: CameraActivity, val model: MutableList<Bitmap>) : RecyclerView.Adapter<MyCameraItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCameraItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return MyCameraItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyCameraItemViewHolder, position: Int) {
        // Get the URL
        // Load it from the internet as a bitmap
        // Change the image to the bitmap that is read from the internet
        val urlstring = listOfMyPhotos[position]
        Glide.with(activity).load(urlstring).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.cardImage)
        holder.checkBox.isChecked = holder.checkBoxArr.get(position, false)
        thread {
            activity.runOnUiThread {
                holder.cardImage.setOnClickListener {
                    Log.d("charizard", "clicked on card num ${position + 1}")
                    (activity as CameraActivity).showRatingFrag(listOfMyPhotos[position], position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

}


class CameraActivity : AppCompatActivity() {
    val dbman = MyDatabaseManager(this)
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath: String
    internal var output: File? = null
    lateinit var imageView: ImageView
    var centeringRating : Float = 0f
    var cornersRating : Float = 0f
    var edgesRating : Float = 0f
    var surfaceRating : Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

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
        bottomNav.selectedItemId = R.id.camera
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.search->startActivity(Intent(this, MainActivity::class.java)).also { finish() }
                R.id.collection->startActivity(Intent(this, CollectionActivity::class.java)).also { finish() }
            }
            true
        }


        val captureButton: FloatingActionButton = findViewById(R.id.capture_button)
        imageView = findViewById(R.id.image_view)
        captureButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val date = Date()
            output = File(dir, "Image " + date.toString())

            //startActivityForResult(takePictureIntent, 1888)
            dispatchTakePictureIntent()

        }
        showList()

        val deleteButton: Button = findViewById(R.id.delete_button)
        deleteButton.setOnClickListener {
            if (listOfDelPhotos.isEmpty()) {
                Toast.makeText(applicationContext, "No photos are selected to delete from your collection.", Toast.LENGTH_SHORT).show()
            } else {
                for (photo in listOfDelPhotos) {
                    //delete photo from database here
                    listOfMyPhotos.remove(photo)

                }
                for (photoData in listOfDelPhotoData) {
                    listOfMyPhotoData.remove(photoData)
                }
                listOfDelPhotos.clear()
                listOfDelPhotoData.clear()
                Toast.makeText(applicationContext, "The photos have been successfully deleted from your collection.", Toast.LENGTH_SHORT).show()
                showList()
            }
            //else {
            //    val deleteFragment: DialogFragment = DeleteFragment.newInstance()
            //    deleteFragment.show(supportFragmentManager, "ROB")
            //}
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val imageView: ImageView = findViewById(R.id.image_view)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            //imageView.setImageBitmap(imageBitmap)
            listOfMyPhotos.add(imageBitmap)
            listOfMyPhotoData.add(PhotoData(imageBitmap, 0f, 0f, 0f, 0f))
            val buffer = ByteArrayOutputStream(imageBitmap.getWidth() * imageBitmap.getHeight())
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, buffer)
            var result = buffer.toByteArray()
            dbman.insertPhoto(result, centeringRating, cornersRating, edgesRating, surfaceRating)
            showList()
            //imageView.setOnClickListener {
                //val ratingFragment: DialogFragment = RatingFragment.newInstance()
                //ratingFragment.show(supportFragmentManager, "ROB")
            //}

        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
        }
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            Log.d("Hail", "this one here officer")
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    //...
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }

            }
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val imageView: ImageView = findViewById(R.id.image_view)
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            imageView.setImageBitmap(imageBitmap)
//        } else {
//            super.onActivityResult(requestCode, resultCode, data)
//        }
//    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        Log.d("Hail", timeStamp)
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.d("Hail", storageDir.toString())
        Log.d("Hail", "returning")
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun showList() {
        //listOfMyPhotos.clear()
        //listOfMyPhotoData.clear()
        var photos = dbman.readAllPhotos()

        runOnUiThread {
            for (photo in photos) {
                Log.d("HiSophie", "database saved: $photo")
                listOfMyPhotos.add(photo.photo)
                listOfMyPhotoData.add(photo)
            }
        }
        val adapter = MyCameraAdapter(this, listOfMyPhotos)
        var recycler: RecyclerView = findViewById(R.id.recycler_view)

        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(this, 3)
    }

    fun showRatingFrag(bitmap: Bitmap, position: Int) {
        val ratingFragment: DialogFragment = RatingFragment.newInstance(bitmap, listOfMyPhotoData[position].centering, listOfMyPhotoData[position].corners, listOfMyPhotoData[position].edges, listOfMyPhotoData[position].surface, position)
        ratingFragment.show(supportFragmentManager, "ROB")
    }

    fun setRatings(centering: Float, corners: Float, edges: Float, surface: Float, position: Int): MutableList<Float> {
        listOfMyPhotoData[position].centering = centering
        listOfMyPhotoData[position].corners = corners
        listOfMyPhotoData[position].edges = edges
        listOfMyPhotoData[position].surface = surface

        val results = mutableListOf<Float>(listOfMyPhotoData[position].centering, listOfMyPhotoData[position].corners, listOfMyPhotoData[position].edges, listOfMyPhotoData[position].surface)
        Log.d("ratingsYay", "$results")
        return results
    }

}