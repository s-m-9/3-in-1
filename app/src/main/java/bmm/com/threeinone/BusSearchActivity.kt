package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil.isValidUrl
import android.widget.*
import org.w3c.dom.Text
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.net.CacheResponse
import java.net.HttpURLConnection
import java.net.URL
import android.R.attr.data
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.ActionBarDrawerToggle
import android.view.*


data class BusStop(var routeNumber: String?, var routeDirection: String?, var routeName: String?, var routeID: Int?) : Serializable
data class BusRoute(var busRoute1: BusStop?, var busRoute2: BusStop? = null) : Serializable

class BusSearchActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var listArray : ArrayList<BusStop?> = ArrayList<BusStop?>()
    var busStopArray : ArrayList<BusRoute> = ArrayList<BusRoute>()
    var savedArray : ArrayList<String> = ArrayList<String>()
    var savedIDArray : ArrayList<Int> = ArrayList<Int>()

    lateinit var db : SQLiteDatabase
    lateinit var results : Cursor
    lateinit var dbHelper : BusDatabaseHelper

    lateinit var savedListAdapter : SavedRouteAdapter
    lateinit var bus_view: ListView
    lateinit var progress_bar : ProgressBar
    lateinit var notFound : TextView
    lateinit var searchButton : Button
    lateinit var searchBar : EditText
    lateinit var searchResult : String

    /**
     *
     * adding adding searchButton and listarray and into code and connecting the bus list adapter
     *
     * @param savedInstanceState saving instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_search)

        var toolbar = findViewById<Toolbar>(R.id.BusToolbar) //import android.support.v7.widget.Toolbar
        toolbar.setTitle(R.string.bus_activity_name)
        setSupportActionBar(toolbar) // show toolbar

        //add navigation to toolbar
        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        var toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        var navView = findViewById<NavigationView>(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

        // Bus search
        searchBar = findViewById<EditText>(R.id.BusSearchBar)
        searchButton = findViewById<Button>(R.id.BusSearchButton)
        notFound = findViewById<TextView>(R.id.BusStopNotFound)

        dbHelper = BusDatabaseHelper()
        db = dbHelper.writableDatabase // now my database object

        results = db.query(TABLE_NAME, arrayOf("_id", BUS_ROUTES ),
            null, null, null, null, null)


        val numRows = results.getCount()

        results.moveToFirst()

        var idIndex = results.getColumnIndex("_id")
        var busIndex = results.getColumnIndex(BUS_ROUTES)


        while (!results.isAfterLast()) { // while not end
            var thisID = results.getInt(idIndex)
            var thisStop = results.getString(busIndex)
            savedIDArray.add(thisID)
            savedArray.add(thisStop);
            results.moveToNext() // moves to next entry
        }


        searchButton.setOnClickListener {
            Toast.makeText(this, "This is toast", Toast.LENGTH_SHORT).show()
            searchResult = searchBar.text.toString()

            hideKeyboard()

            if (listArray.isNotEmpty()) {
                listArray.clear()
                busStopArray.clear()

            }

            notFound.visibility = View.INVISIBLE

            savedArray.add(searchResult)
            savedListAdapter.notifyDataSetChanged()

            // Have a thing that checks to see if its all good?



            // write to database
            val newRow = ContentValues() // import new row into table
            newRow.put(BUS_ROUTES, searchResult);
            db.insert(TABLE_NAME, "", newRow) // insert

            val id = db.query(TABLE_NAME, arrayOf("_id"), "$BUS_ROUTES = ?", arrayOf(searchResult),
                null, null, null)

            id.moveToFirst()
            var idIndex = id.getColumnIndex("_id")
            var theId = id.getInt(idIndex)
            Log.i("This is the id", id.getInt(idIndex).toString())
            savedIDArray.add(theId)
            searchBar.setText("")
        }

        progress_bar = findViewById<ProgressBar>(R.id.progressBar)
        bus_view = findViewById<ListView>(R.id.BusView)

        savedListAdapter = SavedRouteAdapter(this)
        bus_view?.setAdapter(savedListAdapter)


        bus_view?.setOnItemClickListener { _, _, position, _ ->
            val selectedBusStop = savedArray[position]
            val detailIntent = Intent(this, BusListActivity::class.java)
            detailIntent.putExtra("Bus Stop", selectedBusStop)
            startActivity(detailIntent)
        }
    }

    inner class SavedRouteAdapter(ctx: Context) : ArrayAdapter<String>(ctx, 0) { // one i'm using
        override fun getCount() : Int {
            return savedArray.size
        }

        override fun getItem(position : Int) : String {
            return savedArray.get(position)
        }

        override fun getItemId(position: Int):Long {
            return position.toLong()
        }

        override fun getView(position : Int, convertView: View?, parent : ViewGroup) : View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View
            result = inflater.inflate(R.layout.busview_saved_list_item, null)

            val bus_stop_number = result.findViewById<TextView>(R.id.SavedBusItem)
            val bus_stop_delete = result.findViewById<TextView>(R.id.BusTrash)

            bus_stop_delete.setOnClickListener {
                val stop_id = savedIDArray.get(position)
                Log.i("Item", "Item Number $stop_id")
                deleteStop(stop_id.toLong(), position)
            }



            bus_stop_number.text = getItem(position)
            return result
        }
    }

    val DATABASE_NAME = "OCTranspo.db"
    val VERSION_NUM = 1
    val TABLE_NAME = "BusRoutes"

    val BUS_ROUTES = "Routes"


    inner class BusDatabaseHelper : SQLiteOpenHelper(this@BusSearchActivity, DATABASE_NAME, null, VERSION_NUM) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE $TABLE_NAME (_id INTEGER PRIMARY KEY AUTOINCREMENT, $BUS_ROUTES TEXT); ")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

            // create new table
            onCreate(db)
        }
    }

    fun deleteStop(id: Long, position: Int) {
        db.delete(TABLE_NAME, "_id=$id", null)
        results = db.query(TABLE_NAME, arrayOf("_id", BUS_ROUTES ),
            null, null, null, null, null)
        savedArray.removeAt(position)
        savedIDArray.removeAt(position)
        savedListAdapter.notifyDataSetChanged()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        return super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.chat_item -> {
                // System Notification
                var mBuilder = NotificationCompat.Builder(this, "Channel_Name")
                    .setSmallIcon(R.drawable.cow)
                    .setAutoCancel(true)
                    .setContentTitle("Let's chat")
                    .setContentText("Chat with me ;) Click here: http://jhakje.owwuqjab")

                var resultIntent = Intent(this, FoodListActivity::class.java)

                var resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                mBuilder.setContentIntent(resultPendingIntent)
                var mNotificationId = 1
                var mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                mNotifyMgr.notify(mNotificationId, mBuilder.build())
            }
            R.id.list_item -> {
                var mBuilder = NotificationCompat.Builder(this, "Channel_Name")
                    .setSmallIcon(R.drawable.meal)
                    .setAutoCancel(true) // <-- change
                    .setContentTitle("List Items") // <-- change
                    .setContentText("List") // <-- change

                var resultIntent = Intent(this, MovieListActivity::class.java) // <-- change

                var resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                mBuilder.setContentIntent(resultPendingIntent)
                var mNotificationId = 2
                var mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                mNotifyMgr.notify(mNotificationId, mBuilder.build())
            }
            R.id.contact_thing -> {
                var mBuilder = NotificationCompat.Builder(this, "Channel_Name")
                    .setSmallIcon(R.drawable.meal)
                    .setAutoCancel(true) // <-- change
                    .setContentTitle("List Items") // <-- change
                    .setContentText("List") // <-- change


                var emailIntent = Intent(Intent.ACTION_SENDTO)
                var resultPendingIntent = PendingIntent.getActivity(this, 0, emailIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                mBuilder.addAction(R.drawable.pokedex, "Send email",  resultPendingIntent)

                var smsIntent = Intent(Intent.ACTION_SEND)
                var resultSMSIntent = PendingIntent.getActivity(this, 0, smsIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                mBuilder.addAction(R.drawable.barchart, "Send SMS",  resultSMSIntent)


                var mNotificationId = 3 // <-- change
                var mNotifyMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                mNotifyMgr.notify(mNotificationId, mBuilder.build())
            }
        }

        var drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_one -> {
//                Snackbar.make( snackbarButton, currentMessage, Snackbar.LENGTH_LONG).show()
            }
            R.id.action_two -> {
                var builder = AlertDialog.Builder(this);
                builder.setTitle("Do you want to go back")

                builder.setPositiveButton("OK", {dialog, id ->
                    // User clicked OK button
                    finish()
                })
                builder.setNegativeButton("Cancel",  { dialog, id ->
                    // User cancelled the dialog

                })

                var dialog = builder.create()
                dialog.show();


            }
            R.id.action_three -> {

            }
            R.id.action_four -> {
                Toast.makeText(this, "Version 1.0 by Suuba", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }
}
