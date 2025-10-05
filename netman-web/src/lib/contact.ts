
export interface Contact {
    id: String | null,
    name: String,
    initials: String,
    details: ContactDetail[]
}

export interface ContactDetail {
    id: BigInt | null,
    detail: Email | Phone | Notes
}

export interface Email {
    address: String,
    label: String,
    isPrimary: Boolean,
    type: String,
}

export interface Phone {
    number: String,
    label: String,
    type: String,
}

export interface Notes {
    note: String,
}