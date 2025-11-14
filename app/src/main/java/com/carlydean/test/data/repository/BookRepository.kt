package com.carlydean.test.data.repository

import android.content.Context
import com.carlydean.test.data.database.AppDatabase
import com.carlydean.test.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository para manejar libros, favoritos y progreso de lectura
 */
class BookRepository(context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    private val bookDao = database.bookDao()
    private val favoriteDao = database.favoriteDao()
    private val progressDao = database.readingProgressDao()

    // ========== BOOKS ==========
    
    suspend fun getAllBooks(): List<BookEntity> = withContext(Dispatchers.IO) {
        bookDao.getAllBooks()
    }

    suspend fun getBookById(bookId: String): BookEntity? = withContext(Dispatchers.IO) {
        bookDao.getBookById(bookId)
    }

    suspend fun saveBooks(books: List<Book>) = withContext(Dispatchers.IO) {
        val entities = books.map { it.toEntity() }
        bookDao.insertBooks(entities)
    }

    suspend fun updateBook(book: BookEntity) = withContext(Dispatchers.IO) {
        bookDao.updateBook(book)
    }

    suspend fun clearBooks() = withContext(Dispatchers.IO) {
        bookDao.deleteAllBooks()
    }

    // ========== FAVORITES ==========
    
    suspend fun getFavorites(): List<Favorite> = withContext(Dispatchers.IO) {
        favoriteDao.getAllFavorites()
    }

    suspend fun isFavorite(bookId: String): Boolean = withContext(Dispatchers.IO) {
        favoriteDao.getFavorite(bookId) != null
    }

    suspend fun addFavorite(bookId: String) = withContext(Dispatchers.IO) {
        favoriteDao.insertFavorite(Favorite(bookId))
    }

    suspend fun removeFavorite(bookId: String) = withContext(Dispatchers.IO) {
        favoriteDao.deleteFavoriteByBookId(bookId)
    }

    suspend fun toggleFavorite(bookId: String): Boolean = withContext(Dispatchers.IO) {
        val isFav = isFavorite(bookId)
        if (isFav) {
            removeFavorite(bookId)
        } else {
            addFavorite(bookId)
        }
        !isFav
    }

    // ========== READING PROGRESS ==========
    
    suspend fun getProgress(bookId: String): ReadingProgress? = withContext(Dispatchers.IO) {
        progressDao.getProgress(bookId)
    }

    suspend fun saveProgress(progress: ReadingProgress) = withContext(Dispatchers.IO) {
        progressDao.insertProgress(progress)
    }

    suspend fun updateProgress(
        bookId: String,
        currentPage: Int,
        totalPages: Int
    ) = withContext(Dispatchers.IO) {
        val existing = progressDao.getProgress(bookId)
        if (existing != null) {
            val updated = existing.copy(
                currentPage = currentPage,
                totalPages = totalPages,
                lastReadAt = System.currentTimeMillis()
            )
            progressDao.updateProgress(updated)
        } else {
            progressDao.insertProgress(
                ReadingProgress(
                    bookId = bookId,
                    currentPage = currentPage,
                    totalPages = totalPages
                )
            )
        }
    }
}
