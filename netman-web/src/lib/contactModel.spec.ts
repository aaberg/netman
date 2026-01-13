import { describe, it, expect } from "vitest"
import {
  compareDetails,
  type ContactWithDetails,
  type Email,
  type Phone,
  type Note
} from "./contactModel"

describe("contactModel", () => {
  describe("compareDetails", () => {
    it("should sort emails before phones", () => {
      const email: Email = {
        type: "email",
        address: "test@example.com",
        label: "Work",
        isPrimary: false
      }
      const phone: Phone = {
        type: "phone",
        number: "123-456-7890",
        label: "Mobile"
      }

      expect(compareDetails(email, phone)).toBe(-1)
      expect(compareDetails(phone, email)).toBe(1)
    })

    it("should sort phones before notes", () => {
      const phone: Phone = {
        type: "phone",
        number: "123-456-7890",
        label: "Mobile"
      }
      const note: Note = {
        type: "note",
        note: "This is a note"
      }

      expect(compareDetails(phone, note)).toBe(-1)
      expect(compareDetails(note, phone)).toBe(1)
    })

    it("should sort emails before notes", () => {
      const email: Email = {
        type: "email",
        address: "test@example.com",
        label: "Work",
        isPrimary: false
      }
      const note: Note = {
        type: "note",
        note: "This is a note"
      }

      expect(compareDetails(email, note)).toBe(-1)
      expect(compareDetails(note, email)).toBe(1)
    })

    it("should return 0 for same type of details", () => {
      const email1: Email = {
        type: "email",
        address: "test1@example.com",
        label: "Work",
        isPrimary: false
      }
      const email2: Email = {
        type: "email",
        address: "test2@example.com",
        label: "Personal",
        isPrimary: true
      }

      expect(compareDetails(email1, email2)).toBe(0)
    })

    it("should handle sorting a mixed array correctly", () => {
      const email: Email = {
        type: "email",
        address: "test@example.com",
        label: "Work",
        isPrimary: false
      }
      const phone: Phone = {
        type: "phone",
        number: "123-456-7890",
        label: "Mobile"
      }
      const note: Note = {
        type: "note",
        note: "This is a note"
      }

      const mixed = [note, phone, email]
      const sorted = mixed.sort(compareDetails)

      expect(sorted[0]).toBe(email)
      expect(sorted[1]).toBe(phone)
      expect(sorted[2]).toBe(note)
    })
  })

  describe("ContactWithDetails interface", () => {
    it("should handle contact with undefined details", () => {
      const contact: ContactWithDetails = {
        id: "123",
        name: "John Doe",
        initials: "JD",
        details: undefined
      }

      // Verify that undefined details can be handled with || [] pattern
      const details = (contact.details || []).sort(compareDetails)
      expect(details).toEqual([])
      expect(details.length).toBe(0)
    })

    it("should handle contact with null details", () => {
      const contact: ContactWithDetails = {
        id: "123",
        name: "John Doe",
        initials: "JD",
        details: null as any // null should be treated same as undefined
      }

      // Verify that null details can be handled with || [] pattern
      const details = (contact.details || []).sort(compareDetails)
      expect(details).toEqual([])
      expect(details.length).toBe(0)
    })

    it("should handle contact with empty details array", () => {
      const contact: ContactWithDetails = {
        id: "123",
        name: "John Doe",
        initials: "JD",
        details: []
      }

      const details = (contact.details || []).sort(compareDetails)
      expect(details).toEqual([])
      expect(details.length).toBe(0)
    })

    it("should handle contact with populated details array", () => {
      const email: Email = {
        type: "email",
        address: "john@example.com",
        label: "Work",
        isPrimary: true
      }
      const phone: Phone = {
        type: "phone",
        number: "555-1234",
        label: "Mobile"
      }

      const contact: ContactWithDetails = {
        id: "123",
        name: "John Doe",
        initials: "JD",
        details: [phone, email] // Intentionally out of sort order
      }

      const details = (contact.details || []).sort(compareDetails)
      expect(details.length).toBe(2)
      expect(details[0]).toBe(email) // Email should be first
      expect(details[1]).toBe(phone) // Phone should be second
    })
  })
})
