package bmm.com.threeinone

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.security.AccessControlContext

val DATABASE_NAME = "Project.db"
val VERSION_NUM = 2
val MOVIE_TABLE = "FavoriteMovies"
val KEY_TITLE = "FavoritesTitle"
val KEY_DESC = "FavoritesDesc"
val KEY_RUNTIME = "FavoritesRuntime"
val KEY_YEAR = "FavoritesYear"

 class FavDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION_NUM){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE " + MOVIE_TABLE +
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_TITLE + " TEXT, " + KEY_DESC + " TEXT, " + KEY_RUNTIME + " TEXT, " + KEY_YEAR + " TEXT) ") //create the table
        Log.i("FavDatabaseHelper", "Calling onCreate")
        // db.execSQL("CREATE TABLE $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_MESSAGES TEXT)")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE)//deletes your old data

        //create new table
        onCreate(db)
        Log.i("FavDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);
    }



}