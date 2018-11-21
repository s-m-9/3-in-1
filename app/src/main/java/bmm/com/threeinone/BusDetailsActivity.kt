package bmm.com.threeinone

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button

class BusDetailsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus_details)

        var favButton = findViewById(R.id.favouritesButton) as Button

        favButton.setOnClickListener {
            var builder = AlertDialog.Builder(this);
            builder.setTitle("Do you want to open the gates of Oblivion?")

            builder.setPositiveButton("OK", {dialog, id ->
                // User clicked OK button
                finish()
            })
            builder.setNegativeButton("Cancel",  { dialog, id ->
                // User cancelled the dialog

            })

            var dialog = builder.create()
            dialog.show();
        }
    }
}
