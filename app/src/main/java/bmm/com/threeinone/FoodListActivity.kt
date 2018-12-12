package bmm.com.threeinone

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import android.widget.Toolbar
import java.io.Reader
import java.lang.ArithmeticException
import java.util.logging.Level
import kotlin.concurrent.fixedRateTimer
import android.support.design.widget.Snackbar
import android.view.View


class FoodListActivity() : AppCompatActivity(), SearchFragment.OnFragmentInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    var foodNameArray : ArrayList<String> = ArrayList<String>()
    var foodFatArray: ArrayList<String> = ArrayList<String>()
    var foodCalorieArray: ArrayList<String> = ArrayList<String>()
    lateinit var FoodView: ListView
    lateinit var addFavs: Button
    lateinit var pressSearch : Button
    lateinit var inputText : EditText
    lateinit var name : String
    lateinit var fat : String
    lateinit var calories: String
    lateinit var dbHelper: FoodDatabaseHelper
    lateinit var results: Cursor
    lateinit var db: SQLiteDatabase
    lateinit var newFragment:SearchFragment
    lateinit var query: String
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
        var progressBar : ProgressBar = findViewById(R.id.progressBar)
        pressSearch = findViewById<Button>(R.id.pressSearch)
        inputText = findViewById(R.id.typeSearch)
        pressSearch.setOnClickListener{

            if (inputText.text.isEmpty()){
                Toast.makeText(this, "Search field must not be empty", Toast.LENGTH_LONG).show()
            }else{
                hideKeyboard()
                FoodQuery().execute(inputText.text.toString())
                inputText.setText("")
            }
        }


        var goToFavs = findViewById<Button>(R.id.goToFavs)

        addFavs = findViewById<Button>(R.id.favourites)
        addFavs?.setOnClickListener {
            dbHelper = FoodDatabaseHelper() //get a helper object
            db = dbHelper.writableDatabase//open your database
            results = db.query(TABLE_NAME, arrayOf("_id", dbHelper.KEY_FOOD), null, null, null, null, null, null )
            val newRow = ContentValues()
            newRow.put(dbHelper.KEY_FOOD, name)
            newRow.put(dbHelper.KEY_CALORIES, calories)
            newRow.put(dbHelper.KEY_FAT, fat)

            db.insert(TABLE_NAME, "", newRow)
            results = db.query(TABLE_NAME, arrayOf( dbHelper.KEY_FOOD, dbHelper.KEY_ID, dbHelper.KEY_FAT, dbHelper.KEY_CALORIES),
                null, null, null, null,null,null
            )
            Toast.makeText(this, name + " added to favourites", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                supportFragmentManager.beginTransaction().remove(newFragment).commit();
            }, 500)
            val intent = Intent(this, FoodDetailsActivity::class.java)
            startActivityForResult(intent, 50)
        }

        goToFavs.setOnClickListener {
            val intent = Intent(this, FoodDetailsActivity::class.java)
            startActivityForResult(intent, 50)
        }
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




    inner class FoodQuery : AsyncTask<String, Integer, String>(){

        var progressBar : ProgressBar = findViewById(R.id.progressBar)
        var progressStarted = 10
        var progressFinished = 100

        override fun onPreExecute() {
            super.onPreExecute()
            progressBar.visibility = View.VISIBLE
            progressBar.progress = progressStarted

        }

        override fun doInBackground(vararg params: String?): String {
            progressBar.progress = 60
            query = URLEncoder.encode(params[0], "UTF-8")
            var url = URL("https://api.edamam.com/api/food-database/parser?app_id=a79e98d4&app_key=1ae7a8747a7c27ea98a0124a85d9cc91&ingr=$query")
            val Connection = url.openConnection() as HttpURLConnection
            val stream = Connection.getInputStream()
            var reader = BufferedReader(InputStreamReader(stream) as Reader?, 8)
            var sb = StringBuilder()
            var line = reader.readLine()
            while(line != null){
                sb.append(line!! + "\n")
                line = reader.readLine()
            }
            var result = sb.toString()
            var root = JSONObject(result)
            var array = root.getJSONArray("parsed")
            var foodObject = array.getJSONObject(0)
            var food = foodObject.getJSONObject("food")
            var nutrients = food.getJSONObject("nutrients")
            fat= nutrients.getDouble("FAT").toString()
            calories = nutrients.getDouble("ENERC_KCAL").toString()
            name = root.getString("text")
            publishProgress()
                return result
        }


        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressBar.progress = progressFinished
            Handler().postDelayed({
                progressBar.visibility = View.INVISIBLE
            }, 300)
            foodNameArray.add(name)
            foodFatArray.add(fat)
            foodCalorieArray.add(calories)
            var infoToPass = Bundle()
            infoToPass.putString("Name",name)
            infoToPass.putString("Fat",fat)
            infoToPass.putString("Calories", calories)
            newFragment = SearchFragment()
            newFragment.arguments = infoToPass
            supportFragmentManager.beginTransaction().replace(R.id.foodListContainer,newFragment ).commit();
        }
    }

    val DATABASE_NAME = "Food.db"
    val VERSION_NUM = 1
    val TABLE_NAME = "Food"

    inner class FoodDatabaseHelper : SQLiteOpenHelper(this@FoodListActivity, DATABASE_NAME, null, VERSION_NUM){
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 50) {
            if (resultCode == RESULT_OK) {
               val returnedName = data!!.getStringExtra("Name")
                val returnedCalories =  data!!.getStringExtra("Calories")
                val returnedFat =  data!!.getStringExtra("Fat")
                var infoToPass = Bundle()
                infoToPass.putString("Name", returnedName)
                infoToPass.putString("Calories", returnedCalories)
                infoToPass.putString("Fat", returnedFat)
                var newFragment = SearchFragment()
                newFragment.arguments = infoToPass
                supportFragmentManager.beginTransaction().replace(R.id.foodListContainer, newFragment).commit();
            }
        }
    }

//    override fun onNavigationItemSelected(item: MenuItem): Boolean {
//        when(item.itemId) {
//            R.id.movie_item -> {
//                var resultIntent = Intent(this, MovieListActivity::class.java) // <-- change
//                startActivity(resultIntent)
//                finish()
//            }
//            R.id.food_item -> {
//                var resultIntent = Intent(this, FoodListActivity::class.java) // <-- change
//                startActivity(resultIntent)
//                finish()
//            }
//            R.id.news_item -> {
//                var resultIntent = Intent(this, NewsListActivity::class.java) // <-- change
//                startActivity(resultIntent)
//                finish()
//            }
//            R.id.bus_item -> {
//                var resultIntent = Intent(this, BusSearchActivity::class.java) // <-- change
//                startActivity(resultIntent)
//                finish()
//            }
//        }
//
//        var drawer = findViewById<DrawerLayout>(R.id.bus_drawer_layout)
//        drawer.closeDrawer(GravityCompat.START)
//        return true
//    }

    fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus.windowToken, InputMethodManager.SHOW_FORCED)
    }

}




