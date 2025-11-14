package com.carlydean.test.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.carlydean.test.data.model.Catalog
import com.carlydean.test.data.network.CatalogService
import com.google.gson.Gson
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Repositorio para manejar el catálogo
 * Descarga desde la red y guarda en caché local
 */
// 1. CAMBIO: Hacemos 'context' una propiedad de la clase
class CatalogRepository(private val context: Context) {

    private val service = CatalogService()
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "catalog_prefs",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()

    companion object {
        // 2. CAMBIO: Añadimos un nombre de archivo para el caché
        private const val CACHE_FILE_NAME = "catalog_cache.json"
        // private const val KEY_CATALOG = "cached_catalog" // (Ya no usamos esta llave)
        private const val KEY_CATALOG_URL = "catalog_url"
        private const val KEY_LAST_UPDATE = "last_update"
    }

    /**
     * Devuelve el objeto File que apunta a nuestro archivo de caché
     */
    private fun getCacheFile(): File {
        return File(context.cacheDir, CACHE_FILE_NAME)
    }

    /**
     * Descarga el catálogo desde una URL
     */
    suspend fun fetchCatalog(url: String): Result<Catalog> {
        val result = service.downloadCatalog(url)

        // Si se descargó correctamente, guardarlo en caché
        result.onSuccess { catalog ->
            saveCatalogToCache(catalog, url)
        }

        return result
    }

    /**
     * 3. CAMBIO GRANDE: Guarda el catálogo en un ARCHIVO DE CACHÉ
     */
    private fun saveCatalogToCache(catalog: Catalog, url: String) {

        // --- PARTE A: Guardar el JSON grande en un ARCHIVO (con stream) ---
        try {
            val cacheFile = getCacheFile()
            // Usamos un stream (FileWriter) para escribir directo al archivo
            val fileWriter = FileWriter(cacheFile)
            fileWriter.use { writer ->
                // Le decimos a Gson que escriba el objeto EN el stream
                gson.toJson(catalog, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Si falla el guardado del archivo, no actualizamos las prefs
            return
        }

        // --- PARTE B: Guardar los metadatos pequeños en SharedPreferences ---
        // (Ya NO guardamos el JSON string aquí)
        prefs.edit().apply {
            putString(KEY_CATALOG_URL, url)
            putLong(KEY_LAST_UPDATE, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * 4. CAMBIO GRANDE: Obtiene el catálogo desde el ARCHIVO DE CACHÉ
     */
    fun getCachedCatalog(): Catalog? {
        val cacheFile = getCacheFile()
        if (!cacheFile.exists()) {
            return null // No hay caché
        }

        return try {
            // Usamos un stream (FileReader) para leer el archivo
            val fileReader = FileReader(cacheFile)
            fileReader.use { reader ->
                gson.fromJson(reader, Catalog::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 5. CAMBIO: Verifica si el ARCHIVO de caché existe
     */
    fun hasCachedCatalog(): Boolean {
        return getCacheFile().exists()
    }

    /**
     * Obtiene la URL guardada del catálogo
     */
    fun getSavedCatalogUrl(): String? {
        return prefs.getString(KEY_CATALOG_URL, null)
    }

    /**
     * Guarda la URL del catálogo
     */
    fun saveCatalogUrl(url: String) {
        prefs.edit().putString(KEY_CATALOG_URL, url).apply()
    }

    /**
     * Obtiene la fecha de la última actualización
     */
    fun getLastUpdateTime(): Long {
        return prefs.getLong(KEY_LAST_UPDATE, 0)
    }

    /**
     * 6. CAMBIO: Limpia SharedPreferences Y borra el archivo de caché
     */
    fun clearCache() {
        // Limpiar SharedPreferences
        prefs.edit().clear().apply()

        // Borrar el archivo de caché
        try {
            getCacheFile().delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}