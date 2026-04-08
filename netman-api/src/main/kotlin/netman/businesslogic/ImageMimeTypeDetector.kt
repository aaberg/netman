package netman.businesslogic

import io.micronaut.http.HttpStatus
import io.micronaut.http.exceptions.HttpStatusException
import jakarta.inject.Singleton
import org.apache.tika.Tika

@Singleton
class ImageMimeTypeDetector {
    private val tika = Tika()

    fun detectSupportedImageFormat(content: ByteArray): DetectedImageFormat {
        if (content.isEmpty()) {
            throw HttpStatusException(HttpStatus.BAD_REQUEST, "Unsupported image format")
        }

        val detectedMimeType = tika.detect(content).lowercase()

        return when (detectedMimeType) {
            "image/png" -> DetectedImageFormat("png", "image/png")
            "image/jpeg", "image/jpg" -> DetectedImageFormat("jpg", "image/jpeg")
            "image/gif" -> DetectedImageFormat("gif", "image/gif")
            "image/webp" -> DetectedImageFormat("webp", "image/webp")
            "image/avif", "image/avif-sequence" -> DetectedImageFormat("avif", "image/avif")
            else -> throw HttpStatusException(HttpStatus.BAD_REQUEST, "Unsupported image format")
        }
    }
}

data class DetectedImageFormat(
    val extension: String,
    val mimeType: String
)
