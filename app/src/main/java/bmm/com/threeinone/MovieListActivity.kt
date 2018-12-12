package bmm.com.threeinone

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.design.widget.NavigationView
import android.support.v4.app.NotificationCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Xml
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import kotlinx.android.synthetic.main.activity_movies_details.*
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.net.MalformedURLException
import java.net.URLEncoder


/**
 * Title: Movie Information
 * @author: Josh Boose
 * Date: December 12th 2018
 */


/**
 *The two data classes set up all of the movie items such as
 * @param movieTitle
 * @param movieYear
 * @param movieposter
 * @param movieDesc
 * @param movieRating
 * @param movieRuntime
 * @param movieActors
 * @param moviePosterURL
 */
data class Movie(var moviePosterURL: String,
                 var movieTitle: String,
                 var movieYear: String,
                 var movieDesc: String,
                 var movieRating: String,
                 var movieRuntime: String,
                 var movieActors: String
                 )

data class MovieListItem(var movieTitle: String,
                         var movieYear: String,
                         var moviePoster: Bitmap?,
                         var movieDesc: String,
                         var movieRating: String,
                         var movieRuntime: String,
                         var movieActors: String
                         )




class MovieListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var listArray : ArrayList<MovieListItem> = ArrayList<MovieListItem>()
    lateinit var  movielistAdapter : MovieListAdapter
    lateinit var movieView: ListView
    lateinit var progressBar: ProgressBar
    lateinit var searchBtn : Button
   lateinit var error : TextView

    /**
     * @param savedInstanceState
     * In the onCreate function we are making things such as the
     * favBtn, searchBtn, etc functional
     */
    @RequiresApi(Build.VERSION_CODES.CUPCAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_list)



        var toolbar = findViewById<Toolbar>(R.id.movieListToolbar)
        toolbar.setTitle(R.string.movie_activity_name)
        setSupportActionBar(toolbar)


        //add navigation to toolbar:
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        var toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()
        var navView = findViewById<NavigationView>(R.id.movie_navigation_view)
        navView.setNavigationItemSelectedListener(this)





        progressBar = findViewById<ProgressBar>(R.id.movieProgressBar)
        error = findViewById(R.id.movieError)
