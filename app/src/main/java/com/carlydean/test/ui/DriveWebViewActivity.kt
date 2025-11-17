package com.carlydean.test.ui

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.carlydean.test.R

/**
 * Activity que muestra Google Drive en un WebView
 * Permite autenticación y navegación por archivos
 */
class DriveWebViewActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorView: LinearLayout
    private lateinit var btnRetry: Button
    private lateinit var toolbar: Toolbar

    private var mode: String = "browse" // "auth" o "browse"
    private var driveFolderId: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_webview)

        // Obtener parámetros
        mode = intent.getStringExtra("mode") ?: "browse"
        driveFolderId = intent.getStringExtra("folderId")

        // Inicializar vistas
        webView = findViewById(R.id.webView)
        progressBar = findViewById(R.id.progressBar)
        errorView = findViewById(R.id.errorView)
        btnRetry = findViewById(R.id.btnRetry)
        toolbar = findViewById(R.id.toolbar)

        // Configurar toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Configurar WebView
        setupWebView()

        // Configurar retry
        btnRetry.setOnClickListener {
            loadDrive()
        }

        // Cargar Google Drive
        loadDrive()
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
            cacheMode = WebSettings.LOAD_DEFAULT

            // Permitir cookies
            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
            }

            // User agent (importante para que Google Drive funcione bien)
            userAgentString = "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
        }

        // WebViewClient para interceptar navegación
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                hideLoading()

                // Si estamos en modo autenticación, verificar si se completó
                if (mode == "auth") {
                    checkAuthStatus()
                }

                // Inyectar JavaScript para personalizar interfaz
                injectCustomJS()
            }

            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                showError()
            }
        }

        // WebChromeClient para progress bar
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    showLoading()
                    progressBar.progress = newProgress
                } else {
                    hideLoading()
                }
            }
        }

        // DownloadListener para interceptar descargas
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            handleDownload(url, contentDisposition, mimetype)
        }
    }

    /**
     * Carga Google Drive
     */
    private fun loadDrive() {
        showLoading()
        errorView.visibility = View.GONE
        webView.visibility = View.VISIBLE

        val url = if (driveFolderId != null) {
            // Carpeta específica
            "https://drive.google.com/drive/folders/$driveFolderId"
        } else {
            // Drive principal
            "https://drive.google.com/drive/my-drive"
        }

        webView.loadUrl(url)
    }

    /**
     * Inyecta JavaScript personalizado para mejorar la experiencia
     */
    private fun injectCustomJS() {
        val js = """
            javascript:(function() {
                // Ocultar elementos innecesarios de la UI de Drive
                // Esto es opcional, puedes comentarlo si quieres mostrar todo

                /*
                var style = document.createElement('style');
                style.innerHTML = `
                    /* Ocultar header de Drive (opcional) */
                    /* header { display: none !important; } */

                    /* Hacer más grande el área de contenido */
                    [role="main"] {
                        margin-top: 0 !important;
                    }
                `;
                document.head.appendChild(style);
                */

                console.log('CarlyDean Library - WebView initialized');
            })();
        """.trimIndent()

        webView.evaluateJavascript(js, null)
    }

    /**
     * Verifica si la autenticación se completó
     */
    private fun checkAuthStatus() {
        val cookieManager = CookieManager.getInstance()
        val cookies = cookieManager.getCookie("https://drive.google.com")

        if (!cookies.isNullOrEmpty() && cookies.contains("SID")) {
            // Autenticación exitosa
            setResult(RESULT_OK)

            Toast.makeText(this, getString(R.string.msg_sign_in_success), Toast.LENGTH_SHORT).show()

            // Esperar un poco antes de cerrar para que el usuario vea el mensaje
            webView.postDelayed({
                finish()
            }, 1500)
        }
    }

    /**
     * Maneja descargas de archivos
     */
    private fun handleDownload(url: String, contentDisposition: String?, mimeType: String?) {
        try {
            // Extraer nombre del archivo
            val filename = URLUtil.guessFileName(url, contentDisposition, mimeType)

            // Crear request de descarga
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)
                addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url))
                setDescription("Descargando desde CarlyDean Library")
                setTitle(filename)
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, filename)
            }

            // Iniciar descarga
            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)

            Toast.makeText(this, "Descargando: $filename", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al descargar: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showError() {
        webView.visibility = View.GONE
        errorView.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            // Si estamos en modo auth y el usuario cancela, enviar RESULT_CANCELED
            if (mode == "auth") {
                setResult(RESULT_CANCELED)
            }
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        // Limpiar WebView
        webView.clearCache(false)
    }
}
