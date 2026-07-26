package com.paperflow.app.data.local.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ─── Document Entity ───────────────────────────────────────────────────────────
@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL,
        )
    ],
    indices = [
        Index("folder_id"),
        Index("is_hidden"),
        Index("created_at"),
        Index("is_favorite"),
    ],
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    /** "pdf", "jpg", "png", "note" */
    val type: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String? = null,

    @ColumnInfo(name = "folder_id")
    val folderId: Long? = null,

    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean = false,

    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false,

    @ColumnInfo(name = "file_path")
    val filePath: String,

    @ColumnInfo(name = "page_count")
    val pageCount: Int = 1,

    @ColumnInfo(name = "size_bytes")
    val sizeBytes: Long = 0,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "ocr_status")
    val ocrStatus: String = "pending", // "pending", "processing", "complete", "failed"

    @ColumnInfo(name = "last_read_page")
    val lastReadPage: Int = 0,
)

// ─── Page Entity ──────────────────────────────────────────────────────────────
@Entity(
    tableName = "pages",
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["document_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("document_id"),
        Index(value = ["document_id", "page_number"], unique = true),
    ],
)
data class PageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "document_id")
    val documentId: Long,

    @ColumnInfo(name = "page_number")
    val pageNumber: Int,

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String? = null,

    @ColumnInfo(name = "preview_path")
    val previewPath: String? = null,

    @ColumnInfo(name = "extracted_text")
    val extractedText: String? = null,

    @ColumnInfo(name = "order_index")
    val orderIndex: Int = 0,
)

// ─── Note Entity ──────────────────────────────────────────────────────────────
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["folder_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["linked_document_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index("folder_id"),
        Index("linked_document_id"),
        Index("created_at"),
        Index("is_favorite"),
    ],
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,

    /** Stored as rich text JSON (spans/paragraphs) */
    val content: String = "",

    @ColumnInfo(name = "linked_document_id")
    val linkedDocumentId: Long? = null,

    @ColumnInfo(name = "folder_id")
    val folderId: Long? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "is_handwritten")
    val isHandwritten: Boolean = false,

    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false,

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String? = null,
)

// ─── Annotation Entity ────────────────────────────────────────────────────────
@Entity(
    tableName = "annotations",
    foreignKeys = [
        ForeignKey(
            entity = PageEntity::class,
            parentColumns = ["id"],
            childColumns = ["page_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("page_id")],
)
data class AnnotationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "page_id")
    val pageId: Long,

    /**
     * "highlight" | "underline" | "strikethrough" |
     * "text" | "image" | "freehand" | "shape"
     */
    val type: String,

    /** JSON: bounds, path points, text content, shape type etc. */
    @ColumnInfo(name = "position_data")
    val positionData: String,

    /** Hex color string e.g. "#FFFF00" */
    val color: String = "#FFFF00",

    /** For text annotations: the text content */
    val content: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)

// ─── Folder Entity ────────────────────────────────────────────────────────────
@Entity(
    tableName = "folders",
    foreignKeys = [
        ForeignKey(
            entity = FolderEntity::class,
            parentColumns = ["id"],
            childColumns = ["parent_folder_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("parent_folder_id")],
)
data class FolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    @ColumnInfo(name = "parent_folder_id")
    val parentFolderId: Long? = null,

    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean = false,

    @ColumnInfo(name = "is_hidden")
    val isHidden: Boolean = false,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#F5C842",

    @ColumnInfo(name = "icon_name")
    val iconName: String = "folder",

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),
)

// ─── Tag Entity ───────────────────────────────────────────────────────────────
@Entity(tableName = "tags")
data class TagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,

    @ColumnInfo(name = "color_hex")
    val colorHex: String = "#2196F3",
)

// ─── Document-Tag Junction ────────────────────────────────────────────────────
@Entity(
    tableName = "document_tags",
    primaryKeys = ["document_id", "tag_id"],
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["document_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tag_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("document_id"), Index("tag_id")],
)
data class DocumentTagEntity(
    @ColumnInfo(name = "document_id")
    val documentId: Long,

    @ColumnInfo(name = "tag_id")
    val tagId: Long,
)

// ─── Activity Entity ──────────────────────────────────────────────────────────
@Entity(
    tableName = "activity",
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["document_id"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [
        Index("document_id"),
        Index("timestamp"),
    ],
)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "document_id")
    val documentId: Long,

    /**
     * "opened" | "scanned" | "annotated" | "shared" | "printed" |
     * "moved" | "renamed" | "deleted" | "converted" | "ocr_complete"
     */
    val action: String,

    val timestamp: Long = System.currentTimeMillis(),
)
