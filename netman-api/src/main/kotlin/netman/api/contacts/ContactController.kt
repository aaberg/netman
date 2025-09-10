package netman.api.contacts

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import netman.api.contacts.models.ContactResource

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/contacts")
class ContactController {

    @Get(produces = ["application/json"])
    fun getContacts() : HttpResponse<List<ContactResource>> {
        return HttpResponse.ok( listOf(
            ContactResource(1, "John Doe"),
            ContactResource(2, "Jane Smith"),
            ContactResource(3, "Alice Johnson")
        ))
    }
}