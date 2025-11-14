package com.carlydean.test.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Entidad Room para Favoritos
 */
@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey
    val bookId: String,
    val addedAt: Long = System.currentTimeMillis()
)

/**
 * Entidad Room para Progreso de Lectura
 */
@Entity(tableName = "reading_progress")
data class ReadingProgress(
    @PrimaryKey
    val bookId: String,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val lastReadAt: Long = System.currentTimeMillis(),
    val completed: Boolean = false,
    val readingTimeMinutes: Int = 0
)

/**
 * Entidad Room para Books (con campos locales)
 */
@Entity(tableName = "books_cache")
@TypeConverters(Converters::class)
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val filename: String,
    val author: String? = null,
    val category: String,
    val subcategory: String? = null,
    val tags: List<String>? = null,
    val fileType: String,
    val size: String,
    val sizeBytes: Long = 0,
    val downloadUrl: String,
    val thumbnailUrl: String? = null,
    
    // Campos locales (no del JSON)
    var downloaded: Boolean = false,
    var localPath: String? = null,
    var downloadProgress: Int = 0
)

/**
 * Converters para Room - List<String>
 */
class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String>? {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}

/**
 * Función de extensión para convertir Book a BookEntity
 */
fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        filename = this.filename,
        author = this.author,
        category = this.category,
        subcategory = this.subcategory,
        tags = this.tags,
        fileType = this.fileType,
        size = this.size,
        sizeBytes = this.sizeBytes,
        downloadUrl = this.downloadUrl,
        thumbnailUrl = this.thumbnailUrl
    )
}
