package com.example.pokmoncardlist

import android.R.attr.bitmap
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream


class MyDatabaseManager(context: Context) : SQLiteOpenHelper(context, "MyDB", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {

        db?.execSQL("CREATE TABLE IF NOT EXISTS CARDS(name, id, setName, type, rarity, image, cardId, PRIMARY KEY (cardId))")

        db?.execSQL("CREATE TABLE IF NOT EXISTS PHOTOS(photo, centering, corners, edges, surface)")



    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun delete(name: String, id: String, set: String, type: String, rarity: String, image: String, cardId: String) {
        writableDatabase.execSQL("DELETE FROM CARDS WHERE cardId='"+cardId+"'" )
    }

    // Insert a card into the database
    fun insert(name: String, id: String, set: String, type: String, rarity: String, image: String, cardId: String) {
        writableDatabase.execSQL("INSERT INTO CARDS VALUES(\"$name\", \"$id\", \"$set\", \"$type\", \"$rarity\", \"$image\", \"$cardId\")")
    }

    // Insert a photo into the database
    fun insertPhoto(photo : ByteArray, centering : Float, corners : Float, edges : Float, surface : Float) {
        /*
        val buffer = ByteArrayOutputStream(photo.getWidth() * photo.getHeight())
        photo.compress(CompressFormat.PNG, 100, buffer)
        var result = buffer.toByteArray()

        var stream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 0, stream)
        var photoByteArr = stream.toByteArray()*/
        var contentvalues = ContentValues()
        contentvalues.put("Image", "MyImage")
        contentvalues.put("Image", photo)

        writableDatabase.execSQL("INSERT INTO PHOTOS VALUES(\"$photo\", \"$centering\", \"$corners\", \"$edges\", \"$surface\")")
    }

    // Insert a photo into the database
    fun deletePhoto(photo : Bitmap, centering : Double, corners : Double, edges : Double, surface : Double) {
        writableDatabase.execSQL("DELETE FROM PHOTOS WHERE photo='"+photo+"'" )

    }

    fun containsCardId(cardId: String): Boolean {
        var result = false

        val cursor = writableDatabase.rawQuery("SELECT * FROM CARDS WHERE cardId LIKE \"$cardId\"", null)
        while (cursor.moveToNext()) {
            val foundId = cursor.getString(6)
            if (foundId.equals(cardId)) {
                result = true
                return result
            }
        }
        return result
    }

    // Read all rows from the database and return a list of strings
    fun readAllRows(): List<CardData> {
        var result = mutableListOf<CardData>()

        // read from database
        val cursor = writableDatabase.rawQuery("SELECT * FROM CARDS ORDER BY name ASC", null)
        // iterate over table of results and add the cardData to result
        while (cursor.moveToNext()) {
            val cardName = cursor.getString(0)
            val setId = cursor.getString(1)
            val cardSet = cursor.getString(2)
            val cardType = cursor.getString(3)
            val cardRarity = cursor.getString(4)
            val cardImage = cursor.getString(5)
            val cardId = cursor.getString(6)
            result.add(CardData(cardName, setId, cardSet, cardType, cardRarity, cardImage, cardId))
        }
        return result
    }

    // Read all rows from the database and return a list of strings
    fun readAllPhotos(): List<PhotoData> {
        var result = mutableListOf<PhotoData>()
        // read from database
        writableDatabase.execSQL("DELETE FROM PHOTOS")
        val cursor = writableDatabase.rawQuery("SELECT * FROM PHOTOS", null)
        // iterate over table of results and add the cardData to result
        while (cursor.moveToNext()) {
            var photoByteArr = cursor.getBlob(0)
            Log.d("HailHydra","$photoByteArr")
            var photo = BitmapFactory.decodeByteArray(photoByteArr, 0, photoByteArr.size)

            val centering = cursor.getFloat(1)
            Log.d("HailHydra","$centering")
            val corners = cursor.getFloat(2)
            val edges = cursor.getFloat(3)
            val surface = cursor.getFloat(4)
            //result.add(PhotoData(photo, centering, corners, edges, surface))
        }
        return result
    }
}