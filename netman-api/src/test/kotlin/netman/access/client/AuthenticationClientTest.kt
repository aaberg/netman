package netman.access.client

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import netman.access.repository.DefaultTestProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@WireMockTest(httpPort = 8091)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@MicronautTest(startApplication = false)
class AuthenticationClientTest : DefaultTestProperties() {

    @Inject
    private lateinit var authenticationClient: AuthenticationClient

    @Test
    fun `test the authentication client with a non-valid token`(wmRuntimeInfo: WireMockRuntimeInfo) {
        // Arrange
        setupAuthenticationClientForFailedAuthentication(wmRuntimeInfo)

        // Act
        val token = "dummy"
        val response = authenticationClient.validateSession(SessionValidationRequest(token)).block()

        // Assert
        assertThat(response?.isValid).isFalse()
    }

    @Test
    fun `test the authentication client a valid token`(wmRuntimeInfo: WireMockRuntimeInfo) {
        // Arrange
        setupAuthenticationClientForSuccessfullAuthentication(wmRuntimeInfo)

        // Act
        val result = authenticationClient.validateSession(SessionValidationRequest("dummy")).block()

        // Assert
        assertThat(result?.isValid).isTrue
        assertThat(result?.userId).isEqualTo("062a7851-88e2-41aa-aeb4-dcad0c3bcf34")
        assertThat(result?.claims).isNotNull
        assertThat(result?.claims?.email?.address).isEqualTo("lars@aaberg.cc")
    }
}