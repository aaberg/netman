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
        val response = authenticationClient.validateSession(SessionValidationRequest(token)).block()


        assertThat(response?.isValid).isFalse()
    }

    @Test
    fun `test the authentication client a valid token`(wmRuntimeInfo: WireMockRuntimeInfo) {
        val wm = wmRuntimeInfo.wireMock

        wm.post {
            url equalTo "/sessions/validate"
        } returnsJson {
            statusCode = 200
            body = """
                    {
                      "is_valid": true,
                      "claims": {
                        "audience": [
                          "localhost"
                        ],
                        "email": {
                          "address": "lars@aaberg.cc",
                          "is_primary": true,
                          "is_verified": true
                        },
                        "expiration": "2025-08-29T17:11:51Z",
                        "issued_at": "2025-08-29T05:11:51Z",
                        "session_id": "a9babead-12aa-4556-927b-0bf86b3e3ea6",
                        "subject": "062a7851-88e2-41aa-aeb4-dcad0c3bcf34"
                      },
                      "expiration_time": "2025-08-29T17:11:51Z",
                      "user_id": "062a7851-88e2-41aa-aeb4-dcad0c3bcf34"
                    }
                """.trimIndent()
        }

        val result = authenticationClient.validateSession(SessionValidationRequest("dummy")).block()

        assertThat(result?.isValid).isTrue
        assertThat(result?.userId).isEqualTo("062a7851-88e2-41aa-aeb4-dcad0c3bcf34")
        assertThat(result?.claims).isNotNull
        assertThat(result?.claims?.email?.address).isEqualTo("lars@aaberg.cc")
    }
}