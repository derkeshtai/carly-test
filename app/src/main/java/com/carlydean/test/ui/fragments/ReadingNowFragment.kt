package com.carlydean.test.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.load
import com.carlydean.test.R
import com.carlydean.test.data.model.ReadingProgress
import com.carlydean.test.ui.viewmodel.ReadingViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragmento que muestra el libro que se está leyendo actualmente
 */
class ReadingNowFragment : Fragment() {

    private lateinit var contentView: ScrollView
    private lateinit var emptyView: LinearLayout
    private lateinit var ivThumbnail: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvAuthor: TextView
    private lateinit var tvProgress: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvLastRead: TextView
    private lateinit var btnContinue: Button

    private lateinit var viewModel: ReadingViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reading, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        contentView = view.findViewById(R.id.contentView)
        emptyView = view.findViewById(R.id.emptyView)
        ivThumbnail = view.findViewById(R.id.ivThumbnail)
        tvTitle = view.findViewById(R.id.tvTitle)
        tvAuthor = view.findViewById(R.id.tvAuthor)
        tvProgress = view.findViewById(R.id.tvProgress)
        progressBar = view.findViewById(R.id.progressBar)
        tvLastRead = view.findViewById(R.id.tvLastRead)
        btnContinue = view.findViewById(R.id.btnContinue)

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[ReadingViewModel::class.java]

        // Observar libro actual
        observeCurrentBook()

        // Cargar libro actual
        loadCurrentBook()

        // Botón continuar
        btnContinue.setOnClickListener {
            // TODO: Abrir lector en la página guardada
        }
    }

    private fun observeCurrentBook() {
        viewModel.currentBook.observe(viewLifecycleOwner) { bookWithProgress ->
            if (bookWithProgress == null) {
                showEmpty()
            } else {
                showContent()
                displayBook(bookWithProgress)
            }
        }
    }

    private fun loadCurrentBook() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadCurrentBook()
        }
    }

    private fun displayBook(bookWithProgress: Pair<com.carlydean.test.data.model.Book, ReadingProgress>) {
        val (book, progress) = bookWithProgress

        // Título y autor
        tvTitle.text = book.title
        tvAuthor.text = book.author ?: "Autor desconocido"

        // Thumbnail
        if (!book.thumbnailUrl.isNullOrEmpty()) {
            ivThumbnail.load(book.thumbnailUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_book_placeholder)
                error(R.drawable.ic_book_placeholder)
            }
        } else {
            ivThumbnail.setImageResource(R.drawable.ic_book_placeholder)
        }

        // Progreso
        val percentage = if (progress.totalPages > 0) {
            (progress.currentPage * 100) / progress.totalPages
        } else {
            0
        }

        tvProgress.text = getString(R.string.reading_progress, percentage)
        progressBar.progress = percentage
        progressBar.max = 100

        // Última lectura
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val lastReadDate = dateFormat.format(Date(progress.lastReadAt))
        tvLastRead.text = getString(R.string.reading_last_read, lastReadDate)
    }

    private fun showContent() {
        contentView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
    }

    private fun showEmpty() {
        contentView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
    }

    companion object {
        fun newInstance() = ReadingNowFragment()
    }
}
