package bmm.com.threeinone

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class MovieStatsActivity : AppCompatActivity() {


    /**
     * @param savedInstanceState
     * In the onCreate function, we are making the
     * fragmentTransaction
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_stats)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val fragment = movieStatsFragment()
        fragmentTransaction.add(R.id.movie_StatsFragment, fragment)
        fragmentTransaction.commit()
    }
}
