package bmm.com.threeinone

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button

class MoviesDetailsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies_details)

        var addBtn = findViewById<Button>(R.id.addBtn)

        addBtn.setOnClickListener{
            var builder = AlertDialog.Builder(this)
            builder.setTitle("Do you really want to enlist in the KGB?")

            builder.setPositiveButton("OK", {dialog, id ->
                finish()
            })
            builder.setNegativeButton("Cancel", {dialog, id ->

            })

            var dialog = builder.create()
            dialog.show()
        }
    }
}
