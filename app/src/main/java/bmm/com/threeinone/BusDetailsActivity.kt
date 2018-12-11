package bmm.com.threeinone

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.busview_trip_item.view.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.net.HttpURLConnection
import android.support.v7.app.AppCompatActivity
import java.net.URL

data class Trip(var destination: String?,
                var latitude: String?,
                var longitude: String?,
                var speed: String?,
                var startTime: String?,
                var adjTime: String?,
                var routeID: Int?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()) {
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun describeContents(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object CREATOR : Parcelable.Creator<Trip> {
        override fun createFromParcel(parcel: Parcel): Trip {
            return Trip(parcel)
        }

        override fun newArray(size: Int): Array<Trip?> {
            return arrayOfNulls(size)
        }
    }
}

class BusDetailsActivity : AppCompatActivity() {
    var tripArray : ArrayList<Trip?> = ArrayList<Trip?>()
    var routeDirection : Int = 0
    lateinit var tripAdapter : TripAdapter
    var routeNumber : String? = null
    var busStopNumber : String? = null
    lateinit var busViewPager : ViewPager
    lateinit var tabs : TabLayout
    lateinit var fragmentAdapter : PagerAdapter
    var busRoute1 : BusStop? = null
    var busRoute2 : BusStop? = null



    /**
     * It displays a details of the list item and shows a favorites button
     * @param savedInstanceState saves instance state
     */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_details)
        onActivityResult(50 ,2, intent)

        busViewPager = findViewById<android.support.v4.view.ViewPager>(R.id.busViewPager)
        tabs = findViewById<android.support.design.widget.TabLayout>(R.id.busTabs)
        fragmentAdapter = PagerAdapter(supportFragmentManager)


        var myQuery = TripQuery()
        myQuery.execute() // runs the thread

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        val firstName = findViewById<TextView>(R.id.FirstNameText)
        /*
        * destinationText
        * latText
        * longText
        * gpsText
        * startTimeText
        * scheduleText
        * */

        routeNumber = data?.getStringExtra("Bus Route Number")
        busStopNumber = data?.getStringExtra("Bus Stop Number")

        busRoute1 = data?.getSerializableExtra("Bus Route 1") as BusStop
        var route = data?.getSerializableExtra("Bus Route 2")
        if (route != null) busRoute2 = route as BusStop

//        val bus1 = data?.getSerializableExtra("Bus Route 1") as BusStop
//        val bus2 = data?.getSerializableExtra("Bus Route 2") as BusStop

//        val destination = findViewById<TextView>(R.id.destinationText)
//        val latText = findViewById<TextView>(R.id.latText)
//        val longText = findViewById<TextView>(R.id.longText)
//        val gpsText = findViewById<TextView>(R.id.gpsText)
//        val startTimeText = findViewById<TextView>(R.id.startTimeText)
//        val scheduleText = findViewById<TextView>(R.id.scheduleText)

//        destination.text = data?.getStringExtra("destination")
//        latText.text = data?.getStringExtra("latitude")
//        longText.text = data?.getStringExtra("longitude")
//        gpsText.text = data?.getStringExtra("gpsText")
//        startTimeText.text = data?.getStringExtra("startTime")
//        scheduleText.text = data?.getStringExtra("schedule")
    }

    inner class TripQuery : AsyncTask<String, Integer, String>() {
        var trip : Trip? = null

        override fun doInBackground(vararg params: String?): String {

            val busStopUrl = URL("https://api.octranspo1.com/v1.2/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo=$busStopNumber&routeNo=$routeNumber")

            Log.i("Something", URLUtil.isValidUrl(busStopUrl.toString()).toString())

            var connection = busStopUrl.openConnection() as HttpURLConnection // goes to the server
            var response = connection.getInputStream()

            getTrips(response)

            return "Done"
        }

        override fun onProgressUpdate(vararg values: Integer?) { // update your GUI
//            if (listArray.isEmpty()) {
//                notFound.visibility = View.VISIBLE
//            }
//            tripAdapter.notifyDataSetChanged()
            Log.i("Hey", tripArray.size.toString())
        }

        override fun onPostExecute(result: String?) { // run when thread is done and going away
            fragmentAdapter = PagerAdapter(supportFragmentManager)
            busViewPager.adapter = fragmentAdapter
            tabs.setupWithViewPager(busViewPager)
        }

        fun getTrips(response: InputStream) {
            //Initial list of bus stops

            val factory = XmlPullParserFactory.newInstance()
            factory.setNamespaceAware(false)
            val xpp = factory.newPullParser()
            xpp.setInput(response, "UTF-8") // read XML from Server



            while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
                var name = xpp.name

                when (xpp.eventType) {
                    XmlPullParser.START_TAG -> {
                        /*
                            <Trip>
                                <TripDestination>Trim</TripDestination>
                                <TripStartTime>11:55</TripStartTime>
                                <AdjustedScheduleTime>12</AdjustedScheduleTime>
                                <AdjustmentAge>0.28</AdjustmentAge>
                                <LastTripOfSchedule>false</LastTripOfSchedule>
                                <BusType>4EA - DD</BusType>
                                <Latitude>45.286345</Latitude>
                                <Longitude>-75.746111</Longitude>
                                <GPSSpeed>53.4</GPSSpeed>
                            </Trip>
                         */
                        /*
                        <RouteDirection>
                            <RouteNo>62</RouteNo>
                            <RouteLabel>St-Laurent</RouteLabel>
                            <Direction>Eastbound</Direction>
                            <Error />
                        */


                        if (name.equals("Trip")) {
                            trip = Trip(null, null, null, null, null, null, null)
                        }
                        if (xpp.name.equals("TripDestination")) {
                            trip?.destination = xpp.nextText()
                        }

                        if (xpp.name.equals("TripStartTime")) {
                            trip?.startTime = xpp.nextText()
                        }



                        if (xpp.name.equals("AdjustedScheduleTime")) {
                            trip?.adjTime = xpp.nextText()
                        }

                        if (xpp.name.equals("Latitude")) {
                            var text = xpp.nextText()
                            if (text.equals("")) {
                                trip?.latitude = "0.0"
                            } else {
                                trip?.latitude = text
                            }

                        }

                        if (xpp.name.equals("Longitude")) {
                            var text = xpp.nextText()
                            if (text.equals("")) {
                                trip?.longitude = "0.0"
                            } else {
                                trip?.longitude = text
                            }

                        }

                        if (xpp.name.equals("GPSSpeed")) {
                            var text = xpp.nextText()
                            if (text.equals("")) {
                                trip?.speed = "0.0"
                            } else {
                                trip?.speed = text
                            }

                        }

                        if (name.equals("Error")) {
                            var error = xpp.nextText()
                            if (!error.isBlank()) {
                                publishProgress()
                            }
                        }
                    }

                    XmlPullParser.END_TAG -> {

                        if (trip?.adjTime != null && trip?.destination != null && trip?.startTime != null &&
                            trip?.latitude != null && trip?.longitude != null && trip?.speed != null) {
                            trip?.routeID = routeDirection
                            tripArray.add(trip)
                            trip = null

                            publishProgress()
                        }

                        if (name.equals("RouteDirection")) {
                            routeDirection = 1
                        }
                    }
                }

                xpp.next()
            }
        }
    }

    inner class TripAdapter(val items: ArrayList<Trip?>, val ctx: Context) : RecyclerView.Adapter<ViewHolder>() {
        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View
            result = inflater.inflate(R.layout.busview_trip_item, null)

            return ViewHolder(result)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val latitude =  items.get(position)?.latitude
            var longitude = items.get(position)?.longitude

            holder?.destination?.text = items.get(position)?.destination
            holder?.location?.text = "($latitude, $longitude)"
            holder?.gpsText?.text = items.get(position)?.speed
            holder?.startTimeText?.text = items.get(position)?.startTime
            holder?.scheduleText?.text = items.get(position)?.adjTime

        }

    }

    inner class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        // Holds the TextView that will add each animal to

        val destination = view.destinationText
        val location = view.position
        val gpsText = view.gpsText
        val startTimeText = view.startTimeText
        val scheduleText = view.scheduleText
    }

    inner class PagerAdapter(fm : FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            var oneArray : ArrayList<Trip?> = ArrayList<Trip?>()
            var twoArray : ArrayList<Trip?> = ArrayList<Trip?>()
            var avgAdjScheduleTime1 = 0
            var avgAdjScheduleTime2 = 0

            for (trip in tripArray) {

                if (trip?.routeID == 0) {
                    avgAdjScheduleTime1 = trip?.adjTime!!.toInt() + avgAdjScheduleTime1
                    oneArray.add(trip)
                } else if (trip?.routeID == 1) {
                    avgAdjScheduleTime2 = trip?.adjTime!!.toInt() + avgAdjScheduleTime2
                    twoArray.add(trip)
                }
            }

            return when (position) {
                0 -> {
                    var average = avgAdjScheduleTime1.toFloat().div(oneArray.size.toFloat())
                    var dataToPass = Bundle()
                    var busOne = BusRouteOneFragment()
                    dataToPass.putParcelableArrayList("tripList", oneArray)
                    dataToPass.putFloat("average", average)
                    busOne.arguments = dataToPass
                    busOne
                }
                else -> {
                    var average2 = avgAdjScheduleTime2.toFloat().div(twoArray.size.toFloat())
                    var dataToPass = Bundle()
                    var busOne = BusRouteTwoFragment()
                    dataToPass.putParcelableArrayList("tripList", twoArray)
                    dataToPass.putFloat("average", average2)
                    busOne.arguments = dataToPass
                    busOne
                }

            }
        }

        override fun getCount(): Int {
            return 2
        }


        override fun getPageTitle(position: Int): CharSequence? {
            var route1 = busRoute1?.routeName + " (" + busRoute1?.routeDirection + ")"
            var route2 = (busRoute2?.routeName  + " (" + busRoute2?.routeDirection + ")").takeIf { busRoute2 != null } ?: ""
            return when (position) {
                0 -> route1
                else -> route2
            }
        }
    }
}
