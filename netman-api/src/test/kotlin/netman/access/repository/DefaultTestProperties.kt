package netman.access.repository

import io.micronaut.core.annotation.NonNull
import io.micronaut.test.support.TestPropertyProvider

//@Testcontainers
abstract class DefaultTestProperties : TestPropertyProvider {
//    companion object {
//        private val network: Network = Network.newNetwork()

//        @JvmStatic
//        private val postgres: PostgreSQLContainer<*> =
//            PostgreSQLContainer("postgres:17-alpine")
//                .withDatabaseName("netman")
//                .withUsername("test")
//                .withPassword("test")
//                .withNetwork(network)
//                .withNetworkAliases("postgres")
//
//        @JvmStatic
//        private val liquibase: GenericContainer<*> =
//            GenericContainer(DockerImageName.parse("liquibase/liquibase:latest"))
//                .withNetwork(network)
//                .withClasspathResourceMapping("/db", "/liquibase/changelog", BindMode.READ_ONLY)
//                .withEnv("LIQUIBASE_SEARCH_PATH", "/liquibase/changelog")
//                .withEnv("LIQUIBASE_COMMAND_URL", "jdbc:postgresql://postgres:5432/netman")
//                .withEnv("LIQUIBASE_COMMAND_USERNAME", "test")
//                .withEnv("LIQUIBASE_COMMAND_PASSWORD", "test")
//                .withCommand(
//                    "--changelog-file=liquibase-changelog.xml",
//                    "update"
//                )
//                .waitingFor(
//                    Wait.forLogMessage(".*Liquibase command 'update' was executed successfully.*", 1)
//                )
//    }

    override fun getProperties(): @NonNull Map<String?, String?>? {
//        if (!postgres.isRunning) {
//            postgres.start()
//            liquibase.start()
//        }

        return mutableMapOf(
            "datasources.default.url" to "jdbc:postgresql://localhost:5433/netman", //postgres.jdbcUrl,
            "datasources.default.username" to "netman",  //postgres.username,
            "datasources.default.password" to "netman123", //postgres.password,
            "datasources.default.driverClassName" to "org.postgresql.Driver",
            "datasources.default.db-type" to "postgres",
            "datasources.default.dialect" to "POSTGRES",
            "hanko.base-url" to "http://localhost:8091",

            "micronaut.security.enabled" to "true",
            "micronaut.security.authentication" to "bearer",
            "logging.level.io.micronaut.security" to "DEBUG",

        )
    }
}