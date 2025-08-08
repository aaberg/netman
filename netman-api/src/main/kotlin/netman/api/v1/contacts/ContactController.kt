 package netman.api.v1.contacts

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import netman.api.v1.contacts.models.Contact

 @Controller("/api/v1/contacts")
class ContactController {

    @Get(produces = ["application/json"])
    fun GetContacts() : HttpResponse<List<Contact>> {
        return HttpResponse.ok( listOf(
            Contact(1, "John Doe"),
            Contact(2, "Jane Smith"),
            Contact(3, "Alice Johnson")
        ))
    }
}