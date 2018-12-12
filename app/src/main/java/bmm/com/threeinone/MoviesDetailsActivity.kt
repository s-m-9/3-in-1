package bmm.com.threeinone



import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

import android.util.Log
import android.widget.*
import android.support.design.widget.Snackbar
import java.io.ByteArrayOutputStream
import android.R.attr.duration
import bmm.com.threeinone.R.layout.activity_movies_details


class MoviesDetailsActivity: AppCompatActivity() {


    /**
     *
     * Shows the details of the movies activity
     *
     * @param savedInstanceState
     *
     */



    lateinit var db : SQLiteDatabase
    lateinit var results : TheDatabaseHelper
    val MOVIE_TABLE = "FavoriteMovies"

//    lateinit var myAdapter : MyAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_details)


        dbHelper = TheDatabaseHelper(this) //get a helper object
        db = dbHelper.writableDatabase //open your database


        var addBtn = findViewById<Button>(R.id.addBtn)

        addBtn.setOnClickListener{
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Are you sure you want to save this to favorites?")

            builder.setPositiveButton("OK", {dialog, id ->

//                    val selectedMovie = listArray[position]

                val newRow = ContentValues()
                newRow.put(KEY_TITLE, title)
                newRow.put(KEY_DESC, desc)
                newRow.put(KEY_RUNTIME, runtime)
                newRow.put(KEY_YEAR, year)


                db.insert(MOVIE_TABLE, "", newRow)
//                results = db.query( MOVIE_TABLE, arrayOf("_id", KEY_TITLE ), null, null,null,null,null)



//                var s = results.getCount()
                Snackbar.make(addBtn, "Saved To Favorites", Snackbar.LENGTH_LONG).show();


            })
            builder.setNegativeButton("Cancel", {dialog, id ->

            })

            var dialog = builder.create()
            dialog.show()
        }




        onActivityResult(50, 2, intent)
    }


     var title : String? = null
     var desc : String? = null
    var runtime : String? = null
    var year : String? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val titleView = findViewById<TextView>(R.id.movieTitle)
        val yearView = findViewById<TextView>(R.id.movieYear)
        val posterView = findViewById<ImageView>(R.id.moviePosterURL)
        val descView = findViewById<TextView>(R.id.movieDesc)
        val ratingView = findViewById<TextView>(R.id.movieRating)
        val runtimeView = findViewById<TextView>(R.id.movieRuntime)
        val actorsView = findViewById<TextView>(R.id.movieActors)



        title = data?.getStringExtra("title")
        year = data?.getStringExtra("year")
        var poster = data?.getByteArrayExtra("poster")
         desc = data?.getStringExtra("desc")
        var rating = data?.getStringExtra("rating")
        runtime = data?.getStringExtra("runtime")
        var actors = data?.getStringExtra("actors")

        var bmp = BitmapFactory.decodeByteArray(poster, 0, poster!!.size);





        titleView.setText(title)
        yearView.setText(year)
        posterView.setImageBitmap(bmp)
        descView.setText(desc)
        ratingView.setText(rating)
        runtimeView.setText(runtime)
        actorsView.setText(actors)

    }




}



