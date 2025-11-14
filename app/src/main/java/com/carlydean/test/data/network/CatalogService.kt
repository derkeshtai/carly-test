package com.carlydean.test.data.network

import com.carlydean.test.data.model.Catalog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * Cliente para descargar el catálogo desde una URL
 */
class CatalogService {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    /**
     * Descarga el catálogo desde una URL
     * @param url URL del archivo JSON del catálogo
     * @return Catálogo parseado o null si hay error
     */
    suspend fun downloadCatalog(url: String): Result<Catalog> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                return@withContext Result.failure(
                    Exception("Error al descargar: HTTP ${response.code}")
                )
            }

            // --- INICIO DEL CAMBIO ---

            // 1. Obtenemos el cuerpo de la respuesta
            val responseBody = response.body
                ?: return@withContext Result.failure(Exception("Respuesta vacía"))

            // 2. Obtenemos el FLUJO de caracteres (Character Stream)
            //    En lugar de .string()
            val charStream = responseBody.charStream()

            // 3. Usamos '.use' para que el stream se cierre solo
            //    y le pasamos el stream directamente a Gson.
            val catalog = charStream.use { streamReader ->
                gson.fromJson(streamReader, Catalog::class.java)
            }

            // --- FIN DEL CAMBIO ---

            // Esto es lo que causaba el error de memoria:
            // val jsonString = response.body?.string()
            // val catalog = gson.fromJson(jsonString, Catalog::class.java)

            Result.success(catalog)

        } catch (e: Exception) {
            e.printStackTrace() // Ayuda a ver mejor el error en el Logcat
            Result.failure(e)
        }
    }
}
