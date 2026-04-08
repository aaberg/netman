package netman.businesslogic

import io.micronaut.http.exceptions.HttpStatusException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class ImageMimeTypeDetectorTest {
    private val detector = ImageMimeTypeDetector()

    @Test
    fun `detects png format`() {
        val pngHeader = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x00
        )

        val result = detector.detectSupportedImageFormat(pngHeader)

        assertThat(result.extension).isEqualTo("png")
        assertThat(result.mimeType).isEqualTo("image/png")
    }

    @Test
    fun `rejects unsupported payload`() {
        val content = "not-an-image".toByteArray()

        assertThatThrownBy { detector.detectSupportedImageFormat(content) }
            .isInstanceOf(HttpStatusException::class.java)
    }
}
