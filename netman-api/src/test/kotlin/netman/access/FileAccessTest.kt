package netman.access

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.delete
import com.github.tomakehurst.wiremock.client.WireMock.deleteRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.put
import com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(startApplication = false)
class FileAccessTest : DefaultTestProperties() {

    @Inject
    private lateinit var fileAccess: FileAccess

    @Test
    fun `getFilePublicUrl uses cache for repeated lookups`() {
        val key = "t10-file-first.png"
        val publicUrlPath = "/file/$key/public-url"
        val expectedUrl = "https://cdn.test/$key"

        stubFor(
            post(urlEqualTo(publicUrlPath))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"publicUrl\":\"$expectedUrl\"}")
                )
        )

        val first = fileAccess.getFilePublicUrl(key)
        val second = fileAccess.getFilePublicUrl(key)

        assertThat(first).isEqualTo(expectedUrl)
        assertThat(second).isEqualTo(expectedUrl)
        verify(1, postRequestedFor(urlEqualTo(publicUrlPath)))
    }

    @Test
    fun `storeFile evicts cached public URL`() {
        val key = "t11-file-refresh.jpg"
        val publicUrlPath = "/file/$key/public-url"
        val filePath = "/file/$key"

        stubFor(put(urlEqualTo(filePath)).willReturn(aResponse().withStatus(200)))
        stubFor(
            post(urlEqualTo(publicUrlPath))
                .inScenario("public-url-refresh-$key")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("refreshed")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"publicUrl\":\"https://cdn.test/first/$key\"}")
                )
        )
        stubFor(
            post(urlEqualTo(publicUrlPath))
                .inScenario("public-url-refresh-$key")
                .whenScenarioStateIs("refreshed")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"publicUrl\":\"https://cdn.test/second/$key\"}")
                )
        )

        val first = fileAccess.getFilePublicUrl(key)
        val cached = fileAccess.getFilePublicUrl(key)
        fileAccess.storeFile(key, "image-content".toByteArray())
        val refreshed = fileAccess.getFilePublicUrl(key)

        assertThat(first).isEqualTo("https://cdn.test/first/$key")
        assertThat(cached).isEqualTo("https://cdn.test/first/$key")
        assertThat(refreshed).isEqualTo("https://cdn.test/second/$key")
        verify(1, putRequestedFor(urlEqualTo(filePath)))
        verify(2, postRequestedFor(urlEqualTo(publicUrlPath)))
    }

    @Test
    fun `getFilePublicUrl negative caches missing files`() {
        val key = "t12-file-missing.webp"
        val publicUrlPath = "/file/$key/public-url"

        stubFor(
            post(urlEqualTo(publicUrlPath))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"not found\"}")
                )
        )

        val first = fileAccess.getFilePublicUrl(key)
        val second = fileAccess.getFilePublicUrl(key)

        assertThat(first).isNull()
        assertThat(second).isNull()
        verify(1, postRequestedFor(urlEqualTo(publicUrlPath)))
    }

    @Test
    fun `deleteFile is idempotent and evicts cache`() {
        val key = "t13-file-delete.avif"
        val publicUrlPath = "/file/$key/public-url"
        val filePath = "/file/$key"

        stubFor(delete(urlEqualTo(filePath)).willReturn(aResponse().withStatus(404)))
        stubFor(
            post(urlEqualTo(publicUrlPath))
                .inScenario("delete-eviction-$key")
                .whenScenarioStateIs(STARTED)
                .willSetStateTo("deleted")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"publicUrl\":\"https://cdn.test/first-delete/$key\"}")
                )
        )
        stubFor(
            post(urlEqualTo(publicUrlPath))
                .inScenario("delete-eviction-$key")
                .whenScenarioStateIs("deleted")
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"publicUrl\":\"https://cdn.test/second-delete/$key\"}")
                )
        )

        val beforeDelete = fileAccess.getFilePublicUrl(key)
        fileAccess.deleteFile(key)
        val afterDelete = fileAccess.getFilePublicUrl(key)

        assertThat(beforeDelete).isEqualTo("https://cdn.test/first-delete/$key")
        assertThat(afterDelete).isEqualTo("https://cdn.test/second-delete/$key")
        verify(1, deleteRequestedFor(urlEqualTo(filePath)))
        verify(2, postRequestedFor(urlEqualTo(publicUrlPath)))
    }

}
