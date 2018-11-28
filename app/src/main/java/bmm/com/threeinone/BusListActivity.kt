package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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


data class BusStop(var routeNumber: String?, var routeDirection: String?, var routeName: String?)
data class BusRoute(var busRoute1: BusStop?, var busRoute2: BusStop? = null)

class BusListActivity : Activity() {
    var listArray : ArrayList<BusStop?> = ArrayList<BusStop?>()
    var busStopArray : ArrayList<BusRoute> = ArrayList<BusRoute>()
    lateinit var buslistAdapter : BusListAdapter
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
        setContentView(R.layout.activity_bus_list)

        searchBar = findViewById<EditText>(R.id.BusSearchBar)
        searchButton = findViewById<Button>(R.id.BusSearchButton)
        notFound = findViewById<TextView>(R.id.BusStopNotFound)

        searchButton.setOnClickListener {
            Toast.makeText(this, "This is toast", Toast.LENGTH_SHORT).show()
            searchResult = searchBar.text.toString()
            hideKeyboard()

            if (listArray.isNotEmpty()) {
                listArray.clear()
                busStopArray.clear()

            }

            notFound.visibility = View.INVISIBLE

            var myQuery = BusListQuery()
            myQuery.execute() // runs the thread

        }

        progress_bar = findViewById<ProgressBar>(R.id.progressBar)
        bus_view = findViewById<ListView>(R.id.BusView)
        buslistAdapter = BusListAdapter(this)

