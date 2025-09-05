package netman.api.contacts

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import netman.api.contacts.models.Contact

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/api/contacts")
class ContactController {

    @Get(produces = ["application/json"])
    fun getContacts() : HttpResponse<List<Contact>> {
        return HttpResponse.ok( listOf(
            Contact(1, "John Doe"),
            Contact(2, "Jane Smith"),
            Contact(3, "Alice Johnson")
        ))
    }
}