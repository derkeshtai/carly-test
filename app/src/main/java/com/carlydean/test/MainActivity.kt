package com.carlydean.test

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.carlydean.test.ui.adapter.ViewPagerAdapter
import com.carlydean.test.utils.AuthHelper
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * MainActivity con sistema de tabs:
 * - Biblioteca (Google Drive)
 * - Favoritos
 * - Leyendo Ahora
 */
class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var searchView: SearchView

    private var isGuest: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener modo (guest o autenticado)
        isGuest = intent.getBooleanExtra("isGuest", false)

        // Inicializar vistas
        toolbar = findViewById(R.id.toolbar)
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        searchView = findViewById(R.id.searchView)

        // Configurar toolbar
        setSupportActionBar(toolbar)

        // Configurar ViewPager
        setupViewPager()

        // Configurar tabs
        setupTabs()
    }

    private fun setupViewPager() {
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        // Desactivar swipe si quieres (opcional)
        // viewPager.isUserInputEnabled = false
    }

    private fun setupTabs() {
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = getString(R.string.tab_library)
                    tab.setIcon(R.drawable.ic_book_placeholder)
                }
                1 -> {
                    tab.text = getString(R.string.tab_favorites)
                    tab.setIcon(R.drawable.ic_favorite)
                }
                2 -> {
                    tab.text = getString(R.string.tab_reading)
                    tab.setIcon(R.drawable.ic_reading)
                }
            }
        }.attach()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                toggleSearch()
                true
            }
            R.id.action_refresh -> {
                refreshCurrentTab()
                true
            }
            R.id.action_sign_out -> {
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun toggleSearch() {
        if (searchView.visibility == android.view.View.VISIBLE) {
            searchView.visibility = android.view.View.GONE
        } else {
            searchView.visibility = android.view.View.VISIBLE
            searchView.requestFocus()
        }
    }

    private fun refreshCurrentTab() {
        // Refrescar el tab actual
        val currentItem = viewPager.currentItem
        // TODO: Implementar refresh según el fragmento actual
    }

    private fun signOut() {
        AuthHelper.signOut(this)
        finish()
    }
}