error.visibility = View.INVISIBLE
        searchBtn = findViewById<Button>(R.id.MovieSearchBtn)
        var favBtn = findViewById<Button>(R.id.MovieFavBtn)


        favBtn.setOnClickListener {
            val detailIntent = Intent(this, MovieFavoriteActivity::class.java)
            startActivity(detailIntent)
        }

        searchBtn.setOnClickListener{

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(movieView.getWindowToken(), 0)
            progressBar?.visibility = View.VISIBLE
            if (listArray.isNotEmpty()){
                listArray.clear()
            }
            error.visibility = View.INVISIBLE

            Toast.makeText(this, R.string.gettingResults, Toast.LENGTH_SHORT).show()

            var edit = findViewById<EditText>(R.id.movieSearchBar)
            var myQuery = MovieListQuery()
            myQuery.execute(edit.text.toString())
        }







        movieView = findViewById(R.id.MovieView)

        movielistAdapter = MovieListAdapter(this)

        movieView?.setAdapter(movielistAdapter)




        movieView?.setOnItemClickListener{_, _, position, _ ->
            val selectedMovie = listArray[position]
            val detailIntent = Intent(this, MoviesDetailsActivity::class.java)
            var bStream = ByteArrayOutputStream();
            listArray.get(position).moviePoster?.compress(Bitmap.CompressFormat.PNG, 100, bStream);
            var byteArray = bStream.toByteArray();

            detailIntent.putExtra("title", listArray.get(position).movieTitle)
            detailIntent.putExtra("year", listArray.get(position).movieYear)
            detailIntent.putExtra("poster", byteArray)
            detailIntent.putExtra("desc", listArray.get(position).movieDesc)
            detailIntent.putExtra("rating", listArray.get(position).movieRating)
            detailIntent.putExtra("runtime", listArray.get(position).movieRuntime)
            detailIntent.putExtra("actors", listArray.get(position).movieActors)


            startActivity(detailIntent)
        }


    }


    /**
     * @param menu
     * @return in the onCreateOptionsMenu function, we are
     * going to return true
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        return true
    }







    /**
     * @param item
     * @return this is where one of our 4 activities gets called and opened from the toolbar.
     * onNavigationItemSelected function is based solely on this and closing the drawer.
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.movie_item -> {
                val navIntent = Intent(this, MovieListActivity::class.java)
                startActivity(navIntent)
            }
            R.id.food_item -> {
                val navIntent = Intent(this, BusListActivity::class.java)
                startActivity(navIntent)
            }
            R.id.news_item -> {
                val navIntent = Intent(this, NewsListActivity::class.java)
                startActivity(navIntent)
            }
            R.id.bus_item -> {
                val navIntent = Intent(this, BusSearchActivity::class.java)
                startActivity(navIntent)
            }
        }

        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)

        return true




    }















    inner class MovieListAdapter(ctx : Context): ArrayAdapter<MovieListItem>(ctx, 0){


        /**
         * @return the size of the list array gets returned here in the getCount function
         */
        override fun getCount() : Int{

            return listArray.size
        }






        /**
         * @param position
         * @return the item's position gets returned here in the getItem function
         *
         */
        override fun getItem(position : Int) : MovieListItem {

            return listArray.get(position)
        }





        /**
         * @param position
         * @param convertView
         * @param parent
         * @return the view gets inflated with result here in the getView function and
         * the movie title, poster and year are displayed on the view
         */
        override fun getView(position : Int, convertView: View?, parent : ViewGroup) : View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View
            result = inflater.inflate(R.layout.moviesview_list_item, null)

            val movie_title = result.findViewById<TextView>(R.id.movieListTitle)
            val movie_year = result.findViewById<TextView>(R.id.movieListYear)
            val movie_poster = result.findViewById<ImageView>(R.id.movieListPoster)

            movie_title.setText(getItem(position).movieTitle)
           movie_poster.setImageBitmap(getItem(position).moviePoster)
            movie_year.setText(getItem(position).movieYear)



            return result
        }




        /**
         * @param position
         * @return the item Id is returned here in the getItemId function
         */
        override fun getItemId(position : Int):Long{
            val something = 3


            return something.toLong()
        }
    }






    inner class MovieListQuery : AsyncTask<String, Integer, String>() {


      var errorType = ""



        override fun doInBackground(vararg params: String): String {

            var query = URLEncoder.encode(params[0], "UTF-8")

            val url = URL ("http://www.omdbapi.com/?apikey=ea523988&r=xml&t=$query")
            var connection = url.openConnection() as HttpURLConnection //goes to the server
            var response = connection.getInputStream()
            /**
             * The url is created here and we are starting
             * a new pull parser
             */

            val factory = XmlPullParserFactory.newInstance()
            factory.setNamespaceAware(false)
            val xpp = factory.newPullParser()
            xpp.setInput(response, "UTF-8")  //read XML from server


            while (xpp.eventType != XmlPullParser.END_DOCUMENT)
            {
                when(xpp.eventType){
                    XmlPullParser.START_TAG -> {
                        if(xpp.name == "movie"){
                          val title = xpp.getAttributeValue(null, "title")
                            val year = xpp.getAttributeValue(null, "year")

                            val desc = xpp.getAttributeValue(null, "plot")
                            val rating = xpp.getAttributeValue(null, "imdbRating")
                            val runtime = xpp.getAttributeValue(null, "runtime")
                           val actors = xpp.getAttributeValue(null, "actors")

                            /**
                             * This whole section inside the XmlPullParser is dedicated to grabbing all
                             * of the movie data and setting them to actual variable names
                             */
                            val poster = xpp.getAttributeValue(null, "poster")
                            var imageBitmap : Bitmap? = null
                            if (poster != null){


                                imageBitmap = getImage(poster)


                                val outputStream = openFileOutput("$title.png", Context.MODE_PRIVATE);
                                imageBitmap?.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                outputStream.flush();
                                outputStream.close();
                            }



                            val movieListObj = MovieListItem(title, year, imageBitmap, desc, rating, runtime, actors )
                            listArray.add(movieListObj)

                            /**
                             * @param params
                             * In this section, we are creating an object that holds
                             * all of the list items of the movie, and then adding
                             * it to the array
                             */
                        }

                        if(xpp.name == "error"){
                            errorType = xpp.nextText()
                            publishProgress()
                            /**
                             * @param params
                             * @return In this if statement, we are checking if an error is found, if so, we return
                             * "Error!"
                             */
                            return "Error!"
                        }




                        publishProgress() //causes android to call onProgressUpdate when GUI is ready


                    }
                }


                xpp.next() // go to next XML element

            }

            /**
             * @param params
             * @return Finished is returned once all the data has been grabbed and parsed
             */

            return "Finished"
        }





        override fun onProgressUpdate(vararg values: Integer?) {
            movielistAdapter.notifyDataSetChanged()

            /**
             * @param values
             *
             */

        }


        override fun onPostExecute(result: String?) {
            //at the end

            /**
             * @param result
             * Once a movie has been searched, the progress bar gets hidden
             * and if the list array is empty, the error message gets displayed
             */
            progressBar.visibility = View.INVISIBLE //hides progress bar
            if(listArray.isEmpty()) {
                error.setText(errorType)
                error.visibility = View.VISIBLE
            }
        }

        fun getImage(url: URL): Bitmap? {

            /**
             * This section is all dedicated to getting the image
             * by using the url and using connection to set up the HttpURLConnection
             */

            var connection: HttpURLConnection? = null
            try {
                connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val responseCode = connection.responseCode
                /**
                 * @return if the responseCode is equal to 200,
                 * bitmapFactory is then used for decodeStream
                 */
                return if (responseCode == 200) {
                    BitmapFactory.decodeStream(connection.inputStream)
                } else
                    null
            } catch (e: Exception) {
                /**
                 * @return null is returned in the catch
                 */
                return null
            } finally {
                connection?.disconnect()
            }
        }

        fun getImage(urlString: String): Bitmap? {
            try {
                val url = URL(urlString)
                /**
                 * @param urlString
                 * @return url in getImage is returned here
                 */
                return getImage(url)
            } catch (e: MalformedURLException) {
                return null
                /**
                 * @return null is returned in the catch
                 */
            }

        }







    }







}
