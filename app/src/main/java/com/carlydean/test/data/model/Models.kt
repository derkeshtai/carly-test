package com.carlydean.test.data.model

import com.google.gson.annotations.SerializedName

/**
 * Catálogo completo de la biblioteca
 */
data class Catalog(
    val version: String,
    @SerializedName("generated_at")
    val generatedAt: String,
    @SerializedName("classification_method")
    val classificationMethod: String? = null,
    val metadata: CatalogMetadata,
    val statistics: CatalogStatistics? = null,
    val categories: List<Category>,
    val collections: List<Collection>? = null,
    val tags: List<Tag>? = null,
    val books: List<Book>
)

/**
 * Metadata del catálogo
 */
data class CatalogMetadata(
    @SerializedName("total_books")
    val totalBooks: Int,
    @SerializedName("total_categories")
    val totalCategories: Int,
    @SerializedName("total_authors")
    val totalAuthors: Int,
    @SerializedName("total_collections")
    val totalCollections: Int? = null,
    @SerializedName("total_tags")
    val totalTags: Int? = null,
    @SerializedName("source_files_scanned")
    val sourceFilesScanned: Int? = null,
    @SerializedName("files_excluded")
    val filesExcluded: Int? = null
)

/**
 * Estadísticas del catálogo
 */
data class CatalogStatistics(
    @SerializedName("books_with_author")
    val booksWithAuthor: Int? = null,
    @SerializedName("books_without_author")
    val booksWithoutAuthor: Int? = null,
    @SerializedName("books_with_subcategory")
    val booksWithSubcategory: Int? = null,
    @SerializedName("average_file_size")
    val averageFileSize: String? = null
)

/**
 * Categoría de libros
 */
data class Category(
    val name: String,
    val count: Int,
    val description: String? = null,
    @SerializedName("original_folder")
    val originalFolder: String? = null,
    val books: List<String>? = null
)

/**
 * Colección de libros
 */
data class Collection(
    val name: String,
    val category: String? = null,
    val description: String? = null,
    val count: Int,
    val books: List<String>? = null
)

/**
 * Tag/Etiqueta
 */
data class Tag(
    val name: String,
    val count: Int
)

/**
 * Libro - Modelo principal
 */
data class Book(
    val id: String,
    val title: String,
    val filename: String,
    val author: String? = null,
    val category: String,
    val subcategory: String? = null,
    val collection: String? = null,
    val tags: List<String>? = null,
    @SerializedName("file_type")
    val fileType: String,
    @SerializedName("file_extension")
    val fileExtension: String,
    val size: String,
    @SerializedName("size_bytes")
    val sizeBytes: Long = 0,
    @SerializedName("download_url")
    val downloadUrl: String,
    @SerializedName("view_url")
    val viewUrl: String? = null,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,
    @SerializedName("has_thumbnail")
    val hasThumbnail: Boolean = false,
    @SerializedName("original_path")
    val originalPath: String? = null,
    @SerializedName("created_time")
    val createdTime: String? = null,
    @SerializedName("modified_time")
    val modifiedTime: String? = null
)
