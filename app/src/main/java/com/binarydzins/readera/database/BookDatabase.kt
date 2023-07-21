package com.binarydzins.readera.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.binarydzins.readera.mode.Book

@Database(entities = [BookEntity::class], version = 1)
abstract class Bookdatabase: RoomDatabase() {

    abstract fun BookDao() : BookDao

}