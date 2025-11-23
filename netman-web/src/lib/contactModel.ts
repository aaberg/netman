
export interface ContactWithDetails {
    contact: Contact,
    details: ContactDetail<Email | Phone | Note>[]
}

export interface Contact {
    id: String | null,
    name: String,
    initials: String,
    contactInfo: String,
    contactInfoIcon: String,
    hasUpdates: boolean
}

export interface ContactDetail<T> {
    id: BigInt | null,
    detail: T
}

export interface Email {
    type: string,
    address: string,
    label: string,
    isPrimary: boolean,
}

export interface Phone {
    type: string,
    number: string,
    label: string,
}

export interface Note {
    type: string,
    note: string,
}