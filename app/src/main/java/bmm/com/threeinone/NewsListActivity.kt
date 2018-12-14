package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Xml
import android.widget.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.net.HttpURLConnection
import java.net.URL
import java.text.FieldPosition
import android.Manifest.permission.INTERNET
import android.app.AlertDialog
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem

data class NewsArticle (
    var newsTitle: String?,
    var newsDescription: String?,
    var newsLink: String?,
    var newsImage: String?

)

class NewsListActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var listArray : ArrayList<NewsArticle> = ArrayList<NewsArticle>()
    lateinit var newsListAdapter : NewsListAdapter
    lateinit var news_view : ListView
    lateinit var progress_bar : ProgressBar

inner class NewsListQuery : AsyncTask<String, Integer, String>(){

    override fun doInBackground(vararg p0: String?): String {

        val url = URL("https://www.cbc.ca/cmlink/rss-world")

        val connection = url.openConnection() as HttpURLConnection //goes to the server
        val response = connection.getInputStream() //connects to server and gets response back

        val factory = XmlPullParserFactory.newInstance()
        factory.setNamespaceAware(false)
        val xpp = factory.newPullParser()
        xpp.setInput(response, "UTF-8")

        var title: String? = null
        var link: String? = null
        var description: String? = null


        var article: NewsArticle? = null

        while (xpp.eventType != XmlPullParser.END_DOCUMENT) {

            var tagName = xpp.name

    // create a news article object - conditional that checks to see if it is full
            when (xpp.eventType) {

                XmlPullParser.START_TAG -> {
                    if (tagName.equals("item")) {
                        article = NewsArticle(null, null, null, null)
                    }


                    if(tagName.equals("title")) {
                        //going to set the obj to whats inside the tag using nextText()
                        title = xpp.nextText()
                        article?.newsTitle = title

                    }

                    if(tagName.equals("description")) {
                     //going to set the obj to whats inside the tag using nextText()
                     description = xpp.nextText()
                    var imgData:String? = Regex("<[^>]*>").find(description.toString())?.value

                        var imgData2 = Regex("<[^>]*>").find(description.toString())
                        var src:String? = ""
                        var articleData:String? = ""
      if(imgData != null) {
          try {
              Log.i("tag", Regex("\\<img.+src\\=(?:\\\"|\\')(.+?)(?:\\\"|\\')(?:.+?)\\>").find(imgData.toString())!!.groups[1]?.value)
              src = Regex("\\<img.+src\\=(?:\\\"|\\')(.+?)(?:\\\"|\\')(?:.+?)\\>").find(imgData.toString())!!.groups[1]?.value
              articleData =  description.toString().replace(imgData!!.toString(),"").replace("<p>","").replace("</p>","")
          } catch (err : KotlinNullPointerException) {
              Log.e("Error", "Null pointer exception stuff $err")
              src = "https://i.cbc.ca/1.4945072.1544732191!/fileImage/httpImage/image.jpg_gen/derivatives/16x9_460/afp-z4294.jpg"
              articleData = "[Error getting article]"
          }

      }
                        val re = Regex("\\(?\\b(https://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]")
                        val matchResult = re.find(description)

                       if (matchResult != null) {
                           article?.newsImage = src
//                           Log.i("Somethign", matchResult.value)
                       }

                     article?.newsDescription =  articleData

                    }

                    if(tagName.equals("link")) {
                        //going to set the obj to whats inside the tag using nextText()
                        link = xpp.nextText()
                        article?.newsLink = link
                    }


                }
                // IN THE END TAG
                // conditional that checks if the news obj is full
                // if it is, add it to the listarray
                // if it isn't then keep going
                XmlPullParser.END_TAG -> {
                    if (article?.newsTitle != null && article?.newsLink != null && article?.newsDescription != null) {
                        listArray.add(article)
                        article = null
                        publishProgress()
                    }
                }
            }

            xpp.next()
        }

        return "done"

    }

    override fun onProgressUpdate(vararg values: Integer?) {
        super.onProgressUpdate(*values)

        newsListAdapter.notifyDataSetChanged()
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        newsListAdapter.notifyDataSetChanged()
    }
}



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        var toolbar = findViewById<Toolbar>(R.id.TheToolbar) //import android.support.v7.widget.Toolbar
        toolbar.setTitle(R.string.news_activity_name)
        setSupportActionBar(toolbar) // show toolbar

        //add navigation to toolbar
        var drawer = findViewById<DrawerLayout>(R.id.bus_drawer_layout)
        var toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        var navView = findViewById<NavigationView>(R.id.bus_navigation_view)
        navView.setNavigationItemSelectedListener(this)


        var newsToolbar = findViewById<Toolbar>(R.id.news_toolbar)
        newsToolbar.setTitle("News Reader")
        setSupportActionBar(newsToolbar);

        //add navigation to toolbar
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        var toggle = ActionBarDrawerToggle(this, drawer, newsToolbar, R.string.open, R.string.close)

        drawer.addDrawerListener(toggle)
        toggle.syncState()

        var navView = findViewById<NavigationView>(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)


        var myQuery = NewsListQuery()
        myQuery.execute()


        progress_bar = findViewById<ProgressBar>(R.id.progress_bar)

        news_view = findViewById<ListView>(R.id.news_list)

        newsListAdapter = NewsListAdapter(this)

        news_view?.setAdapter(newsListAdapter)

        news_view?.setOnItemClickListener {_, _, position, _->
            val selected = listArray[position]
            val detailIntent = Intent(this, NewsDetailsActivity::class.java)
            detailIntent.putExtra("title", listArray.get(position).newsTitle)
            detailIntent.putExtra("image", listArray.get(position).newsImage)
            detailIntent.putExtra("description", listArray.get(position).newsDescription)
            detailIntent.putExtra("link", listArray.get(position).newsLink)

            startActivity(detailIntent)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.news_menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.news_toolbar_bookmark -> {

                var intent = Intent(this, NewsFavouritesActivity::class.java)
                startActivity(intent)

            }
            R.id.news_toolbar_statistics -> {
                var builder = AlertDialog.Builder(this);
                builder.setTitle("Statistics")
//                builder.setMessage()
                //    stats()
// Add the buttons
                builder.setPositiveButton("Ok", { dialog, id ->
                    //user clicked OK button

                    //finish()
                })

                var dialog = builder.create()
                dialog.show()
            }

            R.id.news_toolbar_about -> {

                var newsAbout = layoutInflater.inflate(R.layout.news_about_dialog, null)
                var builder = AlertDialog.Builder(this);
                builder.setTitle("About")
                builder.setView(newsAbout)
                builder.setPositiveButton("Ok", { dialog, id -> })

                var dialog = builder.create()
                dialog.show()
            }
        }
        return true
    }

    fun stats() {
        lateinit var dbHelper : TheDatabaseHelper
        lateinit var db : SQLiteDatabase
        val NEWS_TABLE = "FavouriteNews"
        val NEWS_IMAGE = "NewImage"
        val NEWS_TITLE = "NewsTitle"
        val NEWS_DESC = "NewDescription"
        val NEWS_LINK = "NewsLink"
        dbHelper = TheDatabaseHelper(this)
        db = dbHelper.writableDatabase

        lateinit var results  : Cursor

        results = db.query(NEWS_TABLE, arrayOf("_id", NEWS_TITLE, NEWS_DESC, NEWS_LINK, NEWS_IMAGE), null, null, null, null, null)

        results.moveToFirst()

        var titleIndex = results.getColumnIndex(NEWS_TITLE)
        var descIndex = results.getColumnIndex(NEWS_DESC)
        var linkIndex = results.getColumnIndex(NEWS_LINK)
        var imageIndex = results.getColumnIndex(NEWS_IMAGE)
        var count = 0
//            title.text =

        while (!results.isAfterLast()) {
            var thisTitle = results.getString(titleIndex)
            var thisDesc = results.getString(descIndex)
            var thisLink = results.getString(linkIndex)
            var thisImage = results.getString(imageIndex)

            //newsFavArray.add(NewsArticle(thisTitle, thisDesc, thisLink, thisImage))

            results.moveToNext()
        }
    }





    inner class NewsListAdapter(ctx : Context) : ArrayAdapter<NewsArticle>(ctx, 0) {
        /**
         *
         * Getting the item from the bus list array.
         *
         * @param  position the index of the position in the array
         * @return returns the item from the array
         */
        override fun getCount() : Int {
            return listArray.size
        }


        /**
         *
         * Getting the item from the bus list array.
         *
         * @param  position the index of the position in the array
         * @return returns the item from the array
         */
        override fun getItem(position: Int) : NewsArticle {
            return listArray.get(position)
        }

        /**
         *
         * This inflates and add a view for an item in the listView.
         *
         * @param position the index of the position in the array
         * @param convertView
         * @param parent the parent view for the adapter
         * @return returns the view for the item in the bus list array
         */
        override fun getView(position: Int, convertView: View?, parent : ViewGroup): View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View

            result = inflater.inflate(R.layout.news_list_item, null)


            val item_name = result.findViewById<TextView>(R.id.news_title)
            item_name.setText(getItem(position).newsTitle)

            return result
        }


        /**
         *
         * gets the ID for the item in the array. Currently only returns the index of array item.
         *
         * @return the index position as a Long
         */
        override fun getItemId(position: Int):Long {
            return position.toLong()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.movie_item -> {
                var resultIntent = Intent(this, MovieListActivity::class.java) // <-- change
                startActivity(resultIntent)
                finish()
            }
            R.id.food_item -> {
                var resultIntent = Intent(this, FoodListActivity::class.java) // <-- change
                startActivity(resultIntent)
                finish()
            }
            R.id.news_item -> {
                var resultIntent = Intent(this, NewsListActivity::class.java) // <-- change
                startActivity(resultIntent)
                finish()
            }
            R.id.bus_item -> {
                var resultIntent = Intent(this, BusSearchActivity::class.java) // <-- change
                startActivity(resultIntent)
                finish()
            }
        }

        var drawer = findViewById<DrawerLayout>(R.id.bus_drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

}
