package com.carlydean.test.utils

import android.content.Context
import android.content.SharedPreferences
import android.webkit.CookieManager

/**
 * Helper para manejar autenticación y cookies de Google Drive
 */
object AuthHelper {

    private const val PREFS_NAME = "carlydean_auth"
    private const val KEY_IS_AUTHENTICATED = "is_authenticated"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_LAST_SYNC = "last_sync"

    /**
     * Verifica si el usuario está autenticado
     */
    fun isAuthenticated(context: Context): Boolean {
        // Verificar SharedPreferences
        val prefs = getPrefs(context)
        val savedAuth = prefs.getBoolean(KEY_IS_AUTHENTICATED, false)

        // Verificar cookies de Google
        val cookieManager = CookieManager.getInstance()
        val cookies = cookieManager.getCookie("https://drive.google.com")
        val hasCookies = !cookies.isNullOrEmpty() && cookies.contains("SID")

        return savedAuth && hasCookies
    }

    /**
     * Marca al usuario como autenticado
     */
    fun setAuthenticated(context: Context, email: String? = null) {
        val prefs = getPrefs(context)
        prefs.edit().apply {
            putBoolean(KEY_IS_AUTHENTICATED, true)
            email?.let { putString(KEY_USER_EMAIL, it) }
            putLong(KEY_LAST_SYNC, System.currentTimeMillis())
            apply()
        }
    }

    /**
     * Cierra sesión
     */
    fun signOut(context: Context) {
        // Limpiar SharedPreferences
        val prefs = getPrefs(context)
        prefs.edit().clear().apply()

        // Limpiar cookies de Google
        val cookieManager = CookieManager.getInstance()
        cookieManager.removeAllCookies(null)
        cookieManager.flush()
    }

    /**
     * Obtiene el email del usuario autenticado (si está disponible)
     */
    fun getUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_EMAIL, null)
    }

    /**
     * Obtiene las cookies para hacer requests autenticados
     */
    fun getAuthCookies(): String? {
        val cookieManager = CookieManager.getInstance()
        return cookieManager.getCookie("https://drive.google.com")
    }

    /**
     * Verifica si las cookies están expiradas o son inválidas
     */
    fun areCookiesValid(): Boolean {
        val cookies = getAuthCookies()
        return !cookies.isNullOrEmpty() && cookies.contains("SID")
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
