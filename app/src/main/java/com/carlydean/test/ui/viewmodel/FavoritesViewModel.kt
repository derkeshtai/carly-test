package com.carlydean.test.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carlydean.test.data.database.AppDatabase
import com.carlydean.test.data.model.Book
import com.carlydean.test.data.model.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para manejar favoritos
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val favoriteDao = database.favoriteDao()
    private val bookDao = database.bookDao()

    private val _favorites = MutableLiveData<List<Book>>()
    val favorites: LiveData<List<Book>> = _favorites

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Carga todos los libros favoritos
     */
    suspend fun loadFavorites() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Obtener IDs de favoritos
                    val favoritesList = favoriteDao.getAllFavorites()
                    val favoriteIds = favoritesList.map { it.bookId }

                    // Obtener los libros completos desde cache
                    val books = bookDao.getAllBooks()
                        .filter { it.id in favoriteIds }
                        .map { entity ->
                            // Convertir BookEntity a Book
                            Book(
                                id = entity.id,
                                title = entity.title,
                                filename = entity.filename,
                                author = entity.author,
                                category = entity.category,
                                subcategory = entity.subcategory,
                                tags = entity.tags,
                                fileType = entity.fileType,
                                fileExtension = entity.fileType,
                                size = entity.size,
                                sizeBytes = entity.sizeBytes,
                                downloadUrl = entity.downloadUrl,
                                thumbnailUrl = entity.thumbnailUrl
                            )
                        }

                    _favorites.postValue(books)
                }
            } catch (e: Exception) {
                _error.postValue("Error al cargar favoritos: ${e.message}")
            }
        }
    }

    /**
     * Agrega un libro a favoritos
     */
    suspend fun addFavorite(bookId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val favorite = Favorite(
                        bookId = bookId,
                        addedAt = System.currentTimeMillis()
                    )
                    favoriteDao.insertFavorite(favorite)
                }
                loadFavorites() // Recargar lista
            } catch (e: Exception) {
                _error.postValue("Error al agregar favorito: ${e.message}")
            }
        }
    }

    /**
     * Remueve un libro de favoritos
     */
    suspend fun removeFavorite(bookId: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    favoriteDao.deleteFavoriteByBookId(bookId)
                }
                loadFavorites() // Recargar lista
            } catch (e: Exception) {
                _error.postValue("Error al remover favorito: ${e.message}")
            }
        }
    }

    /**
     * Verifica si un libro es favorito
     */
    suspend fun isFavorite(bookId: String): Boolean {
        return withContext(Dispatchers.IO) {
            favoriteDao.getFavorite(bookId) != null
        }
    }
}
