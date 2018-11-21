package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class BusListActivity : Activity() {
    var listArray : ArrayList<String> = ArrayList<String>()
    lateinit var buslistAdapter : BusListAdapter
    lateinit var bus_view: ListView
    lateinit var progress_bar : ProgressBar
    lateinit var searchButton : Button

    /**
     *
     * adding adding searchButton and listarray and into code and connecting the bus list adapter
     *
     * @param savedInstanceState saving instance state
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_list)

        listArray.add("Frank")
        listArray.add("Dennis")
        listArray.add("Dee")
        listArray.add("Mac")
        listArray.add("Charlie")


        searchButton = findViewById<Button>(R.id.searchButton)

        searchButton.setOnClickListener {
            Toast.makeText(this, "This is toast", Toast.LENGTH_SHORT).show()

        }

        progress_bar = findViewById<ProgressBar>(R.id.progressBar)
//        progress_bar.visibility = View.VISIBLE

        bus_view = findViewById<ListView>(R.id.BusView)

        buslistAdapter = BusListAdapter(this)

        bus_view?.setAdapter(buslistAdapter)

        bus_view?.setOnItemClickListener { _, _, position, _ ->
            val selected = listArray[position]
            val detailIntent = Intent(this, BusDetailsActivity::class.java)

//            detailIntent.putExtra("First Name", selectedPerson.fname)
//            detailIntent.putExtra("Last Name", selectedPerson.lname)
//            detailIntent.putExtra("Email", selectedPerson.email)
//            detailIntent.putExtra("Phone", selectedPerson.phone)

            startActivity(detailIntent)
        }

    }




    inner class BusListAdapter(ctx : Context): ArrayAdapter<String>(ctx, 0) {
        /**
         *
         * gets the size of the array
         *
         * @return returns the size of list array
         *
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
        override fun getItem(position : Int) : String {
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
        override fun getView(position : Int, convertView: View?, parent : ViewGroup) : View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View
            result = inflater.inflate(R.layout.busview_list_item, null)

            val item_name = result.findViewById<TextView>(R.id.busStopNum)
//            val message = result.findViewById(R.id.message_text) as TextView
            item_name.setText( getItem(position) ) // get the string at position
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
}
