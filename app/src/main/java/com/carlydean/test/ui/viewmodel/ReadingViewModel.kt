package com.carlydean.test.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carlydean.test.data.database.AppDatabase
import com.carlydean.test.data.model.Book
import com.carlydean.test.data.model.ReadingProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel para manejar el progreso de lectura
 */
class ReadingViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val progressDao = database.readingProgressDao()
    private val bookDao = database.bookDao()

    private val _currentBook = MutableLiveData<Pair<Book, ReadingProgress>?>()
    val currentBook: LiveData<Pair<Book, ReadingProgress>?> = _currentBook

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Carga el libro que se está leyendo actualmente
     * (el último que se leyó)
     */
    suspend fun loadCurrentBook() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    // Obtener todos los progresos, ordenados por última lectura
                    val allProgress = progressDao.getProgress("") // Necesitamos modificar el DAO

                    // Por ahora, obtener el primero que encontremos
                    // TODO: Modificar DAO para obtener el más reciente
                    if (allProgress != null) {
                        val bookEntity = bookDao.getBookById(allProgress.bookId)

                        if (bookEntity != null) {
                            val book = Book(
                                id = bookEntity.id,
                                title = bookEntity.title,
                                filename = bookEntity.filename,
                                author = bookEntity.author,
                                category = bookEntity.category,
                                subcategory = bookEntity.subcategory,
                                tags = bookEntity.tags,
                                fileType = bookEntity.fileType,
                                fileExtension = bookEntity.fileType,
                                size = bookEntity.size,
                                sizeBytes = bookEntity.sizeBytes,
                                downloadUrl = bookEntity.downloadUrl,
                                thumbnailUrl = bookEntity.thumbnailUrl
                            )

                            _currentBook.postValue(Pair(book, allProgress))
                        } else {
                            _currentBook.postValue(null)
                        }
                    } else {
                        _currentBook.postValue(null)
                    }
                }
            } catch (e: Exception) {
                _error.postValue("Error al cargar libro actual: ${e.message}")
                _currentBook.postValue(null)
            }
        }
    }

    /**
     * Actualiza el progreso de lectura de un libro
     */
    suspend fun updateProgress(
        bookId: String,
        currentPage: Int,
        totalPages: Int,
        completed: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val progress = ReadingProgress(
                        bookId = bookId,
                        currentPage = currentPage,
                        totalPages = totalPages,
                        lastReadAt = System.currentTimeMillis(),
                        completed = completed
                    )
                    progressDao.insertProgress(progress)
                }
                loadCurrentBook() // Recargar
            } catch (e: Exception) {
                _error.postValue("Error al actualizar progreso: ${e.message}")
            }
        }
    }

    /**
     * Obtiene el progreso de un libro específico
     */
    suspend fun getProgress(bookId: String): ReadingProgress? {
        return withContext(Dispatchers.IO) {
            progressDao.getProgress(bookId)
        }
    }
}
