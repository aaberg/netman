package netman.access

import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import net.aabergs.client.privateapi.FileserverClient
import net.aabergs.client.privateapi.FileserverClientException
import net.aabergs.client.privateapi.NotFoundException
import net.aabergs.client.privateapi.dto.TemporaryFileUploadResponse
import netman.businesslogic.TimeService
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

@Singleton
class FileAccess(
    @param:Value("\${fileserver.private.base-url:`http://localhost:9001`}")
    private val fileserverBaseUrl: String,
    @param:Value("\${fileserver.private.token:`dev-token`}")
    private val fileserverToken: String,
    @param:Value("\${fileserver.public-url-duration-seconds:3600}")
    private val publicUrlDurationSeconds: Long,
    @param:Value("\${fileserver.public-url-refresh-skew-seconds:30}")
    private val publicUrlRefreshSkewSeconds: Long,
    @param:Value("\${fileserver.missing-cache-seconds:300}")
    private val missingCacheSeconds: Long,
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
            val publicUrl = fileserverClient.createPublicUrl(fileKey, publicUrlDurationSeconds)
            publicUrlCache.put(fileKey, CachedPublicUrl(publicUrl, now.plusSeconds(positiveCacheSeconds())))
            publicUrl
        } catch (_: NotFoundException) {
            publicUrlCache.put(fileKey, CachedPublicUrl(null, now.plusSeconds(missingCacheSeconds.coerceAtLeast(1))))
            null
        } catch (_: Exception) {
            null
        }
    }

    fun createPublicUrl(fileKey: String, durationSeconds: Long): String {
        try {
            return fileserverClient.createPublicUrl(fileKey, durationSeconds.coerceAtLeast(1))
        } catch (e: FileserverClientException) {
            throw FileAccessException("Failed to create public URL for file key '$fileKey'", e)
        } catch (e: Exception) {
            throw FileAccessException("Unexpected error while creating public URL for file key '$fileKey'", e)
        }
    }

    fun storeTemporaryFile(content: ByteArray): TemporaryFileUploadResponse {
        try {
            return fileserverClient.uploadTemporaryFile(content)
        } catch (e: FileserverClientException) {
            throw FileAccessException("Failed to upload temporary file", e)
        } catch (e: Exception) {
            throw FileAccessException("Unexpected error while uploading temporary file", e)
        }
    }

    fun createTemporaryFilePublicUrl(tempFileId: String, durationSeconds: Long): String {
        try {
            return fileserverClient.createPublicUrlForTemporaryFile(tempFileId, durationSeconds.coerceAtLeast(1))
        } catch (e: FileserverClientException) {
            throw FileAccessException("Failed to create public URL for temporary file '$tempFileId'", e)
        } catch (e: Exception) {
            throw FileAccessException("Unexpected error while creating public URL for temporary file '$tempFileId'", e)
        }
    }

    fun promoteFile(sourceFileKey: String, targetFileKey: String) {
        val content = fileserverClient.downloadFile(sourceFileKey)
        fileserverClient.uploadFile(targetFileKey, content)
        publicUrlCache.evict(sourceFileKey)
        publicUrlCache.evict(targetFileKey)
    }

    fun promoteTemporaryFile(tempFileId: String, targetFileKey: String) {
        try {
            fileserverClient.promoteTemporaryFile(tempFileId, targetFileKey)
        } catch (e: FileserverClientException) {
            throw FileAccessException("Failed to promote temporary file '$tempFileId' to '$targetFileKey'", e)
        } catch (e: Exception) {
            throw FileAccessException("Unexpected error while promoting temporary file '$tempFileId'", e)
        }
        publicUrlCache.evict(targetFileKey)
    }

    fun deleteTemporaryFile(tempFileId: String) {
        try {
            fileserverClient.deleteTemporaryFile(tempFileId)
        } catch (_: NotFoundException) {
        } catch (e: FileserverClientException) {
            throw FileAccessException("Failed to delete temporary file '$tempFileId'", e)
        } catch (e: Exception) {
            throw FileAccessException("Unexpected error while deleting temporary file '$tempFileId'", e)
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
        val normalizedDuration = publicUrlDurationSeconds.coerceAtLeast(1)
        val normalizedSkew = publicUrlRefreshSkewSeconds.coerceAtLeast(0)
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

class FileAccessException(message: String, cause: Throwable) : RuntimeException(message, cause)
