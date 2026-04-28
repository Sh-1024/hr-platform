package com.s1024.hr_platform.ui.state

data class HoroscopeUiState(
    val isOnline: Boolean = true,
    val sign: String? = null,
    val text: String? = null
) {
    val isAvailable: Boolean
        get() = !sign.isNullOrBlank() && !text.isNullOrBlank()
}
