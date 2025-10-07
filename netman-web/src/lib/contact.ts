
export interface Contact {
    id: String | null,
    name: String,
    initials: String,
    details: ContactDetail<Email | Phone | Note>[]
}

export interface ContactDetail<T> {
    id: BigInt | null,
    type: string,
    detail: T
}

export interface Email {
    address: string,
    label: string,
    isPrimary: boolean,
}

export interface Phone {
    number: string,
    label: string,
}

export interface Note {
    note: string,
}