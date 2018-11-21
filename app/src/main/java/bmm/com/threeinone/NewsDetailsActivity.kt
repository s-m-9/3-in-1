package bmm.com.threeinone

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button

class NewsDetailsActivity : Activity() {


    /**
     *
     * Shows the details of the news activity
     *
     * @param savedInstanceState
     *
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)

        var favButton = findViewById<Button>(R.id.news_btn_fav)

        favButton.setOnClickListener {
            var builder = AlertDialog.Builder(this);
            builder.setTitle("Do you like to save it into favourites")

            builder.setPositiveButton("Ok", {dialog, id -> finish()
            })
            builder.setNegativeButton("cancel", {dialog, id ->

            })
            var dialog = builder.create()
            dialog.show();
        }
    }
}
