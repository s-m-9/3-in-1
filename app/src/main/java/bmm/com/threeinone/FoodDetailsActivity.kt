package bmm.com.threeinone

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.*
import android.view.Menu
import android.view.MenuItem
import bmm.com.threeinone.R.layout.food_item


class FoodDetailsActivity : AppCompatActivity(), SearchFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    private var foodList = null as ListView?
    lateinit var dbHelper: FoodDatabaseHelper
    lateinit var results: Cursor
    lateinit var db: SQLiteDatabase
    lateinit var myAdapter: MyAdapter
    lateinit var searchFragment: SearchFragment

    var foodPosition : Long = 0

    var food = ArrayList<String>()
    var calories = ArrayList<String>()
    var fat = ArrayList<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)
        dbHelper = FoodDatabaseHelper() //get a helper object
        db = dbHelper.writableDatabase//open your database
        results = db.query(TABLE_NAME, arrayOf("_id", dbHelper.KEY_FOOD, dbHelper.KEY_CALORIES, dbHelper.KEY_FAT), null, null, null, null, null, null )
        results.getCount()
        results.moveToFirst() //point to first row
        val foodIndex = results.getColumnIndex(dbHelper.KEY_FOOD) // find the index of Messages column
        val calorieIndex = results.getColumnIndex(dbHelper.KEY_CALORIES)
        val fatIndex = results.getColumnIndex(dbHelper.KEY_FAT)
        while(!results.isAfterLast()){
            var thisFood = results.getString(foodIndex)
            var thisCalories = results.getString(calorieIndex)
            var thisFat = results.getString(fatIndex)
            food.add(thisFood);
            calories.add(thisCalories)
            fat.add(thisFat)
            results.moveToNext() //look at next row in table
        }



        foodList = this.findViewById(R.id.foodList)
        myAdapter = MyAdapter(this)
        val deleteButton = findViewById<Button>(R.id.delete_button)


        foodList?.adapter = myAdapter

        foodList?.setOnItemClickListener { parent, view, position, id ->

            this.foodPosition = myAdapter.getItemId(position)

            val intent = Intent(this, FoodListActivity::class.java)
                intent.putExtra("Name", food[position])
                intent.putExtra("Fat", fat[position])
                intent.putExtra("Calories", calories[position])
                setResult(Activity.RESULT_OK, intent)
                finish()






            }

        myAdapter.notifyDataSetChanged()



    }

    fun deleteFood(id:Long)
    {
        db.delete(TABLE_NAME, "_id=$id", null)
        var i=0
        i++
        results = db.query(TABLE_NAME, arrayOf( dbHelper.KEY_FOOD, dbHelper.KEY_FAT, dbHelper.KEY_CALORIES, dbHelper.KEY_ID),
            null, null, null, null,null,null
        )
        food.removeAt(this.foodPosition.toInt())
        fat.removeAt(this.foodPosition.toInt())
        calories.removeAt(this.foodPosition.toInt())
        myAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_one ->{
                var intent = Intent(this, MovieListActivity::class.java)
                startActivity(intent)

            }
            R.id.action_two ->{
                var intent = Intent(this, BusListActivity::class.java)
                startActivity(intent)

            }
            R.id.action_three -> {
                var intent = Intent(this, NewsListActivity::class.java)
                startActivity(intent)

            }
            R.id.action_four ->{
                Toast.makeText(this, "Version 1.0 by Marcus Blais", Toast.LENGTH_LONG).show()
            }
        }
        return true
    }


    inner class MyAdapter(ctx : Context) : ArrayAdapter<String>(ctx, 0 ){

        override fun getCount(): Int {
            return fat.size
        }


        fun getName(position: Int) : String{

            return food.get(position)
        }

        fun getCalories(position: Int) : String{
            return calories.get(position)
        }


        fun getFat(position: Int) : String{
            return fat.get(position)
        }

        override fun getView(position : Int, convertView: View?, parent : ViewGroup): View {
              var inflater = LayoutInflater.from(parent.context)
              var result: View?

            result = inflater.inflate(R.layout.food_item, null)
            val deleteButton = result.findViewById<Button>(R.id.delete_button)

            deleteButton.setOnClickListener {
                results = db.query(TABLE_NAME, arrayOf("_id", dbHelper.KEY_FOOD, dbHelper.KEY_CALORIES, dbHelper.KEY_FAT), null, null, null, null, null, null )
                //val idIndex = results.getColumnIndex("_id") //find the index of _id column
                    deleteFood(getItemId(position))

                Snackbar.make(deleteButton,"Item successfully deleted!",  Snackbar.LENGTH_SHORT)
                    .show()

            }
            val foodName = result?.findViewById(R.id.food_item_name) as TextView
            foodName.text = "Name: " + getName(position) // get the string at position

            val foodCalories = result?.findViewById(R.id.food_item_calories) as TextView
            foodCalories.text ="Calories: " + getCalories(position) // get the string at position

            val foodFat = result?.findViewById(R.id.food_item_fat) as TextView
            foodFat.text = "Fat: " + getFat(position) // get the string at position

            return result
        }

        override fun getItemId(position: Int): Long {
            results.moveToPosition(position)
            return results.getInt(results.getColumnIndex("_id")).toLong()
        }
    }


    val DATABASE_NAME = "Food.db"
    val VERSION_NUM = 1
    val TABLE_NAME = "Food"


    inner class FoodDatabaseHelper : SQLiteOpenHelper(this@FoodDetailsActivity, DATABASE_NAME, null, VERSION_NUM){
        val KEY_FOOD = "Food"
        val KEY_ID = "_id"
        val KEY_CALORIES = "Calories"
        val KEY_FAT = "Fat"

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_FOOD TEXT, $KEY_CALORIES TEXT, $KEY_FAT TEXT ) ") //create the table
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") //deletes old data
            onCreate(db)
        }
    }
}
