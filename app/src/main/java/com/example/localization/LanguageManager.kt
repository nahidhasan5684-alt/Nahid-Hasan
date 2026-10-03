package com.example.localization

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    BENGALI("bn", "Bengali", "বাংলা"),
    SPANISH("es", "Spanish", "Español"),
    FRENCH("fr", "French", "Français")
}

object LanguageManager {
    var currentLanguage by mutableStateOf(AppLanguage.ENGLISH)

    fun getString(key: String): String {
        return translations[key]?.get(currentLanguage)
            ?: translations[key]?.get(AppLanguage.ENGLISH)
            ?: key
    }

    private val translations: Map<String, Map<AppLanguage, String>> = mapOf(
        "app_name" to mapOf(
            AppLanguage.ENGLISH to "SCORNER",
            AppLanguage.BENGALI to "স্কর্নার",
            AppLanguage.SPANISH to "SCORNER",
            AppLanguage.FRENCH to "SCORNER"
        ),
        "dashboard" to mapOf(
            AppLanguage.ENGLISH to "Dashboard",
            AppLanguage.BENGALI to "ড্যাশবোর্ড",
            AppLanguage.SPANISH to "Panel",
            AppLanguage.FRENCH to "Tableau de bord"
        ),
        "transcripts" to mapOf(
            AppLanguage.ENGLISH to "Transcripts & Certs",
            AppLanguage.BENGALI to "নম্বরপত্র ও সনদ",
            AppLanguage.SPANISH to "Calificaciones y Certs",
            AppLanguage.FRENCH to "Relevés et Certificats"
        ),
        "fees" to mapOf(
            AppLanguage.ENGLISH to "Fees & Payments",
            AppLanguage.BENGALI to "ফি ও পেমেন্ট",
            AppLanguage.SPANISH to "Tarifas y Pagos",
            AppLanguage.FRENCH to "Frais et Paiements"
        ),
        "reports" to mapOf(
            AppLanguage.ENGLISH to "Reporting & Analytics",
            AppLanguage.BENGALI to "প্রতিবেদন ও বিশ্লেষণ",
            AppLanguage.SPANISH to "Informes y Métricas",
            AppLanguage.FRENCH to "Rapports et Analyses"
        ),
        "search" to mapOf(
            AppLanguage.ENGLISH to "Search & Materials",
            AppLanguage.BENGALI to "অনুসন্ধান ও উপকরণ",
            AppLanguage.SPANISH to "Buscar y Materiales",
            AppLanguage.FRENCH to "Recherche et Cours"
        ),
        "portal" to mapOf(
            AppLanguage.ENGLISH to "Role Portals",
            AppLanguage.BENGALI to "ভূমিকা পোর্টাল",
            AppLanguage.SPANISH to "Portales de Rol",
            AppLanguage.FRENCH to "Portails de Rôle"
        ),
        "student_profile" to mapOf(
            AppLanguage.ENGLISH to "Student Profile",
            AppLanguage.BENGALI to "শিক্ষার্থী প্রোফাইল",
            AppLanguage.SPANISH to "Perfil del Estudiante",
            AppLanguage.FRENCH to "Profil de l'étudiant"
        ),
        "ssc_gpa" to mapOf(
            AppLanguage.ENGLISH to "SSC GPA",
            AppLanguage.BENGALI to "এসএসসি জিপিএ",
            AppLanguage.SPANISH to "Promedio SSC",
            AppLanguage.FRENCH to "Moyenne SSC"
        ),
        "college" to mapOf(
            AppLanguage.ENGLISH to "College / Institution",
            AppLanguage.BENGALI to "কলেজ / শিক্ষাপ্রতিষ্ঠান",
            AppLanguage.SPANISH to "Colegio / Institución",
            AppLanguage.FRENCH to "Collège / Institution"
        ),
        "class_grade" to mapOf(
            AppLanguage.ENGLISH to "Class / Grade",
            AppLanguage.BENGALI to "শ্রেণি / বিভাগ",
            AppLanguage.SPANISH to "Clase / Grado",
            AppLanguage.FRENCH to "Classe / Niveau"
        ),
        "address" to mapOf(
            AppLanguage.ENGLISH to "Address",
            AppLanguage.BENGALI to "ঠিকানা",
            AppLanguage.SPANISH to "Dirección",
            AppLanguage.FRENCH to "Adresse"
        ),
        "contact" to mapOf(
            AppLanguage.ENGLISH to "Contact Info",
            AppLanguage.BENGALI to "যোগাযোগ তথ্য",
            AppLanguage.SPANISH to "Contacto",
            AppLanguage.FRENCH to "Contact"
        ),
        "guardian_contact" to mapOf(
            AppLanguage.ENGLISH to "Guardian Contact",
            AppLanguage.BENGALI to "অভিভাবকের যোগাযোগ",
            AppLanguage.SPANISH to "Contacto del Tutor",
            AppLanguage.FRENCH to "Contact du Tuteur"
        ),
        "upcoming_deadlines" to mapOf(
            AppLanguage.ENGLISH to "Upcoming Deadlines",
            AppLanguage.BENGALI to "আসন্ন সময়সীমা",
            AppLanguage.SPANISH to "Próximos Plazos",
            AppLanguage.FRENCH to "Prochaines Échéances"
        ),
        "pay_fees" to mapOf(
            AppLanguage.ENGLISH to "Pay Now",
            AppLanguage.BENGALI to "পেমেন্ট করুন",
            AppLanguage.SPANISH to "Pagar Ahora",
            AppLanguage.FRENCH to "Payer Maintenant"
        ),
        "offline_mode" to mapOf(
            AppLanguage.ENGLISH to "Offline Ready",
            AppLanguage.BENGALI to "অফলাইন প্রস্তুত",
            AppLanguage.SPANISH to "Modo sin conexión",
            AppLanguage.FRENCH to "Prêt hors-ligne"
        ),
        "sync_now" to mapOf(
            AppLanguage.ENGLISH to "Sync Database",
            AppLanguage.BENGALI to "ডাটাবেজ সিঙ্ক করুন",
            AppLanguage.SPANISH to "Sincronizar",
            AppLanguage.FRENCH to "Synchroniser"
        ),
        "mfa_verification" to mapOf(
            AppLanguage.ENGLISH to "Multi-Factor Verification",
            AppLanguage.BENGALI to "মাল্টি-ফ্যাক্টর যাচাইকরণ",
            AppLanguage.SPANISH to "Verificación de Dos Pasos",
            AppLanguage.FRENCH to "Authentification Multi-facteurs"
        ),
        "share_profile" to mapOf(
            AppLanguage.ENGLISH to "Share Profile",
            AppLanguage.BENGALI to "প্রোফাইল শেয়ার করুন",
            AppLanguage.SPANISH to "Compartir Perfil",
            AppLanguage.FRENCH to "Partager le Profil"
        )
    )
}
