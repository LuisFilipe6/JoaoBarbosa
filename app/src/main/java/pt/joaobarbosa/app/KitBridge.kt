package pt.joaobarbosa.app

import android.content.Context
import android.webkit.JavascriptInterface
import org.json.JSONObject

/** Ponte JS <-> Android exposta como window.KitNative. */
class KitBridge(context: Context) {
    private val prefs = context.getSharedPreferences("kit", Context.MODE_PRIVATE)

    @JavascriptInterface
    fun isTestMode(): Boolean = BuildConfig.TEST_MODE

    @JavascriptInterface
    fun isUnlocked(): Boolean = prefs.getString("code", null)?.let { KitCode.isValid(it) } ?: false

    @JavascriptInterface
    fun redeem(code: String): Boolean {
        if (!KitCode.isValid(code)) return false
        prefs.edit().putString("code", KitCode.normalize(code)).apply()
        return true
    }

    /** Compra simulada (modo teste). Devolve JSON com nº de encomenda e código. */
    @JavascriptInterface
    fun testPurchase(name: String, email: String): String {
        if (!BuildConfig.TEST_MODE) return JSONObject().put("ok", false).toString()
        val code = KitCode.generate()
        val order = "TESTE-" + System.currentTimeMillis().toString().takeLast(6)
        prefs.edit().putString("code", KitCode.normalize(code)).putString("order", order)
            .putString("name", name.take(80)).putString("email", email.take(120)).apply()
        return JSONObject().put("ok", true).put("order", order).put("code", code).toString()
    }

    @JavascriptInterface
    fun reset() { prefs.edit().clear().apply() }
}
