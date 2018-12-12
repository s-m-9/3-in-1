package bmm.com.threeinone

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

lateinit var db : SQLiteDatabase
lateinit var results : Cursor
lateinit var dbHelper : FavDatabaseHelper

var avgRunTime = 0
var minRunTime = 0
var maxRunTime = 0
var avgYear = 0


//The user can view statistics on the shortest, longest, and average movie run time and year of the movies.
class movieStatsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        var view = inflater.inflate(R.layout.fragment_movie_stats, container, false)

        dbHelper = FavDatabaseHelper(view!!.context) //get a helper object
        db = dbHelper.writableDatabase //open your database


        results = db.query( "FavoriteMovies", arrayOf("_id", "FavoritesTitle", "FavoritesDesc",  "FavoritesRuntime", "FavoritesYear"), null, null,null,null,null)
        results.moveToFirst()

        var idIndex = results.getColumnIndex("_id")
        var titleIndex = results.getColumnIndex("FavoritesTitle")
        var descIndex = results.getColumnIndex("FavoritesDesc")
        var runTimeIndex = results.getColumnIndex("FavoritesRuntime")
        var yearIndex = results.getColumnIndex("FavoritesYear")

var avgSum = 0
    var maxRuntime = 0
        var minRuntime = 0
            var yearSum = 0
        var count = 0

        while (!results.isAfterLast()){
            var thisID = results.getInt(idIndex)
            var runTime  = results.getString(runTimeIndex).split(" ")[0].toInt()

            var number = results.getString(yearIndex)
            val index = number.indexOf('-')
            var yearr = number.substring(0, 4)
            var year = yearr.toInt()

            avgSum = avgSum + runTime
            maxRuntime = maxOf(maxRuntime, runTime)
            minRuntime = minOf(minRuntime, runTime)
            yearSum = yearSum + year
            count++
//            Log.i("Something", runTime)
            //results.getString(runTime).split(" ")[0]
//            var thisRunTime = result.getInt()
//            favListArray.add(FavoriteMovies(results.getString(title), results.getString(desc)))
            results.moveToNext()
        }

        var avgRunTimeView = view.findViewById<TextView>(R.id.avgRunTime)
        var avgYearView = view.findViewById<TextView>(R.id.avgYear)
        var maxRunTimeView = view.findViewById<TextView>(R.id.maxRunTime)
        var minRunTimeView = view.findViewById<TextView>(R.id.minRunTime)
        avgSum = avgSum.div(count)
        yearSum = yearSum.div(count)

        avgRunTimeView.text = avgSum.toString()
        avgYearView.text = yearSum.toString()
        maxRunTimeView.text = maxRunTime.toString()
        minRunTimeView.text = minRunTime.toString()





        return view
    }

}
