package com.binarydzins.readera.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao // Add this annotation to mark it as a Data Access Object (DAO)
interface BookDao {
    @Insert
    fun insertBook(bookEntity: BookEntity)

    @Delete
    fun deleteBook(bookEntity: BookEntity)

    @Query("SELECT * FROM books")
    fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM books WHERE book_id = :bookId")
    fun getBookById(bookId : String): BookEntity
}
