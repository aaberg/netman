package netman.access.client

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.marcinziolo.kotlin.wiremock.equalTo
import com.marcinziolo.kotlin.wiremock.post
import com.marcinziolo.kotlin.wiremock.returnsJson

fun setupAuthenticationClientForSuccessfullAuthentication(
    wmRuntimeInfo: WireMockRuntimeInfo,
    userId: String = "062a7851-88e2-41aa-aeb4-dcad0c3bcf34"
) {
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
                        "subject": "${userId}"
                      },
                      "expiration_time": "2025-08-29T17:11:51Z",
                      "user_id": "${userId}"
                    }
                """.trimIndent()
    }
}

fun setupAuthenticationClientForFailedAuthentication(wmRuntimeInfo: WireMockRuntimeInfo) {
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
}