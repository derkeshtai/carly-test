package com.carlydean.test.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.carlydean.test.data.model.Catalog
import com.carlydean.test.data.repository.CatalogRepository
import kotlinx.coroutines.launch

/**
 * ViewModel para manejar el catálogo
 */
class CatalogViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository = CatalogRepository(application)
    
    // Estado del catálogo
    private val _catalog = MutableLiveData<Catalog?>()
    val catalog: LiveData<Catalog?> = _catalog
    
    // Estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    // Estado de error
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        // Intentar cargar el catálogo desde caché
        loadCachedCatalog()
    }
    
    /**
     * Carga el catálogo desde caché
     */
    private fun loadCachedCatalog() {
        val cached = repository.getCachedCatalog()
        if (cached != null) {
            _catalog.value = cached
        }
    }
    
    /**
     * Descarga el catálogo desde una URL
     */
    fun downloadCatalog(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.fetchCatalog(url)
            
            result.onSuccess { catalog ->
                _catalog.value = catalog
                _error.value = null
            }
            
            result.onFailure { exception ->
                _error.value = "Error: ${exception.message}"
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Obtiene la URL guardada del catálogo
     */
    fun getSavedUrl(): String? {
        return repository.getSavedCatalogUrl()
    }
    
    /**
     * Verifica si hay catálogo en caché
     */
    fun hasCachedCatalog(): Boolean {
        return repository.hasCachedCatalog()
    }
    
    /**
     * Limpia el error
     */
    fun clearError() {
        _error.value = null
    }
}
