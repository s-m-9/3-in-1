package bmm.com.threeinone

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MovieStatsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_stats)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = movieStatsFragment()
        fragmentTransaction.add(R.id.statsFragment, fragment)
        fragmentTransaction.commit()
    }
}
