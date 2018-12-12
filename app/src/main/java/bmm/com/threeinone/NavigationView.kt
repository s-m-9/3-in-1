package bmm.com.threeinone

import android.content.Context
import android.content.Intent
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat.startActivity
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

class NavigationView(val activity : AppCompatActivity): NavigationView.OnNavigationItemSelectedListener {
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.movie_item -> {
                var resultIntent = Intent(activity, MovieListActivity::class.java) // <-- change
                activity.startActivity(resultIntent)

            }
            R.id.food_item -> {
                var resultIntent = Intent(activity, FoodListActivity::class.java) // <-- change
                activity.startActivity(resultIntent)

            }
            R.id.news_item -> {
                var resultIntent = Intent(activity, NewsListActivity::class.java) // <-- change
                activity.startActivity(resultIntent)

            }
            R.id.bus_item -> {
                var resultIntent = Intent(activity, BusSearchActivity::class.java) // <-- change
                activity.startActivity(resultIntent)

            }
        }

        var drawer = activity.findViewById<DrawerLayout>(R.id.bus_drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


}