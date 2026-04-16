import { beforeEach, describe, expect, it, vi } from "vitest"

vi.mock("$lib/server/common", () => ({
  accessToken: vi.fn(() => "test-access-token")
}))

vi.mock("$lib/server/contact", () => ({
  saveContact: vi.fn(),
  uploadTemporaryContactImage: vi.fn()
}))

import {
  MAX_CONTACT_IMAGE_SIZE_BYTES,
  parseContactFormData,
  submitContactForm
} from "$lib/server/contactForm"
import { saveContact, uploadTemporaryContactImage } from "$lib/server/contact"

const mockedSaveContact = vi.mocked(saveContact)
const mockedUploadTemporaryContactImage = vi.mocked(uploadTemporaryContactImage)

const createImageFile = (size: number, type = "image/png"): File => {
  const file = new File([new Uint8Array(size)], "avatar.png", { type })
  Object.defineProperty(file, "arrayBuffer", {
    value: vi.fn().mockResolvedValue(new Uint8Array(size).buffer)
  })
  return file
}

describe("contactForm", () => {
  beforeEach(() => {
    vi.clearAllMocks()
    mockedSaveContact.mockResolvedValue({ id: "saved-contact-id" })
    mockedUploadTemporaryContactImage.mockResolvedValue({
      tempFileId: "temp-file-id",
      mimeType: "image/png",
      extension: "png",
      previewUrl: "https://cdn.test/temp-file-id",
      previewUrlExpiresAt: "2026-01-01T00:00:00Z"
    })
  })

  it("parses contact form data with trimming and null normalization", () => {
    const data = new FormData()
    data.set("name", "  Jane Doe  ")
    data.set("email", "   ")
    data.set("phone", "  +45 12345678 ")
    data.set("title", " CTO ")
    data.set("organization", " Example Corp ")
    data.set("location", " Aarhus ")
    data.set("notes", "  Important contact  ")

    expect(parseContactFormData(data, "contact-123")).toEqual({
      id: "contact-123",
      name: "Jane Doe",
      email: null,
      phone: "+45 12345678",
      title: "CTO",
      organization: "Example Corp",
      location: "Aarhus",
      notes: "Important contact",
      tempFileId: null,
      tempFileMimeType: null,
      tempFileExtension: null
    })
  })

  it("returns a validation error when name is missing", async () => {
    const data = new FormData()
    data.set("name", "   ")

    await expect(submitContactForm(data, {} as never, "tenant-1")).resolves.toEqual({
      values: {
        id: null,
        name: "",
        email: null,
        phone: null,
        title: null,
        organization: null,
        location: null,
        notes: null,
        tempFileId: null,
        tempFileMimeType: null,
        tempFileExtension: null
      },
      error: "Name is required."
    })

    expect(mockedUploadTemporaryContactImage).not.toHaveBeenCalled()
    expect(mockedSaveContact).not.toHaveBeenCalled()
  })

  it("submits form values directly when no image is provided", async () => {
    const data = new FormData()
    data.set("name", "Jane Doe")
    data.set("email", "jane@example.com")

    await expect(submitContactForm(data, {} as never, "tenant-1")).resolves.toEqual({
      values: {
        id: null,
        name: "Jane Doe",
        email: "jane@example.com",
        phone: null,
        title: null,
        organization: null,
        location: null,
        notes: null,
        tempFileId: null,
        tempFileMimeType: null,
        tempFileExtension: null
      },
      error: null,
      savedContactId: "saved-contact-id"
    })

    expect(mockedUploadTemporaryContactImage).not.toHaveBeenCalled()
    expect(mockedSaveContact).toHaveBeenCalledWith("test-access-token", "tenant-1", {
      id: null,
      name: "Jane Doe",
      email: "jane@example.com",
      phone: null,
      title: null,
      organization: null,
      location: null,
      notes: null,
      tempFileId: null,
      tempFileMimeType: null,
      tempFileExtension: null
    })
  })

  it("uploads the image first and includes temp file metadata when present", async () => {
    const data = new FormData()
    data.set("name", "Jane Doe")
    data.set("image", createImageFile(3))

    await submitContactForm(data, {} as never, "tenant-1", "contact-123")

    expect(mockedUploadTemporaryContactImage).toHaveBeenCalledWith(
      "test-access-token",
      "tenant-1",
      expect.any(ArrayBuffer)
    )
    expect(mockedSaveContact).toHaveBeenCalledWith("test-access-token", "tenant-1", {
      id: "contact-123",
      name: "Jane Doe",
      email: null,
      phone: null,
      title: null,
      organization: null,
      location: null,
      notes: null,
      tempFileId: "temp-file-id",
      tempFileMimeType: "image/png",
      tempFileExtension: ".png"
    })
  })

  it("rejects images larger than 1 MB before upload", async () => {
    const data = new FormData()
    data.set("name", "Jane Doe")
    data.set("image", createImageFile(MAX_CONTACT_IMAGE_SIZE_BYTES + 1))

    await expect(submitContactForm(data, {} as never, "tenant-1")).resolves.toEqual({
      values: {
        id: null,
        name: "Jane Doe",
        email: null,
        phone: null,
        title: null,
        organization: null,
        location: null,
        notes: null,
        tempFileId: null,
        tempFileMimeType: null,
        tempFileExtension: null
      },
      error: "Image must be 1 MB or smaller."
    })

    expect(mockedUploadTemporaryContactImage).not.toHaveBeenCalled()
    expect(mockedSaveContact).not.toHaveBeenCalled()
  })

  it("propagates upload failures to the caller", async () => {
    const data = new FormData()
    data.set("name", "Jane Doe")
    data.set("image", createImageFile(3))
    mockedUploadTemporaryContactImage.mockRejectedValueOnce(new Error("upload failed"))

    await expect(submitContactForm(data, {} as never, "tenant-1")).rejects.toThrow("upload failed")

    expect(mockedSaveContact).not.toHaveBeenCalled()
  })

  it("propagates save failures to the caller", async () => {
    const data = new FormData()
    data.set("name", "Jane Doe")
    mockedSaveContact.mockRejectedValueOnce(new Error("save failed"))

    await expect(submitContactForm(data, {} as never, "tenant-1")).rejects.toThrow("save failed")
  })
})
