package bmm.com.threeinone

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class BusListActivity : Activity() {

    var listArray : ArrayList<BusStop?> = ArrayList<BusStop?>()
    var busStopArray : ArrayList<BusRoute> = ArrayList<BusRoute>()
    var savedArray : ArrayList<String> = ArrayList<String>()


    lateinit var buslistAdapter : BusListAdapter
    lateinit var bus_view: ListView
    lateinit var progress_bar : ProgressBar
    lateinit var notFound : TextView
    var favResult : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_list)
        onActivityResult(50 ,2, intent)

        notFound = findViewById<TextView>(R.id.BusStopNotFoundRoutes)

        progress_bar = findViewById<ProgressBar>(R.id.progressBarDetails)

        progress_bar.visibility = View.VISIBLE
        bus_view = findViewById<ListView>(R.id.BusRouteView)
        buslistAdapter = BusListAdapter(this)

        bus_view?.setAdapter(buslistAdapter)
        bus_view?.setOnItemClickListener { _, _, position, _ ->
            val busRoute = busStopArray[position]
            val busIntent = Intent(this, BusDetailsActivity::class.java)

            // send bus number
            busIntent.putExtra("Bus Route Number", busRoute.busRoute1?.routeNumber)
//            busIntent.putExtra("Bus 1 Direction", busRoute.busRoute1?.rou)
            busIntent.putExtra("Bus Stop Number", favResult)

            busIntent.putExtra("Bus Route 1", busRoute.busRoute1)
            busIntent.putExtra("Bus Route 2", busRoute.busRoute2)

            startActivity(busIntent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        if (requestCode == 50) {
//            Log.i("contactlist", "Returned to StartActivity.onActivityResult");
        }

        // Loads results as soon as activity starts using the bus stop number
        if (resultCode == 2) {
            favResult = data?.getStringExtra("Bus Stop")
            var myQuery = BusListQuery()
            myQuery.execute() // runs the thread
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

            val busStopUrl = URL("https://api.octranspo1.com/v1.2/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=$favResult")

//            val busStopUrl = URL("https://api.octranspo1.com/v1.2/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=3050")

            Log.i("Something", URLUtil.isValidUrl(busStopUrl.toString()).toString())

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

        override fun onPostExecute(result: String?) { // run when thread is done and going away
//            busStopArray
            progress_bar.visibility = View.INVISIBLE
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
                            busStop = BusStop(null, null, null, null)
                        }
                        if (xpp.name.equals("RouteNo")) {
                            busStop?.routeNumber = xpp.nextText()
//                            routeNumber = xpp.nextText()

//                            Log.i("Strings", routeNumber + " - " + routeName)
//                            publishProgress() // causes android to call onProgressUpdate
                        }

                        if (xpp.name.equals("DirectionID")) {
                            busStop?.routeID = xpp.nextText().toInt()
                        }

                        if (xpp.name.equals("Direction")) {
                            busStop?.routeDirection = xpp.nextText()
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
                            busStop = null
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

            val busRoute1 = getItem(position)?.busRoute1
            val busRoute2 = getItem(position)?.busRoute2

            bus_stop_number.text = busRoute1?.routeNumber
            bus_stop_name1.text = busRoute1?.routeName
            bus_stop_name2.text = busRoute2?.routeName


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
}
