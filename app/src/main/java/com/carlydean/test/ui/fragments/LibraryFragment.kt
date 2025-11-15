package com.carlydean.test.ui.fragments

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.carlydean.test.R
import com.carlydean.test.ui.DriveWebViewActivity
import com.carlydean.test.utils.AuthHelper

/**
 * Fragmento que muestra la biblioteca de Google Drive
 */
class LibraryFragment : Fragment() {

    private lateinit var webView: WebView
    private lateinit var loadingView: LinearLayout
    private lateinit var emptyView: LinearLayout
    private lateinit var btnSignIn: Button

    // URL de la carpeta de Drive (puedes cambiarla)
    private val driveFolderUrl = "https://drive.google.com/drive/folders/12XZ6Vu3ll0hE3Nz1rkXzXJwiz_oofXMA"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        webView = view.findViewById(R.id.webView)
        loadingView = view.findViewById(R.id.loadingView)
        emptyView = view.findViewById(R.id.emptyView)
        btnSignIn = view.findViewById(R.id.btnSignIn)

        // Configurar botón de sign in
        btnSignIn.setOnClickListener {
            openSignIn()
        }

        // Configurar WebView
        setupWebView()

        // Cargar contenido
        loadContent()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            loadWithOverviewMode = true
            useWideViewPort = true

            // Cookies
            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
            }

            // User agent
            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                showLoading(true)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                showLoading(false)
                injectCustomJS()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                // No mostrar error para recursos secundarios
                if (request?.isForMainFrame == true) {
                    showLoading(false)
                }
            }
        }

        // Download listener
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            handleDownload(url, contentDisposition, mimetype)
        }
    }

    /**
     * Carga el contenido según si está autenticado o no
     */
    private fun loadContent() {
        val isAuthenticated = context?.let { AuthHelper.isAuthenticated(it) } ?: false

        if (isAuthenticated) {
            // Mostrar Drive completo
            showWebView()
            webView.loadUrl(driveFolderUrl)
        } else {
            // Mostrar solo preview (modo público)
            // Google Drive permite ver archivos públicos sin autenticación
            showWebView()
            webView.loadUrl(driveFolderUrl)
        }
    }

    /**
     * Inyecta JavaScript para personalizar la interfaz de Drive
     */
    private fun injectCustomJS() {
        val js = """
            javascript:(function() {
                try {
                    // Personalización opcional de la UI de Drive
                    console.log('CarlyDean Library - Drive loaded');

                    // Puedes agregar más personalizaciones aquí
                    // Por ejemplo, ocultar ciertos elementos, cambiar estilos, etc.

                } catch (e) {
                    console.error('Error in custom JS:', e);
                }
            })();
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    /**
     * Maneja descargas de archivos
     */
    private fun handleDownload(url: String, contentDisposition: String?, mimeType: String?) {
        try {
            val filename = URLUtil.guessFileName(url, contentDisposition, mimeType)

            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)
                addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url))
                setDescription("Descargando desde CarlyDean Library")
                setTitle(filename)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            }

            val downloadManager = context?.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            downloadManager?.enqueue(request)

            Toast.makeText(context, "Descargando: $filename", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Abre pantalla de inicio de sesión
     */
    private fun openSignIn() {
        val intent = Intent(context, DriveWebViewActivity::class.java)
        intent.putExtra("mode", "auth")
        startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == android.app.Activity.RESULT_OK) {
            // Autenticación exitosa, recargar
            loadContent()
        }
    }

    private fun showLoading(show: Boolean) {
        loadingView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showWebView() {
        webView.visibility = View.VISIBLE
        emptyView.visibility = View.GONE
        loadingView.visibility = View.GONE
    }

    private fun showEmpty() {
        webView.visibility = View.GONE
        emptyView.visibility = View.VISIBLE
        loadingView.visibility = View.GONE
    }

    fun reload() {
        loadContent()
    }

    companion object {
        private const val REQUEST_CODE_SIGN_IN = 101

        fun newInstance() = LibraryFragment()
    }
}
