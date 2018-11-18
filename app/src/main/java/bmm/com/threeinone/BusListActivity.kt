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
        override fun getCount() : Int {
            return listArray.size
        }

        override fun getItem(position : Int) : String {
            return listArray.get(position)
        }

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

        override fun getItemId(position: Int):Long {
            return position.toLong()
        }
    }
}
