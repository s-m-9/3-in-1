package bmm.com.threeinone

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageButton
import android.Manifest.permission.INTERNET
import android.content.ContentValues
import android.content.Intent
import android.media.Image
import android.widget.ImageView
import android.widget.TextView

class NewsDetailsActivity : Activity() {


    /**
     *
     * Shows the details of the news activity
     *
     * @param savedInstanceState
     *
     */

    lateinit var titleView : TextView
    lateinit var imageView: ImageView
    lateinit var descriptionView : TextView
    lateinit var linkView : TextView
    var imageLink : String? = null

    val NEWS_TABLE = "FavouriteNews"
    val NEWS_IMAGE = "NewImage"
    val NEWS_TITLE = "NewsTitle"
    val NEWS_DESC = "NewDescription"
    val NEWS_LINK = "NewsLink"

    var title : String? = null
    var desc : String? = null
    var image : String? = null
    var link : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)
        onActivityResult(10, 3, intent)

        var bkmButton = findViewById<ImageButton>(R.id.news_btn_bookmark_off)



        bkmButton.setOnClickListener {
            var builder = AlertDialog.Builder(this);
            builder.setTitle("Do you like to save it into favourites")

            builder.setPositiveButton("Ok", {dialog, id ->
//                finish()
                var dbHelper = TheDatabaseHelper(this)
                var db = dbHelper.writableDatabase

                val newRow = ContentValues()
                newRow.put(NEWS_TITLE, title)
                newRow.put(NEWS_DESC, desc)
                newRow.put(NEWS_LINK, link)
                newRow.put(NEWS_IMAGE, image)

                db.insert(NEWS_TABLE, "", newRow)

//                db.execSQL("CREATE TABLE " + NEWS_TABLE +
//                        " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + NEWS_TITLE + " TEXT, " + NEWS_DESC + " TEXT, " + NEWS_LINK + " TEXT " + NEWS_IMAGE + " TEXT ) ") //create the fav table


            })
            builder.setNegativeButton("cancel", {dialog, id ->

            })
            var dialog = builder.create()
            dialog.show();
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        titleView = findViewById<TextView>(R.id.news_title)
        imageView = findViewById<ImageView>(R.id.news_image)
        descriptionView = findViewById<TextView>(R.id.news_description)
        linkView = findViewById<TextView>(R.id.news_link)


        title = data?.getStringExtra("title")
        titleView.text = title

        desc = data?.getStringExtra("description")
        descriptionView.text = desc


        link = data?.getStringExtra("link")
        linkView.text = link


        image = " no image "
//        image = data?.getStringExtra("image")
//
//        if (image == null) {
//            image = "no image"
//        }
//        imageLink.src = image


//        detailIntent.putExtra("title", listArray.get(position).newsTitle)
//        detailIntent.putExtra("image", listArray.get(position).newsImage)
//        detailIntent.putExtra("description", listArray.get(position).newsDescription)
//        detailIntent.putExtra("link", listArray.get(position).newsLink)



    }
}
