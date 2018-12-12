package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*


data class FavoriteMovies(
    var favTitle: String?,
    var favDesc: String?
)

class MovieFavoriteActivity : AppCompatActivity() {

    var favListArray : ArrayList<FavoriteMovies> = ArrayList<FavoriteMovies>()
    lateinit var  movieFavAdapter : MovieFavListAdapter
    lateinit var favView: ListView
    lateinit var db : SQLiteDatabase
    lateinit var results : Cursor
    lateinit var dbHelper : FavDatabaseHelper
    val MOVIE_TABLE = "FavoriteMovies"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_favorite)

        var statsBtn = findViewById<Button>(R.id.statsBtn)
        favView = findViewById<ListView>(R.id.MovieFavView)
//
        statsBtn.setOnClickListener {
            startActivity(Intent(this, MovieStatsActivity::class.java))
        }

        movieFavAdapter = MovieFavListAdapter(this)

        favView.adapter = movieFavAdapter


        dbHelper = FavDatabaseHelper(this) //get a helper object
        db = dbHelper.writableDatabase //open your database


        results = db.query( MOVIE_TABLE, arrayOf("_id", "FavoritesTitle", "FavoritesDesc" ), null, null,null,null,null)
        results.moveToFirst()

        var idIndex = results.getColumnIndex("_id")
        var title = results.getColumnIndex("FavoritesTitle")
        var desc = results.getColumnIndex("FavoritesDesc")


        while (!results.isAfterLast()){
            var thisID = results.getInt(idIndex)
            favListArray.add(FavoriteMovies(results.getString(title), results.getString(desc)))
            results.moveToNext()
        }

        Log.i("Test", favListArray.toString())
    }


    fun deleteMovie(position: Int){

        val idQuery = db.query(MOVIE_TABLE, arrayOf("_id"), " $KEY_TITLE = ? ", arrayOf(favListArray.get(position).favTitle), null, null, null)
        idQuery.moveToFirst()
        var idIndex = idQuery.getColumnIndex("_id")
        var id = idQuery.getInt(idIndex)
        db.delete(MOVIE_TABLE, "_id=$id", null)
        results = db.query( MOVIE_TABLE, arrayOf("_id", KEY_TITLE, KEY_DESC ), null, null,null,null,null)

        //copy from above
        favListArray.removeAt(position)
        movieFavAdapter.notifyDataSetChanged()
    }

    inner class MovieFavListAdapter(ctx : Context): ArrayAdapter<FavoriteMovies>(ctx, 0){
        override fun getCount() : Int{
            return favListArray.size
        }

        override fun getItem(position : Int) : FavoriteMovies {
            return favListArray.get(position)
        }
        override fun getView(position : Int, convertView: View?, parent : ViewGroup) : View? {
            var inflater = LayoutInflater.from(parent.getContext())




            var result: View
            result = inflater.inflate(R.layout.movie_favorites_list_item, null)

            var deleteBtn = result.findViewById<TextView>(R.id.movieDelete)

            deleteBtn.setOnClickListener{
                deleteMovie(position)
            }

            val movie_title = result.findViewById<TextView>(R.id.favTitle)
            val movie_desc = result.findViewById<TextView>(R.id.favDesc)

            movie_title.setText(getItem(position).favTitle)
            movie_desc.setText(getItem(position).favDesc)

            return result
        }

        override fun getItemId(position : Int):Long{
            val something = 3
            return something.toLong()
        }
    }







//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        val titleFav = findViewById<TextView>(R.id.favTitle)
//
//        val descFav = findViewById<TextView>(R.id.favDesc)
//
//
//
//        var title = data?.getStringExtra("title")
//        var desc = data?.getStringExtra("desc")
//
//
//
//
//
//
//        titleFav.setText(title)
//
//        descFav.setText(desc)
//
//    }
}



