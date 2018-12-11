package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import org.w3c.dom.Text

class NewsFavouritesActivity : Activity() {

    lateinit var dbHelper : TheDatabaseHelper
    lateinit var db : SQLiteDatabase
    val NEWS_TABLE = "FavouriteNews"
    val NEWS_IMAGE = "NewImage"
    val NEWS_TITLE = "NewsTitle"
    val NEWS_DESC = "NewDescription"
    val NEWS_LINK = "NewsLink"

    lateinit var newsFavListAdapter : NewsFavListAdapter

    val newsFavArray = ArrayList<NewsArticle>()
    lateinit var results  : Cursor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_favourites)

        val listView = findViewById<ListView>(R.id.NewsFavListView)
        newsFavListAdapter = NewsFavListAdapter(this)

        dbHelper = TheDatabaseHelper(this)
        db = dbHelper.writableDatabase

        listView?.setAdapter(newsFavListAdapter)

        results = db.query(NEWS_TABLE, arrayOf("_id", NEWS_TITLE, NEWS_DESC, NEWS_LINK, NEWS_IMAGE), null, null, null, null, null)

        results.moveToFirst()

        var titleIndex = results.getColumnIndex(NEWS_TITLE)
        var descIndex = results.getColumnIndex(NEWS_DESC)
        var linkIndex = results.getColumnIndex(NEWS_LINK)
        var imageIndex = results.getColumnIndex(NEWS_IMAGE)
//            title.text =

        while (!results.isAfterLast()) {
            var thisTitle = results.getString(titleIndex)
            var thisDesc = results.getString(descIndex)
            var thisLink = results.getString(linkIndex)
            var thisImage = results.getString(imageIndex)

            newsFavArray.add(NewsArticle(thisTitle, thisDesc, thisLink, thisImage))

            results.moveToNext()
        }

    }

    inner class NewsFavListAdapter(ctx : Context) : ArrayAdapter<NewsArticle>(ctx, 0) {
        /**
         *
         * Getting the item from the bus list array.
         *
         * @param  position the index of the position in the array
         * @return returns the item from the array
         */
        override fun getCount() : Int {
            return newsFavArray.size
        }


        /**
         *
         * Getting the item from the bus list array.
         *
         * @param  position the index of the position in the array
         * @return returns the item from the array
         */
        override fun getItem(position: Int) : NewsArticle {
            return newsFavArray.get(position)
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

            result = inflater.inflate(R.layout.news_fav_list_item, null)

            var title = result.findViewById<TextView>(R.id.newsFavTitle)

            var deleteButton = result.findViewById<TextView>(R.id.NewsFavDelete)

            deleteButton.setOnClickListener {
                deleteArticle(position)
            }

            title.text = getItem(position).newsTitle

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

    fun deleteArticle(position: Int){

        val idQuery = db.query(NEWS_TABLE, arrayOf("_id"), " $NEWS_TITLE = ? ", arrayOf(newsFavArray.get(position).newsTitle), null, null, null)
        idQuery.moveToFirst()
        var idIndex = idQuery.getColumnIndex("_id")
        var id = idQuery.getInt(idIndex)
        db.delete(NEWS_TABLE, "_id=$id", null)
        results = db.query( NEWS_TABLE, arrayOf("_id", NEWS_TITLE, NEWS_DESC, NEWS_LINK, NEWS_IMAGE ), null, null,null,null,null)

        //copy from above
        newsFavArray.removeAt(position)
        newsFavListAdapter.notifyDataSetChanged()
    }

}