        bus_view?.setAdapter(buslistAdapter)
        bus_view?.setOnItemClickListener { _, _, position, _ ->
            val selected = listArray[position]
            val detailIntent = Intent(this, BusDetailsActivity::class.java)
            startActivity(detailIntent)
        }
    }

    inner class BusListQuery : AsyncTask<String, Integer, String>() {
        /*
            Getting the route summary for stop #3017 would be:
                https://api.octranspo1.com/v1.2/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=3050
            The next trips for the bus #95 from stop #3017 would be:
                https://api.octranspo1.com/v1.2/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=3017&routeNo=95

         */

        // bus list items

        var busStop : BusStop? = null

        override fun doInBackground(vararg params: String?): String {

            val busStopUrl = URL("https://api.octranspo1.com/v1.2/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=$searchResult")

//            val busStopUrl = URL("https://api.octranspo1.com/v1.2/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=3050")

            Log.i("Something", isValidUrl(busStopUrl.toString()).toString())

            var connection = busStopUrl.openConnection() as HttpURLConnection // goes to the server
            var response = connection.getInputStream()

            findBusses(response)

            return "Done"
        }

        override fun onProgressUpdate(vararg values: Integer?) { // update your GUI
            if (listArray.isEmpty()) {
                notFound.visibility = View.VISIBLE
            }
            buslistAdapter.notifyDataSetChanged()
        }

        override fun onPostExecute(result: String?) { // run when thead is done and going away
//            busStopArray
            if (listArray.isNotEmpty()) {
//                busStopArray
//                Log.i("List Array Size", "lost " + listArray.size);
                var i = 0
                while (i < listArray.size) {
                    if (i < listArray.size - 1) {
                        if (listArray[i]?.routeNumber == listArray[i + 1]?.routeNumber) {
                            busStopArray.add(BusRoute(listArray[i], listArray[i + 1]))

//                            Log.i("One", "In busStop " + busStopArray.get(i));
                            i++
                            i++
                        } else {
                            busStopArray.add(BusRoute(listArray[i]))
//                            Log.i("Two", "In busStop " + busStopArray.get(i));
                            i++
                        }
                    } else {
                        busStopArray.add(BusRoute(listArray[i]))
//                        Log.i("Three", "In busStop " + busStopArray.get(i));
                        i++

                    }
                }

                for  (stop in busStopArray) {
                    Log.i("One", "ok "  + stop);
                }
            }
        }

        fun findBusses(response: InputStream) {
            //Initial list of bus stops

            val factory = XmlPullParserFactory.newInstance()
            factory.setNamespaceAware(false)
            val xpp = factory.newPullParser()
            xpp.setInput(response, "UTF-8") // read XML from Server



            while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
                var name = xpp.name

                when (xpp.eventType) {
                    XmlPullParser.START_TAG -> {
                        if (name.equals("Route")) {
                            busStop = BusStop(null, null, null)
                        }
                        if (xpp.name.equals("RouteNo")) {
                            busStop?.routeNumber = xpp.nextText()
//                            routeNumber = xpp.nextText()

//                            Log.i("Strings", routeNumber + " - " + routeName)
//                            publishProgress() // causes android to call onProgressUpdate
                        }

                        if (xpp.name.equals("RouteHeading")) {
                            busStop?.routeName = xpp.nextText()
//                            routeName = xpp.nextText()
                        }

                        if (name.equals("Error")) {
                            var error = xpp.nextText()
                           if (!error.isBlank()) {
                               publishProgress()
                           }
                        }
                    }

                    XmlPullParser.END_TAG -> {
                        if (busStop?.routeName != null && busStop?.routeNumber != null && busStop?.routeName != "") {
                            listArray.add(busStop)
                            publishProgress()
                        }
                    }
                }

                xpp.next()
            }
        }
    }

    inner class BusListAdapter(ctx : Context): ArrayAdapter<BusRoute>(ctx, 0) {
        /**
         *
         * gets the size of the array
         *
         * @return returns the size of list array
         *
         */
        override fun getCount() : Int {
            return busStopArray.size
        }


        /**
         *
         * Getting the item from the bus list array.
         *
         * @param  position the index of the position in the array
         * @return returns the item from the array
         */
        override fun getItem(position : Int) : BusRoute? {
            return busStopArray.get(position)
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
        override fun getView(position : Int, convertView: View?, parent : ViewGroup) : View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View
            result = inflater.inflate(R.layout.busview_list_item, null)

            val bus_stop_number = result.findViewById<TextView>(R.id.busStopNum)
            val bus_stop_name1 = result.findViewById<TextView>(R.id.busStopName1)
            val bus_stop_name2 = result.findViewById<TextView>(R.id.busStopName2)
//            val message = result.findViewById(R.id.message_text) as TextView

//            val busStopItem = getItem(position)
//            var busStopItem2: BusStop? = null

//            if (position < getCount() - 1) {
//                if (busStopItem?.routeNumber == getItem(position + 1)?.routeNumber) {
//                    busStopItem2 = getItem(position + 1)
//                }
//            }

            val busRoute1 = getItem(position)?.busRoute1
            val busRoute2 = getItem(position)?.busRoute2

            bus_stop_number.text = busRoute1?.routeNumber
            bus_stop_name1.text = busRoute1?.routeName
            bus_stop_name2.text = busRoute2?.routeName


//            if (position < getCount() - 1) {
//                listArray.remove(getItem(position + 1))
//                notifyDataSetChanged()
//            }


//            if (busStopItem2 != null) {
//                if (getItem(position + 1)?.routeName != "What are you doing") {
//                    val clone = BusStop("nine", "two", getItem(position + 1)?.routeName)
//                    var route = deepClone(getItem(position + 1)?.routeName)
//                    var name = route
//                    bus_stop_name2.text = name
//                    getItem(position + 1)?.routeName = "What are you doing"
//
////                    listArray.remove(getItem(position + 1))
//                    Log.i("Do", bus_stop_name2.text.toString())
//                } else {
//                    bus_stop_name2.text = deepClone(getItem(position + 1)?.routeName)
//                    getItem(position + 1)?.routeName = "Why"
//                }
//
//            }

//            item_name.setText( getItem(position) ) // get the string at position
//            item_name.setText( getItem(position) )

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

    fun deepClone(obj: String?): String {
        try {

            var ori = obj?.toCharArray()
            var clone : CharArray = CharArray(ori!!.size)

            var i : Int = 0
            for (i in ori!!.indices) {
                clone[i] = ori[i]
            }

            Log.i("Here We Go", String(clone))
            if (String(clone).equals("What are you doing")) return "No"
            return String(clone)
        } catch (e: Exception) {
            Log.e("ERROR", "ERROR")
            return ""
        }

//        try {
//            val baos = ByteArrayOutputStream()
//
//            val oos = ObjectOutputStream(baos)
//            oos.writeObject(obj)
//            val ois = ObjectInputStream(ByteArrayInputStream(baos.toByteArray()))
//            return ois.readObject() as String
//        } catch (e: Exception) {
//            e.printStackTrace()
////            Log.e("Bad", "This isn't good")
//            return ""
//        }

    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }
}
