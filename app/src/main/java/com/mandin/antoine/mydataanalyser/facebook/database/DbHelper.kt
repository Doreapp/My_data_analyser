package com.mandin.antoine.mydataanalyser.facebook.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.mandin.antoine.mydataanalyser.utils.database.Names

class DbHelper(context: Context?) :
    SQLiteOpenHelper(context, Names.FACEBOOK_DB, null, DATABASE_VERSION) {

    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(ConversationEntries.getSqlCreateEntries())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(ConversationEntries.getSqlDeleteEntries())
    }
}