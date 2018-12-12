package bmm.com.threeinone


import android.content.Context
import android.os.Bundle
//import android.app.Fragment
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.busview_trip_item.view.*

//There is no point to this fragment's existence

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class BusRouteTwoFragment : Fragment() {
    lateinit var tripArray : ArrayList<Trip>
    lateinit var tripAdapter : TripAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var screen = inflater.inflate(R.layout.fragment_bus_route_two, container, false)


        val dataPassed = arguments
        tripArray = dataPassed!!.getParcelableArrayList<Trip>("tripList")
        var noBusses = screen.findViewById<TextView>(R.id.noBusses2)
        var averageText = screen.findViewById<TextView>(R.id.average2)
        if (tripArray.isEmpty()) {
            noBusses.visibility = View.VISIBLE
            averageText.visibility = View.INVISIBLE
        }

        var average = dataPassed!!.getFloat("average").toString()
        averageText.text = "Average Adjusted Schedule Time $average"

        var trip_view = screen.findViewById<RecyclerView>(R.id.BusRoute2CardListView)
        tripAdapter = TripAdapter(tripArray, activity!!.applicationContext)
        trip_view?.setAdapter(tripAdapter)
        trip_view.layoutManager = LinearLayoutManager(activity)

        return screen
    }

    inner class TripAdapter(val items: ArrayList<Trip>, val ctx: Context) : RecyclerView.Adapter<ViewHolder>() {
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

        val destination = view.busDestinationText
        val location = view.position
        val gpsText = view.gpsText
        val startTimeText = view.startTimeText
        val scheduleText = view.scheduleText
    }

}
