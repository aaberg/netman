package netman.proxy

import io.micronaut.core.async.publisher.Publishers
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.client.ProxyHttpClient
import io.micronaut.http.filter.FilterChain
import io.micronaut.http.filter.HttpFilter
import io.micronaut.http.uri.UriBuilder
import io.micronaut.runtime.server.EmbeddedServer
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Filter("/**")
class WebProxyFilter(
    private val client: ProxyHttpClient,
    private val embeddedServer: EmbeddedServer,
) : HttpFilter {

    private val logger = LoggerFactory.getLogger(WebProxyFilter::class.java)

    val localPaths = setOf("api", "swagger-ui", "swagger", "scalar")

    override fun doFilter(
        request: HttpRequest<*>,
        chain: FilterChain?
    ): Publisher<out HttpResponse<*>> {

        val rootPath = request.uri.path
            ?.split("/")
            ?.firstOrNull { it.isNotEmpty() }
            ?: ""

        if (localPaths.contains(rootPath)) {
            return chain?.proceed(request)
                ?: Publishers.just(HttpResponse.notFound<Any>())
        } else {
            logger.info("the uri is {}", request.uri)
            return Flux.from (client.proxy(
                request.mutate()
                    .uri { b: UriBuilder ->
                        b.apply {
                            scheme("http")
                            host("127.0.0.1")
                            port(5173)
                        }
                    }
                )
            )
        }
    }
}