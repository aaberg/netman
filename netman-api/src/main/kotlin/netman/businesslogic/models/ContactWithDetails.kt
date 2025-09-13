package netman.businesslogic.models

import netman.models.CDetail
import netman.models.Contact
import netman.models.ContactDetail

data class ContactWithDetails(val contact: Contact, val details: List<ContactDetail<CDetail>>)