package com.carlydean.test.data.database

import android.content.Context
import androidx.room.*
import com.carlydean.test.data.model.BookEntity
import com.carlydean.test.data.model.Converters
import com.carlydean.test.data.model.Favorite
import com.carlydean.test.data.model.ReadingProgress

@Database(
    entities = [BookEntity::class, Favorite::class, ReadingProgress::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun readingProgressDao(): ReadingProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "carlydean_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

@Dao
interface BookDao {
    @Query("SELECT * FROM books_cache")
    suspend fun getAllBooks(): List<BookEntity>

    @Query("SELECT * FROM books_cache WHERE id = :bookId")
    suspend fun getBookById(bookId: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("DELETE FROM books_cache")
    suspend fun deleteAllBooks()
}

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    suspend fun getAllFavorites(): List<Favorite>

    @Query("SELECT * FROM favorites WHERE bookId = :bookId")
    suspend fun getFavorite(bookId: String): Favorite?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: Favorite)

    @Delete
    suspend fun deleteFavorite(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE bookId = :bookId")
    suspend fun deleteFavoriteByBookId(bookId: String)
}

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress WHERE bookId = :bookId")
    suspend fun getProgress(bookId: String): ReadingProgress?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ReadingProgress)

    @Update
    suspend fun updateProgress(progress: ReadingProgress)

    @Query("DELETE FROM reading_progress WHERE bookId = :bookId")
    suspend fun deleteProgress(bookId: String)
}
