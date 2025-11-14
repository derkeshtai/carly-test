package com.carlydean.test.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.carlydean.test.MainActivity
import com.carlydean.test.R

/**
 * Pantalla de bienvenida/splash
 * Permite al usuario iniciar sesión o continuar como invitado
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var btnSignIn: Button
    private lateinit var btnContinueGuest: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Inicializar vistas
        btnSignIn = findViewById(R.id.btnSignIn)
        btnContinueGuest = findViewById(R.id.btnContinueGuest)
        progressBar = findViewById(R.id.progressBar)

        // Configurar listeners
        setupListeners()

        // Verificar si ya hay sesión activa
        checkExistingSession()
    }

    private fun setupListeners() {
        // Botón de inicio de sesión
        btnSignIn.setOnClickListener {
            openDriveAuth()
        }

        // Botón de continuar sin cuenta (solo libros públicos)
        btnContinueGuest.setOnClickListener {
            openMainActivity(isGuest = true)
        }
    }

    /**
     * Verifica si ya existe una sesión de Google activa
     * Si existe, va directo a MainActivity
     */
    private fun checkExistingSession() {
        // Verificar cookies de sesión de Google
        val cookieManager = android.webkit.CookieManager.getInstance()
        val cookies = cookieManager.getCookie("https://drive.google.com")

        if (!cookies.isNullOrEmpty() && cookies.contains("SID")) {
            // Ya hay sesión activa, ir a MainActivity
            openMainActivity(isGuest = false)
        }
    }

    /**
     * Abre DriveWebViewActivity para autenticación
     */
    private fun openDriveAuth() {
        showLoading(true)

        val intent = Intent(this, DriveWebViewActivity::class.java)
        intent.putExtra("mode", "auth")
        startActivityForResult(intent, REQUEST_CODE_AUTH)
    }

    /**
     * Abre MainActivity
     */
    private fun openMainActivity(isGuest: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("isGuest", isGuest)
        startActivity(intent)
        finish() // No permitir volver atrás
    }

    /**
     * Muestra/oculta loading
     */
    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnSignIn.isEnabled = !show
        btnContinueGuest.isEnabled = !show
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTH) {
            showLoading(false)

            if (resultCode == RESULT_OK) {
                // Autenticación exitosa
                openMainActivity(isGuest = false)
            } else {
                // Error o cancelado
                // No hacer nada, usuario puede reintentar
            }
        }
    }

    companion object {
        private const val REQUEST_CODE_AUTH = 100
    }
}
