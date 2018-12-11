package bmm.com.threeinone

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

val DATABASE_NAME = "Project.db"
val VERSION_NUM = 2
val MOVIE_TABLE = "FavoriteMovies"
val FAV_TITLE = "FavoritesTitle"
val FAV_DESC = "FavoritesDesc"

val NEWS_TABLE = "FavouriteNews"
val NEWS_IMAGE = "NewImage"
val NEWS_TITLE = "NewsTitle"
val NEWS_DESC = "NewDescription"
val NEWS_LINK = "NewsLink"

class TheDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, VERSION_NUM){

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE " + MOVIE_TABLE +
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + FAV_TITLE + " TEXT, " + FAV_DESC + " TEXT) ") //create the fav table
//        Log.i("FavDatabaseHelper", "Calling onCreate")


        db.execSQL("CREATE TABLE " + NEWS_TABLE +
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " + NEWS_TITLE + " TEXT, " + NEWS_DESC + " TEXT, " + NEWS_LINK + " TEXT, " + NEWS_IMAGE + " TEXT ) ") //create the fav table


        // db.execSQL("CREATE TABLE $TABLE_NAME ( _id INTEGER PRIMARY KEY AUTOINCREMENT, $KEY_MESSAGES TEXT)")

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + MOVIE_TABLE)//deletes your old data

        //create new table
        onCreate(db)
        Log.i("FavDatabaseHelper", "Calling onUpgrade, oldVersion=" + oldVersion + " newVersion=" + newVersion);
    }



}