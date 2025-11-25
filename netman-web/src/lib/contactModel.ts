
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

export function compareDetails(a: Email | Phone | Note, b: Email | Phone | Note) {
    const val = (d: Email | Phone | Note) => {
        if (d.type === "email") return 1
        else if (d.type === "phone") return 2
        else if (d.type === "note") return 3
        else return 4
    }
    if (val(a) == val(b)) return 0
    if (val(a) > val(b)) return 1
    else return -1
}