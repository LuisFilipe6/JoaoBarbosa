package pt.joaobarbosa.app

import java.security.SecureRandom

/**
 * Códigos de desbloqueio do kit: JB-XXXX-XXXX (6 chars aleatórios + 2 de verificação).
 * ATENÇÃO: validação local = só serve para testes. Em produção o código tem de ser
 * validado no servidor (ver README).
 */
object KitCode {
    private const val ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // 32 chars, sem I/O/0/1
    private val rnd = SecureRandom()

    fun normalize(raw: String): String = raw.uppercase().filter { it.isLetterOrDigit() }

    private fun check(body: String): String {
        var n = 0
        body.forEachIndexed { i, c -> n = (n * 31 + (ALPHABET.indexOf(c) + 1) * (i + 7)) % 1024 }
        return "${ALPHABET[n / 32]}${ALPHABET[n % 32]}"
    }

    fun generate(): String {
        val body = (1..6).map { ALPHABET[rnd.nextInt(ALPHABET.length)] }.joinToString("")
        val c = body + check(body)
        return "JB-${c.substring(0, 4)}-${c.substring(4)}"
    }

    fun isValid(raw: String): Boolean {
        val s = normalize(raw)
        if (BuildConfig.TEST_MODE && s == "TESTE2026") return true
        if (s.length != 10 || !s.startsWith("JB")) return false
        val body = s.substring(2, 8)
        if (body.any { ALPHABET.indexOf(it) < 0 }) return false
        return check(body) == s.substring(8)
    }
}
