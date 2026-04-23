package com.drsecuritygps.app.network

object ApiEnvironment {
    const val baseUrl: String = "https://drsecuritygps.com/api"
    const val defaultLanguage: String = "es"
    const val geoapifyReverseGeocodeUrl: String = "https://api.geoapify.com/v1/geocode/reverse"
    const val geoapifyApiKey: String = "ef12251034564828a67d8e8ad79e5197"
    const val geoapifyAttribution: String = "Powered by Geoapify"

    val siteOrigin: String
        get() = baseUrl.removeSuffix("/api").trimEnd('/')
}

/** `path` devuelto por Wox p.ej. `frontend/images/map_icons/...` → URL absoluta. */
fun woxAssetUrl(path: String): String =
    if (path.startsWith("http", ignoreCase = true)) path else "${ApiEnvironment.siteOrigin}/${path.trimStart('/')}"
