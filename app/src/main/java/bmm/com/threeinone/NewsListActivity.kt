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
        override fun getCount() : Int {
            return listArray.size
        }

        override fun getItem(position: Int) : String {
            return listArray.get(position)
        }

        override fun getView(position: Int, convertView: View?, parent : ViewGroup): View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View

            result = inflater.inflate(R.layout.news_list_item, null)

            val item_name = result.findViewById<TextView>(R.id.news_title)
            item_name.setText(getItem(position) )

            return result
        }

        override fun getItemId(position: Int):Long {
            return position.toLong()
        }
    }
}
