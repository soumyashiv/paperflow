package com.paperflow.app.presentation.scanner

/**
 * Represents every category the Smart Scanner can automatically identify.
 *
 * [emoji]       — shown in the animated detection badge.
 * [displayName] — human-readable label for the badge.
 * [cropAspect]  — preferred width:height ratio for the edge-overlay guide.
 *                 null = unconstrained (full-screen crop box).
 * [isCard]      — true when the document fits ISO 7810 ID-1 card dimensions.
 */
enum class DocumentType(
    val emoji: String,
    val displayName: String,
    val cropAspect: Float?,  // width / height
    val isCard: Boolean = false,
) {
    // ── Unknown / no detection ────────────────────────────────────────────
    UNKNOWN(
        emoji = "🔍",
        displayName = "Scanning…",
        cropAspect = null,
    ),

    // ── Generic document / paper ──────────────────────────────────────────
    DOCUMENT(
        emoji = "📄",
        displayName = "Document",
        cropAspect = 0.707f,   // A4 / Letter portrait
    ),

    // ── Books ─────────────────────────────────────────────────────────────
    BOOK(
        emoji = "📕",
        displayName = "Book",
        cropAspect = 0.75f,
    ),
    OPEN_BOOK(
        emoji = "📖",
        displayName = "Open Book",
        cropAspect = 1.6f,     // Two-page spread, landscape
    ),

    // ── Notebooks / notes ─────────────────────────────────────────────────
    NOTEBOOK(
        emoji = "📓",
        displayName = "Notebook",
        cropAspect = 0.75f,
    ),
    HANDWRITTEN_NOTE(
        emoji = "✍️",
        displayName = "Handwritten Note",
        cropAspect = 0.707f,
    ),
    PRINTED_NOTE(
        emoji = "🗒️",
        displayName = "Printed Note",
        cropAspect = 0.707f,
    ),

    // ── Identity / cards ──────────────────────────────────────────────────
    ID_CARD_AADHAAR(
        emoji = "🪪",
        displayName = "Aadhaar Card",
        cropAspect = 1.586f,
        isCard = true,
    ),
    ID_CARD_PAN(
        emoji = "🪪",
        displayName = "PAN Card",
        cropAspect = 1.586f,
        isCard = true,
    ),
    ID_CARD_PASSPORT(
        emoji = "📘",
        displayName = "Passport",
        cropAspect = 0.707f,   // passport booklet open page
    ),
    ID_CARD_DRIVING_LICENCE(
        emoji = "🪪",
        displayName = "Driving Licence",
        cropAspect = 1.586f,
        isCard = true,
    ),
    ID_CARD_VOTER(
        emoji = "🪪",
        displayName = "Voter ID",
        cropAspect = 1.586f,
        isCard = true,
    ),
    ID_CARD_STUDENT(
        emoji = "🎓",
        displayName = "Student ID",
        cropAspect = 1.586f,
        isCard = true,
    ),
    ID_CARD_EMPLOYEE(
        emoji = "🏢",
        displayName = "Employee ID",
        cropAspect = 1.586f,
        isCard = true,
    ),
    ID_CARD_GENERIC(
        emoji = "🪪",
        displayName = "ID Card",
        cropAspect = 1.586f,
        isCard = true,
    ),

    // ── Business card ─────────────────────────────────────────────────────
    BUSINESS_CARD(
        emoji = "💼",
        displayName = "Business Card",
        cropAspect = 1.75f,    // standard biz-card landscape
        isCard = true,
    ),

    // ── Financial / receipts ──────────────────────────────────────────────
    RECEIPT(
        emoji = "🧾",
        displayName = "Receipt",
        cropAspect = 0.38f,    // long narrow thermal receipt
    ),
    INVOICE(
        emoji = "🧾",
        displayName = "Invoice",
        cropAspect = 0.707f,
    ),
    BILL(
        emoji = "💵",
        displayName = "Bill",
        cropAspect = 0.707f,
    ),

    // ── Formal documents ──────────────────────────────────────────────────
    CERTIFICATE(
        emoji = "🏆",
        displayName = "Certificate",
        cropAspect = 1.414f,   // landscape A4
    ),
    LICENCE(
        emoji = "📜",
        displayName = "Licence",
        cropAspect = 0.707f,
    ),
    FORM(
        emoji = "📋",
        displayName = "Form",
        cropAspect = 0.707f,
    ),
    LETTER(
        emoji = "✉️",
        displayName = "Letter",
        cropAspect = 0.707f,
    ),
    CONTRACT(
        emoji = "📑",
        displayName = "Contract",
        cropAspect = 0.707f,
    ),
}
