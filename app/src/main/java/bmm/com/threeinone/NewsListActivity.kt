package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.FieldPosition

class NewsListActivity : Activity() {

    var listArray : ArrayList<String> = ArrayList<String>()
    lateinit var newsListAdapter : NewsListAdapter
    lateinit var news_view : ListView
    lateinit var progress_bar : ProgressBar
    lateinit var news_btn_search : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_list)

        listArray.add("A")
        listArray.add("B")
        listArray.add("C")
        listArray.add("D")
        listArray.add("E")
        listArray.add("F")
        listArray.add("G")

        news_btn_search = findViewById(R.id.news_btn_search)

        news_btn_search.setOnClickListener{
            Toast.makeText(this, "this is toast", Toast.LENGTH_SHORT).show()
        }

        progress_bar = findViewById<ProgressBar>(R.id.progress_bar)

        news_view = findViewById<ListView>(R.id.news_list)

        newsListAdapter = NewsListAdapter(this)

        news_view?.setAdapter(newsListAdapter)

        news_view?.setOnItemClickListener {_, _, position, _->
            val selected = listArray[position]
            val detailIntent = Intent(this, NewsDetailsActivity::class.java)

            startActivity(detailIntent)
        }


    }

    inner class NewsListAdapter(ctx : Context): ArrayAdapter<String>(ctx, 0) {
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
        override fun getItem(position: Int) : String {
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
            item_name.setText(getItem(position) )

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
