package com.carlydean.test.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.carlydean.test.R
import com.carlydean.test.ui.adapter.BooksAdapter
import com.carlydean.test.ui.viewmodel.FavoritesViewModel
import kotlinx.coroutines.launch

/**
 * Fragmento que muestra los libros favoritos del usuario
 */
class FavoritesFragment : Fragment() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var emptyView: LinearLayout
    private lateinit var booksAdapter: BooksAdapter
    private lateinit var viewModel: FavoritesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        rvFavorites = view.findViewById(R.id.rvFavorites)
        emptyView = view.findViewById(R.id.emptyView)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]

        // Configurar RecyclerView
        setupRecyclerView()

        // Observar favoritos
        observeFavorites()

        // Cargar favoritos
        loadFavorites()
    }

    private fun setupRecyclerView() {
        booksAdapter = BooksAdapter(
            onBookClick = { book ->
                // TODO: Abrir lector
            },
            onDownloadClick = { book ->
                // TODO: Descargar libro
            },
            onFavoriteClick = { book ->
                // Remover de favoritos
                viewLifecycleOwner.lifecycleScope.launch {
                    viewModel.removeFavorite(book.id)
                    loadFavorites() // Recargar lista
                }
            },
            isFavorite = { bookId ->
                // Todos los libros aquí son favoritos
                true
            }
        )

        rvFavorites.apply {
            adapter = booksAdapter
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
        }
    }

    private fun observeFavorites() {
        viewModel.favorites.observe(viewLifecycleOwner) { books ->
            if (books.isEmpty()) {
                showEmpty()
            } else {
                showContent()
                booksAdapter.submitList(books)
            }
        }
    }

    private fun loadFavorites() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadFavorites()
        }
    }

    private fun showContent() {
        rvFavorites.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    private fun showEmpty() {
        rvFavorites.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = FavoritesFragment()
    }
}
