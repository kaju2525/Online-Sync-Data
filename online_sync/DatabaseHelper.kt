package karun.com.online_sync

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        val DB_NAME = "NamesDB"
        val TABLE_NAME = "names"
        val COLUMN_ID = "id"
        val COLUMN_NAME = "name"
        val COLUMN_STATUS = "status"
        private val DB_VERSION = 1
    }


    override fun onCreate(db: SQLiteDatabase) {
        val sql = ("CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_NAME VARCHAR, $COLUMN_STATUS TINYINT);")
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Persons")
        onCreate(db)
    }

    val names: Cursor
        get() {
            val db = this.readableDatabase
            val sql = "SELECT * FROM $TABLE_NAME ORDER BY $COLUMN_ID ASC"
            return db.rawQuery(sql, null)
        }

    val unsyncedNames: Cursor
        get() {
            val db = this.readableDatabase
            val sql = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_STATUS = 0"
            return db.rawQuery(sql, null)
        }



    fun addName(name: String, status: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_NAME, name)
        contentValues.put(COLUMN_STATUS, status)

        db.insert(TABLE_NAME, null, contentValues)
        db.close()
        return true
    }

    fun updateNameStatus(id: Int, status: Int): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COLUMN_STATUS, status)
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null)
        db.close()
        return true
    }


}
