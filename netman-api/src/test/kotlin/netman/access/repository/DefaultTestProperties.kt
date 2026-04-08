package netman.access.repository

import io.micronaut.core.annotation.NonNull
import io.micronaut.test.support.TestPropertyProvider

abstract class DefaultTestProperties : TestPropertyProvider {

    override fun getProperties(): @NonNull Map<String?, String?>? {
        return mutableMapOf(
            "datasources.default.url" to "jdbc:postgresql://localhost:5433/netman", //postgres.jdbcUrl,
            "datasources.default.username" to "netman",  //postgres.username,
            "datasources.default.password" to "netman123", //postgres.password,
            "datasources.default.driverClassName" to "org.postgresql.Driver",
            "datasources.default.db-type" to "postgres",
            "datasources.default.dialect" to "POSTGRES",
            "hanko.base-url" to "http://localhost:8091",
            "fileserver.private.base-url" to "http://localhost:8091",
            "fileserver.private.token" to "test-token",
            "fileserver.public-url-duration-seconds" to "120",
            "fileserver.public-url-refresh-skew-seconds" to "10",
            "fileserver.missing-cache-seconds" to "10",

            "micronaut.security.enabled" to "true",
            "micronaut.security.authentication" to "bearer",
            "logging.level.io.micronaut.security" to "DEBUG",
            
            "nats.default.addresses[0]" to "nats://localhost:4222",
        )
    }
}
