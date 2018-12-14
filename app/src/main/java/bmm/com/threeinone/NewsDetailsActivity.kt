package bmm.com.threeinone

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.ImageButton
import android.Manifest.permission.INTERNET
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.net.Uri
import android.webkit.WebView
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_choose.view.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import android.os.AsyncTask
import android.util.Log


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
    lateinit var bitmp: Bitmap
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
        descriptionView.text = desc?.trim()


        link = data?.getStringExtra("link")
        linkView.text = link

        image = data?.getStringExtra("image")

linkView.setOnClickListener {
    openWebPage(linkView.text.toString())
}

//        bitmp = getImage(image!!)!!
//        val outputStream = openFileOutput("$iconName.png", Context.MODE_PRIVATE)
//        bitmp.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
//        outputStream.flush()
//        outputStream.close()


     DownloadImageTask().execute(image)








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
    fun openWebPage(url: String) {
        val webpage: Uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    fun getImage(url: URL): Bitmap? {
        var connection: HttpURLConnection? = null
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val responseCode = connection.responseCode
            return if (responseCode == 200) {
                BitmapFactory.decodeStream(connection.inputStream)
            } else
                null
        } catch (e: Exception) {
            return null
        } finally {
            connection?.disconnect()
        }
    }

    fun getImage(urlString: String): Bitmap? {
        try {
            val url = URL(urlString)
            return getImage(url)
        } catch (e: MalformedURLException) {
            return null
        }
    }


    private inner class DownloadImageTask() : AsyncTask<String, Void, Bitmap>() {

        override fun doInBackground(vararg urls: String): Bitmap? {
            val urldisplay = urls[0]
            var mIcon11: Bitmap? = null
            try {
                val `in` = java.net.URL(urldisplay).openStream()
                mIcon11 = BitmapFactory.decodeStream(`in`)
            } catch (e: Exception) {
                Log.e("Error", e.message)
                e.printStackTrace()
            }

            return mIcon11
        }

        override fun onPostExecute(result: Bitmap) {
            imageView = findViewById<ImageView>(R.id.news_image)
            imageView.setImageBitmap(result)
        }
    }
}
