package netman.api

import io.micronaut.security.authentication.Authentication

fun getUserId(authentication: Authentication) : String {
    return authentication.name
}