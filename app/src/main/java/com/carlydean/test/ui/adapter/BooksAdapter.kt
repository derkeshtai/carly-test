package com.carlydean.test.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.isVisible
import coil.load
import com.carlydean.test.R
import com.carlydean.test.data.model.Book
import com.carlydean.test.databinding.ItemBookBinding

class BooksAdapter(
    private val onBookClick: (Book) -> Unit,
    private val onDownloadClick: (Book) -> Unit,
    private val onFavoriteClick: (Book) -> Unit,
    private val isFavorite: (String) -> Boolean
) : ListAdapter<Book, BooksAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding, onBookClick, onDownloadClick, onFavoriteClick, isFavorite)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(
        private val binding: ItemBookBinding,
        private val onBookClick: (Book) -> Unit,
        private val onDownloadClick: (Book) -> Unit,
        private val onFavoriteClick: (Book) -> Unit,
        private val isFavorite: (String) -> Boolean
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(book: Book) {
            binding.apply {
                tvTitle.text = book.title
                tvAuthor.text = book.author ?: "Autor desconocido"
                tvCategory.text = book.category
                tvSize.text = book.size
                tvFileType.text = book.fileType.uppercase()

                // Cargar thumbnail
                ivThumbnail.load(book.thumbnailUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_book_placeholder)
                    error(R.drawable.ic_book_placeholder)
                }

                // Actualizar ícono de favorito
                val isFav = isFavorite(book.id)
                btnFavorite.setIconResource(
                    if (isFav) R.drawable.ic_favorite
                    else R.drawable.ic_favorite_border
                )

                // Clicks
                root.setOnClickListener { onBookClick(book) }
                btnDownload.setOnClickListener { onDownloadClick(book) }
                btnFavorite.setOnClickListener { onFavoriteClick(book) }
            }
        }
    }

    private class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }
}