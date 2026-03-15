package netman.api.communication

import io.micronaut.http.annotation.Controller
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import netman.businesslogic.models.CommunicationResource
import netman.businesslogic.models.PageResource
import netman.businesslogic.models.RegisterCommunicationResource
import netman.businesslogic.models.SetCommunicationPropertiesResource
import java.util.UUID

@Controller("/api/tenants")
@Secured(SecurityRule.IS_AUTHENTICATED)
class CommunicationApiController(
    private val networkManager: netman.businesslogic.NetworkManager
) : CommunicationApi {
    override fun getCommunication(
        tenantId: Long,
        communicationId: UUID
    ): CommunicationResource? {
        TODO("Not yet implemented")
    }

    override fun getCommunications(
        tenantId: Long,
        contactId: UUID?,
        page: Int?,
        pageSize: Int?
    ): PageResource<CommunicationResource> {
        TODO("Not yet implemented")
    }

    override fun deleteCommunication(tenantId: Long, communicationId: UUID) {
        TODO("Not yet implemented")
    }

    override fun registerCommunication(
        tenantId: Long,
        registerCommunicationApi: RegisterCommunicationResource
    ): CommunicationResource {
        TODO("Not yet implemented")
    }

    override fun setCommunicationProperties(
        tenantId: Long,
        communicationId: UUID,
        setCommunicationPropertiesApi: SetCommunicationPropertiesResource
    ) {
        TODO("Not yet implemented")
    }


}