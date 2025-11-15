package com.carlydean.test

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.carlydean.test.ui.viewmodel.CatalogViewModel
import com.carlydean.test.ui.adapter.BooksAdapter

class MainActivity : AppCompatActivity() {
    
    private lateinit var tvStatus: TextView
    private lateinit var etCatalogUrl: EditText
    private lateinit var btnDownload: Button
    private lateinit var btnTest: Button
    
    private lateinit var viewModel: CatalogViewModel


    private lateinit var rvBooks: androidx.recyclerview.widget.RecyclerView
    private lateinit var booksAdapter: com.carlydean.test.ui.adapter.BooksAdapter



    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[CatalogViewModel::class.java]
        
        // Inicializar vistas
        tvStatus = findViewById(R.id.tv_status)
        etCatalogUrl = findViewById(R.id.et_catalog_url)
        btnDownload = findViewById(R.id.btn_download)
        btnTest = findViewById(R.id.btn_test)

        // --- AÑADIR ESTO ---
        rvBooks = findViewById(R.id.rvBooks) // El ID del XML
        setupRecyclerView() // Llamar a una nueva función de configuración
        
        // Cargar URL guardada si existe
        viewModel.getSavedUrl()?.let { url ->
            etCatalogUrl.setText(url)
        }
        
        // Mostrar info del sistema
        updateSystemInfo()
        
        // Observar estado del catálogo
        observeViewModel()
        
        // Botón de descarga
        btnDownload.setOnClickListener {
            val url = etCatalogUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                viewModel.downloadCatalog(url)
            } else {
                Toast.makeText(this, "Ingresa una URL", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Botón de prueba (mantiene funcionalidad anterior)
        btnTest.setOnClickListener {
            tvStatus.text = "✅ ¡Botón funcionando!\n\n${getSystemInfo()}"
        }
    }
    
    private fun observeViewModel() {
        // Observar estado de carga
        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                tvStatus.text = "⏳ Descargando catálogo..."
                btnDownload.isEnabled = false
            } else {
                btnDownload.isEnabled = true
            }
        }
        
        // Observar catálogo
        viewModel.catalog.observe(this) { catalog ->
            if (catalog != null) {

                // ----------------------------------------------------
                // ¡¡ESTA ES LA LÍNEA MÁGICA QUE FALTABA!!
                // Le das la lista de libros al adapter optimizado (ListAdapter)
                booksAdapter.submitList(catalog.books)
                // ----------------------------------------------------


                // Esto es lo que tenías antes.
                // Es mejor moverlo a un status más simple.
                tvStatus.text = "✅ Catálogo cargado con ${catalog.metadata.totalBooks} libros."

                // Opcional: Oculta el formulario de descarga y muestra la lista
                // etCatalogUrl.visibility = View.GONE
                // btnDownload.visibility = View.GONE
                // tvStatus.visibility = View.GONE
                // rvBooks.visibility = View.VISIBLE

            }
        }
        
        // Observar errores
        viewModel.error.observe(this) { error ->
            if (error != null) {
                tvStatus.text = "❌ $error\n\nVerifica:\n• La URL es correcta\n• Tienes conexión a internet\n• El archivo JSON es válido"
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun updateSystemInfo() {
        if (viewModel.hasCachedCatalog()) {
            viewModel.catalog.value?.let { catalog ->
                tvStatus.text = """
                    📦 Catálogo en caché
                    
                    • Libros: ${catalog.metadata.totalBooks}
                    • Categorías: ${catalog.metadata.totalCategories}
                    • Autores: ${catalog.metadata.totalAuthors}
                    
                    Ingresa URL y descarga para actualizar
                """.trimIndent()
                return
            }
        }
        
        tvStatus.text = """
            🚀 CarlyDean Library - Test
            
            Ingresa la URL de tu catálogo JSON
            y presiona "Descargar Catálogo"
            
            ${getSystemInfo()}
        """.trimIndent()
    }
    
    private fun getSystemInfo(): String {
        return """
            📱 Información del dispositivo:
            • Android ${android.os.Build.VERSION.RELEASE}
            • SDK ${android.os.Build.VERSION.SDK_INT}
            • Modelo: ${android.os.Build.MODEL}
            • Fabricante: ${android.os.Build.MANUFACTURER}
        """.trimIndent()
    }

    // --- AÑADIR ESTA NUEVA FUNCIÓN ---
    private fun setupRecyclerView() {
        booksAdapter = BooksAdapter(
            onBookClick = { book ->
                // ¡AQUÍ VA TU LÓGICA!
                // Llama a tu sistema para abrir el lector (PDF, EPUB, etc.)
                Toast.makeText(this, "Abrir lector para: ${book.title}", Toast.LENGTH_SHORT).show()
            },
            onDownloadClick = { book ->
                // ¡AQUÍ VA TU LÓGICA!
                // Llama a tu sistema de descargas
                Toast.makeText(this, "Descargar: ${book.title}", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = { book ->
                // ¡AQUÍ VA TU LÓGICA!
                // Llama a tu ViewModel para actualizar Room
                Toast.makeText(this, "Favorito: ${book.title}", Toast.LENGTH_SHORT).show()
                // viewModel.toggleFavorite(book)
            },
            isFavorite = { bookId ->
                // ¡AQUÍ VA TU LÓGICA!
                // Llama a tu ViewModel para preguntar a Room si es favorito
                // return viewModel.isBookFavorite(bookId)
                false // Placeholder
            }
        )

        rvBooks.adapter = booksAdapter
        // Opcional: Si quieres más rendimiento (¡para tus 40,000+ libros!)
        rvBooks.setHasFixedSize(true)
    }
}
