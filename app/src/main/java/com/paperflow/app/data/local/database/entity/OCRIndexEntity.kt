package com.paperflow.app.data.local.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * Full-Text Search index for OCR content.
 * FTS4 virtual table — SQLite handles all indexing.
 * Kept separate from PageEntity to allow fast FTS queries without joining.
 *
 * SR-SEARCH-002: Locked/hidden document OCR is excluded at the query layer,
 * not at the storage layer (the index itself contains all text, but queries
 * filter by document visibility before returning results).
 */
@Fts4(contentEntity = PageEntity::class)
@Entity(tableName = "ocr_index")
data class OCRIndexEntity(
    @PrimaryKey
    @ColumnInfo(name = "rowid")
    val pageId: Long,
    @ColumnInfo(name = "document_id")
    val documentId: Long,
    @ColumnInfo(name = "extracted_text")
    val extractedText: String,
)
