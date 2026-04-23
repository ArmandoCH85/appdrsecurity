package com.drsecuritygps.app.core

open class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)

class SessionExpiredException(message: String = "Tu sesion ha expirado.") : AppException(message)

class MapTimeoutException(message: String = "Tiempo de espera agotado.", cause: Throwable? = null) :
    AppException(message, cause)

class MapOfflineException(message: String = "Sin conectividad.", cause: Throwable? = null) :
    AppException(message, cause)

class MapUnauthorizedException(message: String = "Sesion no autorizada.", cause: Throwable? = null) :
    AppException(message, cause)

class MapInvalidPayloadException(message: String = "Respuesta invalida del servidor.", cause: Throwable? = null) :
    AppException(message, cause)
