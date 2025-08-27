package netman.api.access.client

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson
import io.micronaut.context.ApplicationContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@WireMockTest(httpPort = 8090)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationClientTest {

    private lateinit var context: ApplicationContext
    private lateinit var authenticationClient: AuthenticationClient

    @BeforeAll
    fun setup() {
        context = ApplicationContext.builder()
            .properties(
                mapOf(
                    "hanko.base-url" to "http://localhost:8090",
                    "datasources.default.enabled" to "false",
                    "micronaut.bean.context.exclude" to "netman.api.access.repository.ContactRepository"
                )
            )
            .start()
        authenticationClient = context.getBean(AuthenticationClient::class.java)
    }

    @Test
    fun `test the authentication client with a non-valid token`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val wm = wmRuntimeInfo.wireMock

        wm.post {
            url equalTo "/sessions/validate"
        } returnsJson {
            statusCode = 200
            body = """
                {
                    "is_valid": false
                }
                """.trimIndent()
        }

        val token = "dummy"
        val response = authenticationClient.validateSession(SessionValidationRequest(token))

        assertThat(response.isValid).isFalse()
    }
}