package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class FoodListActivity : Activity() {

    var foodArray : ArrayList<String> = ArrayList<String>()
    lateinit var  foodlistAdapter : FoodListAdapter
    lateinit var FoodView: ListView
    lateinit var pressSearch : Button

    /**
        * adds items to the food array
        * finds the pressSearch button and adds a listener to it
        * When the pressSearch button is pressed a toast will appear
        * gets the view with the id FoodView and sets the food list as its adapter
     *
        * @param savedInstanceState
        *
        */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_list)



        foodArray.add("Apple")
        foodArray.add("Orange")
        foodArray.add("Banana")
        foodArray.add("Kiwi")
        foodArray.add("Grapes")

        pressSearch = findViewById<Button>(R.id.pressSearch)

        pressSearch.setOnClickListener{
            Toast.makeText(this, "Get Out Of Here", Toast.LENGTH_SHORT).show()
        }

        FoodView = findViewById<ListView>(R.id.FoodView)

        foodlistAdapter = FoodListAdapter(this)

        FoodView?.setAdapter(foodlistAdapter)


        FoodView?.setOnItemClickListener{_, _, position, _ ->
            val selectedFood = foodArray[position]
            val foodDetailIntent = Intent(this, FoodDetailsActivity::class.java)

            startActivity(foodDetailIntent)
        }

    }

    inner class FoodListAdapter(ctx : Context): ArrayAdapter<String>(ctx, 0){


        /**
         * Gets the size count of the array and returns it
         * @return
         */
        override fun getCount() : Int{
            return foodArray.size
        }

        /**
         * Gets the position of the item in the foodArray and returns it
         * @param position
         * @return
         */


        override fun getItem(position : Int) : String{
            return foodArray.get(position)
        }

        /**
         *
         * sets the title of the text view with the index of the position , inflates the view and returns it
         * @param position
         * @param convertView
         * @param parent
         * @return
         */
        override fun getView(position : Int, convertView: View?, parent : ViewGroup) : View? {
            var inflater = LayoutInflater.from(parent.getContext())

            var result: View
            result = inflater.inflate(R.layout.foodview_list_item, null)

            val food_title = result.findViewById<TextView>(R.id.foodTitle)
            val food_desc = result.findViewById<TextView>(R.id.foodDesc)

            food_title.setText(getItem(position))
          // food_desc.setText("Some details about the food")

            return result
        }

        /**
         *
         * Gets the item at the position index and returns it as a long
         * @param position
         * @retun
         */

        override fun getItemId(position : Int):Long{
            val something = 3
            return something.toLong()
        }
    }
}
