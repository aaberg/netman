
export interface ContactWithDetails {
    id: string | null,
    name: string,
    initials: string,
    details: Array<Email | Phone | Note>
}

export interface Contact {
    id: string | null,
    name: string,
    initials: string,
    contactInfo: string,
    contactInfoIcon: string,
    hasUpdates: boolean
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