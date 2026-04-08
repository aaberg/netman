package netman.access

import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import net.aabergs.client.privateapi.FileserverClient
import net.aabergs.client.privateapi.NotFoundException
import netman.businesslogic.TimeService
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Singleton
class FileAccess(
    @param:Value("\${fileserver.private.base-url:`http://localhost:9001`}")
    private val fileserverBaseUrl: String,
    @param:Value($$"${fileserver.private.token:`dev-token`}")
    private val fileserverToken: String,
    @param:Value($$"${fileserver.public-url-duration-minutes}")
    private val publicUrlDurationMinutes: Long,
    @param:Value($$"${fileserver.public-url-refresh-skew-minutes}")
    private val publicUrlRefreshSkewMinutes: Long,
    @param:Value($$"${fileserver.missing-cache-minutes:5}")
    private val missingCacheMinutes: Long,
    private val timeService: TimeService,
    private val publicUrlCache: PublicUrlCache
) {
    private val fileserverClient: FileserverClient = FileserverClient(fileserverBaseUrl, fileserverToken)

    fun storeFile(fileKey: String, content: ByteArray) {
        fileserverClient.uploadFile(fileKey, content)
        publicUrlCache.evict(fileKey)
    }

    fun getFilePublicUrl(fileKey: String): String? {
        val now = timeService.now()

        val cached = publicUrlCache.get(fileKey)
        if (cached != null) {
            if (cached.expiresAt.isAfter(now)) {
                return cached.publicUrl
            }
            publicUrlCache.evict(fileKey)
        }

        return try {
            val publicUrl = fileserverClient.createPublicUrl(fileKey, publicUrlDurationMinutes)
            publicUrlCache.put(fileKey, CachedPublicUrl(publicUrl, now.plusSeconds(positiveCacheSeconds())))
            publicUrl
        } catch (_: NotFoundException) {
            publicUrlCache.put(fileKey, CachedPublicUrl(null, now.plusSeconds(missingCacheMinutes.coerceAtLeast(1))))
            null
        } catch (_: Exception) {
            null
        }
    }

    fun deleteFile(fileKey: String) {
        try {
            fileserverClient.deleteFile(fileKey)
        } catch (_: NotFoundException) {
        }
        publicUrlCache.evict(fileKey)
    }

    private fun positiveCacheSeconds(): Long {
        val normalizedDuration = publicUrlDurationMinutes.coerceAtLeast(1)
        val normalizedSkew = publicUrlRefreshSkewMinutes.coerceAtLeast(0)
        return (normalizedDuration - normalizedSkew).coerceAtLeast(1)
    }
}

data class CachedPublicUrl(
    val publicUrl: String?,
    val expiresAt: Instant
)

interface PublicUrlCache {
    fun get(key: String): CachedPublicUrl?
    fun put(key: String, value: CachedPublicUrl)
    fun evict(key: String)
}

@Singleton
class InMemoryPublicUrlCache : PublicUrlCache {
    private val cache = ConcurrentHashMap<String, CachedPublicUrl>()

    override fun get(key: String): CachedPublicUrl? = cache[key]

    override fun put(key: String, value: CachedPublicUrl) {
        cache[key] = value
    }

    override fun evict(key: String) {
        cache.remove(key)
    }
}
