package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MovieListActivity : Activity() {

    var listArray : ArrayList<String> = ArrayList<String>()
    lateinit var  movielistAdapter : MovieListAdapter
    lateinit var movieView: ListView
    lateinit var progressBar: ProgressBar
    lateinit var searchBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)

        listArray.add("A Quiet Place")
        listArray.add("The Hunger Games")
        listArray.add("Passengers")
        listArray.add("Frozen")
        listArray.add("Spy Kids 3D")

        searchBtn = findViewById<Button>(R.id.searchBtn)

        searchBtn.setOnClickListener{
            Toast.makeText(this, "Get Out Of Here", Toast.LENGTH_SHORT).show()
        }




        progressBar = findViewById<ProgressBar>(R.id.progressBar)
       // progressBar.visibility = View.VISIBLE

        movieView = findViewById<ListView>(R.id.MovieView)

        movielistAdapter = MovieListAdapter(this)

        movieView?.setAdapter(movielistAdapter)


        movieView?.setOnItemClickListener{_, _, position, _ ->
            val selectedMovie = listArray[position]
            val detailIntent = Intent(this, MoviesDetailsActivity::class.java)

            startActivity(detailIntent)
        }

    }

    inner class MovieListAdapter(ctx : Context): ArrayAdapter<String>(ctx, 0){
        override fun getCount() : Int{
            return listArray.size
        }

        override fun getItem(position : Int) : String{
            return listArray.get(position)
        }
        override fun getView(position : Int, convertView: View?, parent : ViewGroup) : View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View
            result = inflater.inflate(R.layout.moviesview_list_item, null)

            val movie_title = result.findViewById<TextView>(R.id.movieListTitle)
            val movie_desc = result.findViewById<TextView>(R.id.movieListDesc)

            movie_title.setText(getItem(position))
//            movie_desc.setText("Based off of a made up story, one made by none other than Jim Halpert, paper salesman")

            return result
        }

        override fun getItemId(position : Int):Long{
            val something = 3
            return something.toLong()
        }
    }
}
