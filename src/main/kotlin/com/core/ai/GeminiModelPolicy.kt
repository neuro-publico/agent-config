package com.core.ai

object GeminiModelPolicy {
    const val GENERAL_FLASH_MODEL = "gemini-3.7-flash"
    const val GENERAL_FLASH_LITE_MODEL = "gemini-3.5-flash-lite"

    private val aliases = setOf("gemini", "gemini-flash-latest")
    private val specializedMarkers = setOf(
        "embedding", "imagen", "image", "live", "lyria", "native-audio",
        "omni", "pro", "tts", "veo"
    )

    fun resolve(model: String): String {
        val normalized = model.trim().lowercase()
        val isGeneralFlash = normalized in aliases || (
            normalized.startsWith("gemini-") &&
                normalized.contains("flash") &&
                specializedMarkers.none(normalized::contains)
            )
        if (!isGeneralFlash) return model
        return if (normalized.contains("lite")) GENERAL_FLASH_LITE_MODEL else GENERAL_FLASH_MODEL
    }
}
