package com.carlydean.test

import com.carlydean.test.data.model.Catalog
import com.google.gson.Gson
import org.junit.Test
import org.junit.Assert.*

/**
 * Test para verificar que el parsing del JSON funciona correctamente
 */
class CatalogParsingTest {
    
    private val gson = Gson()
    
    @Test
    fun catalog_parsing_works() {
        val jsonSample = """
        {
            "version": "2.0.0",
            "generated_at": "2025-11-01T04:10:23.502Z",
            "metadata": {
                "total_books": 3,
                "total_categories": 2,
                "total_authors": 2
            },
            "categories": [
                {
                    "name": "Programación",
                    "count": 10
                }
            ],
            "books": [
                {
                    "id": "book1",
                    "title": "Test Book",
                    "filename": "test.pdf",
                    "category": "Programación",
                    "download_url": "https://example.com/book.pdf",
                    "file_type": "pdf",
                    "file_extension": "pdf",
                    "size": "1.5 MB",
                    "size_bytes": 1572864
                }
            ]
        }
        """.trimIndent()
        
        val catalog = gson.fromJson(jsonSample, Catalog::class.java)
        
        assertNotNull(catalog)
        assertEquals("2.0.0", catalog.version)
        assertEquals(3, catalog.metadata.totalBooks)
        assertEquals(1, catalog.books.size)
        assertEquals("Test Book", catalog.books[0].title)
        assertEquals("https://example.com/book.pdf", catalog.books[0].downloadUrl)
    }
}
