package fairies.pixels.curlyLabAndroid.domain.usecase.auth

object AuthErrors {
    const val INVALID_EMAIL = "Неверный формат email"

    const val PASSWORD_EMPTY = "Пароль не может быть пустым"
    const val PASSWORD_TOO_SHORT = "Пароль должен содержать минимум 6 символов"
    const val PASSWORD_TOO_LONG = "Пароль должен содержать не более 20 символов"
    const val PASSWORD_NON_LATIN = "Пароль может содержать любые символы, но буквы только латинские"
    const val PASSWORDS_DONT_MATCH = "Пароли не совпадают"

    const val USERNAME_TOO_SHORT = "Имя должно содержать минимум 2 символа"
    const val USERNAME_TOO_LONG = "Имя должно содержать не более 20 символов"

    const val GOOGLE_TOKEN_EMPTY = "Google token отсутствует"

    const val FIELDS_REQUIRED = "Пожалуйста, заполните все поля"
    const val REGISTRATION_FAILED = "Ошибка регистрации"
    const val LOGIN_FAILED = "Ошибка входа"
    const val GOOGLE_LOGIN_FAILED = "Ошибка Google входа"
    const val NETWORK_ERROR = "Ошибка сети"
}