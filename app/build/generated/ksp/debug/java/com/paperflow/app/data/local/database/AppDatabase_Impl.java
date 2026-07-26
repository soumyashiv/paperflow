package com.paperflow.app.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.FtsTableInfo;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.paperflow.app.data.local.database.dao.ActivityDao;
import com.paperflow.app.data.local.database.dao.ActivityDao_Impl;
import com.paperflow.app.data.local.database.dao.AnnotationDao;
import com.paperflow.app.data.local.database.dao.AnnotationDao_Impl;
import com.paperflow.app.data.local.database.dao.DocumentDao;
import com.paperflow.app.data.local.database.dao.DocumentDao_Impl;
import com.paperflow.app.data.local.database.dao.FolderDao;
import com.paperflow.app.data.local.database.dao.FolderDao_Impl;
import com.paperflow.app.data.local.database.dao.NoteDao;
import com.paperflow.app.data.local.database.dao.NoteDao_Impl;
import com.paperflow.app.data.local.database.dao.OCRIndexDao;
import com.paperflow.app.data.local.database.dao.OCRIndexDao_Impl;
import com.paperflow.app.data.local.database.dao.PageDao;
import com.paperflow.app.data.local.database.dao.PageDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile DocumentDao _documentDao;

  private volatile PageDao _pageDao;

  private volatile OCRIndexDao _oCRIndexDao;

  private volatile NoteDao _noteDao;

  private volatile AnnotationDao _annotationDao;

  private volatile FolderDao _folderDao;

  private volatile ActivityDao _activityDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `documents` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `thumbnail_path` TEXT, `folder_id` INTEGER, `is_locked` INTEGER NOT NULL, `is_hidden` INTEGER NOT NULL, `file_path` TEXT NOT NULL, `page_count` INTEGER NOT NULL, `size_bytes` INTEGER NOT NULL, `is_favorite` INTEGER NOT NULL, `ocr_status` TEXT NOT NULL, `last_read_page` INTEGER NOT NULL, FOREIGN KEY(`folder_id`) REFERENCES `folders`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_folder_id` ON `documents` (`folder_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_is_hidden` ON `documents` (`is_hidden`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_created_at` ON `documents` (`created_at`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_documents_is_favorite` ON `documents` (`is_favorite`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `pages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `document_id` INTEGER NOT NULL, `page_number` INTEGER NOT NULL, `thumbnail_path` TEXT, `preview_path` TEXT, `extracted_text` TEXT, `order_index` INTEGER NOT NULL, FOREIGN KEY(`document_id`) REFERENCES `documents`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_pages_document_id` ON `pages` (`document_id`)");
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_pages_document_id_page_number` ON `pages` (`document_id`, `page_number`)");
        db.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `ocr_index` USING FTS4(`document_id` INTEGER NOT NULL, `extracted_text` TEXT NOT NULL, content=`pages`)");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_BEFORE_UPDATE BEFORE UPDATE ON `pages` BEGIN DELETE FROM `ocr_index` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_BEFORE_DELETE BEFORE DELETE ON `pages` BEGIN DELETE FROM `ocr_index` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_AFTER_UPDATE AFTER UPDATE ON `pages` BEGIN INSERT INTO `ocr_index`(`docid`, `document_id`, `extracted_text`) VALUES (NEW.`rowid`, NEW.`document_id`, NEW.`extracted_text`); END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_AFTER_INSERT AFTER INSERT ON `pages` BEGIN INSERT INTO `ocr_index`(`docid`, `document_id`, `extracted_text`) VALUES (NEW.`rowid`, NEW.`document_id`, NEW.`extracted_text`); END");
        db.execSQL("CREATE TABLE IF NOT EXISTS `notes` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT NOT NULL, `content` TEXT NOT NULL, `linked_document_id` INTEGER, `folder_id` INTEGER, `created_at` INTEGER NOT NULL, `updated_at` INTEGER NOT NULL, `is_handwritten` INTEGER NOT NULL, `is_favorite` INTEGER NOT NULL, `thumbnail_path` TEXT, FOREIGN KEY(`folder_id`) REFERENCES `folders`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`linked_document_id`) REFERENCES `documents`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_folder_id` ON `notes` (`folder_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_linked_document_id` ON `notes` (`linked_document_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_created_at` ON `notes` (`created_at`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_notes_is_favorite` ON `notes` (`is_favorite`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `annotations` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `page_id` INTEGER NOT NULL, `type` TEXT NOT NULL, `position_data` TEXT NOT NULL, `color` TEXT NOT NULL, `content` TEXT, `created_at` INTEGER NOT NULL, FOREIGN KEY(`page_id`) REFERENCES `pages`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_annotations_page_id` ON `annotations` (`page_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `folders` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `parent_folder_id` INTEGER, `is_locked` INTEGER NOT NULL, `is_hidden` INTEGER NOT NULL, `color_hex` TEXT NOT NULL, `icon_name` TEXT NOT NULL, `created_at` INTEGER NOT NULL, FOREIGN KEY(`parent_folder_id`) REFERENCES `folders`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_folders_parent_folder_id` ON `folders` (`parent_folder_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `tags` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `color_hex` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `document_tags` (`document_id` INTEGER NOT NULL, `tag_id` INTEGER NOT NULL, PRIMARY KEY(`document_id`, `tag_id`), FOREIGN KEY(`document_id`) REFERENCES `documents`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`tag_id`) REFERENCES `tags`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_document_tags_document_id` ON `document_tags` (`document_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_document_tags_tag_id` ON `document_tags` (`tag_id`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `activity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `document_id` INTEGER NOT NULL, `action` TEXT NOT NULL, `timestamp` INTEGER NOT NULL, FOREIGN KEY(`document_id`) REFERENCES `documents`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_activity_document_id` ON `activity` (`document_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_activity_timestamp` ON `activity` (`timestamp`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'fea3694754fc9dea3b12d8925bd3933e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `documents`");
        db.execSQL("DROP TABLE IF EXISTS `pages`");
        db.execSQL("DROP TABLE IF EXISTS `ocr_index`");
        db.execSQL("DROP TABLE IF EXISTS `notes`");
        db.execSQL("DROP TABLE IF EXISTS `annotations`");
        db.execSQL("DROP TABLE IF EXISTS `folders`");
        db.execSQL("DROP TABLE IF EXISTS `tags`");
        db.execSQL("DROP TABLE IF EXISTS `document_tags`");
        db.execSQL("DROP TABLE IF EXISTS `activity`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_BEFORE_UPDATE BEFORE UPDATE ON `pages` BEGIN DELETE FROM `ocr_index` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_BEFORE_DELETE BEFORE DELETE ON `pages` BEGIN DELETE FROM `ocr_index` WHERE `docid`=OLD.`rowid`; END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_AFTER_UPDATE AFTER UPDATE ON `pages` BEGIN INSERT INTO `ocr_index`(`docid`, `document_id`, `extracted_text`) VALUES (NEW.`rowid`, NEW.`document_id`, NEW.`extracted_text`); END");
        db.execSQL("CREATE TRIGGER IF NOT EXISTS room_fts_content_sync_ocr_index_AFTER_INSERT AFTER INSERT ON `pages` BEGIN INSERT INTO `ocr_index`(`docid`, `document_id`, `extracted_text`) VALUES (NEW.`rowid`, NEW.`document_id`, NEW.`extracted_text`); END");
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDocuments = new HashMap<String, TableInfo.Column>(15);
        _columnsDocuments.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("thumbnail_path", new TableInfo.Column("thumbnail_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("folder_id", new TableInfo.Column("folder_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("is_locked", new TableInfo.Column("is_locked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("is_hidden", new TableInfo.Column("is_hidden", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("file_path", new TableInfo.Column("file_path", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("page_count", new TableInfo.Column("page_count", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("size_bytes", new TableInfo.Column("size_bytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("is_favorite", new TableInfo.Column("is_favorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("ocr_status", new TableInfo.Column("ocr_status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocuments.put("last_read_page", new TableInfo.Column("last_read_page", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDocuments = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysDocuments.add(new TableInfo.ForeignKey("folders", "SET NULL", "NO ACTION", Arrays.asList("folder_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDocuments = new HashSet<TableInfo.Index>(4);
        _indicesDocuments.add(new TableInfo.Index("index_documents_folder_id", false, Arrays.asList("folder_id"), Arrays.asList("ASC")));
        _indicesDocuments.add(new TableInfo.Index("index_documents_is_hidden", false, Arrays.asList("is_hidden"), Arrays.asList("ASC")));
        _indicesDocuments.add(new TableInfo.Index("index_documents_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        _indicesDocuments.add(new TableInfo.Index("index_documents_is_favorite", false, Arrays.asList("is_favorite"), Arrays.asList("ASC")));
        final TableInfo _infoDocuments = new TableInfo("documents", _columnsDocuments, _foreignKeysDocuments, _indicesDocuments);
        final TableInfo _existingDocuments = TableInfo.read(db, "documents");
        if (!_infoDocuments.equals(_existingDocuments)) {
          return new RoomOpenHelper.ValidationResult(false, "documents(com.paperflow.app.data.local.database.entity.DocumentEntity).\n"
                  + " Expected:\n" + _infoDocuments + "\n"
                  + " Found:\n" + _existingDocuments);
        }
        final HashMap<String, TableInfo.Column> _columnsPages = new HashMap<String, TableInfo.Column>(7);
        _columnsPages.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPages.put("document_id", new TableInfo.Column("document_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPages.put("page_number", new TableInfo.Column("page_number", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPages.put("thumbnail_path", new TableInfo.Column("thumbnail_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPages.put("preview_path", new TableInfo.Column("preview_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPages.put("extracted_text", new TableInfo.Column("extracted_text", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPages.put("order_index", new TableInfo.Column("order_index", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPages = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPages.add(new TableInfo.ForeignKey("documents", "CASCADE", "NO ACTION", Arrays.asList("document_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPages = new HashSet<TableInfo.Index>(2);
        _indicesPages.add(new TableInfo.Index("index_pages_document_id", false, Arrays.asList("document_id"), Arrays.asList("ASC")));
        _indicesPages.add(new TableInfo.Index("index_pages_document_id_page_number", true, Arrays.asList("document_id", "page_number"), Arrays.asList("ASC", "ASC")));
        final TableInfo _infoPages = new TableInfo("pages", _columnsPages, _foreignKeysPages, _indicesPages);
        final TableInfo _existingPages = TableInfo.read(db, "pages");
        if (!_infoPages.equals(_existingPages)) {
          return new RoomOpenHelper.ValidationResult(false, "pages(com.paperflow.app.data.local.database.entity.PageEntity).\n"
                  + " Expected:\n" + _infoPages + "\n"
                  + " Found:\n" + _existingPages);
        }
        final HashSet<String> _columnsOcrIndex = new HashSet<String>(3);
        _columnsOcrIndex.add("document_id");
        _columnsOcrIndex.add("extracted_text");
        final FtsTableInfo _infoOcrIndex = new FtsTableInfo("ocr_index", _columnsOcrIndex, "CREATE VIRTUAL TABLE IF NOT EXISTS `ocr_index` USING FTS4(`document_id` INTEGER NOT NULL, `extracted_text` TEXT NOT NULL, content=`pages`)");
        final FtsTableInfo _existingOcrIndex = FtsTableInfo.read(db, "ocr_index");
        if (!_infoOcrIndex.equals(_existingOcrIndex)) {
          return new RoomOpenHelper.ValidationResult(false, "ocr_index(com.paperflow.app.data.local.database.entity.OCRIndexEntity).\n"
                  + " Expected:\n" + _infoOcrIndex + "\n"
                  + " Found:\n" + _existingOcrIndex);
        }
        final HashMap<String, TableInfo.Column> _columnsNotes = new HashMap<String, TableInfo.Column>(10);
        _columnsNotes.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("linked_document_id", new TableInfo.Column("linked_document_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("folder_id", new TableInfo.Column("folder_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("updated_at", new TableInfo.Column("updated_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("is_handwritten", new TableInfo.Column("is_handwritten", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("is_favorite", new TableInfo.Column("is_favorite", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNotes.put("thumbnail_path", new TableInfo.Column("thumbnail_path", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNotes = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysNotes.add(new TableInfo.ForeignKey("folders", "SET NULL", "NO ACTION", Arrays.asList("folder_id"), Arrays.asList("id")));
        _foreignKeysNotes.add(new TableInfo.ForeignKey("documents", "SET NULL", "NO ACTION", Arrays.asList("linked_document_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesNotes = new HashSet<TableInfo.Index>(4);
        _indicesNotes.add(new TableInfo.Index("index_notes_folder_id", false, Arrays.asList("folder_id"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_linked_document_id", false, Arrays.asList("linked_document_id"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_created_at", false, Arrays.asList("created_at"), Arrays.asList("ASC")));
        _indicesNotes.add(new TableInfo.Index("index_notes_is_favorite", false, Arrays.asList("is_favorite"), Arrays.asList("ASC")));
        final TableInfo _infoNotes = new TableInfo("notes", _columnsNotes, _foreignKeysNotes, _indicesNotes);
        final TableInfo _existingNotes = TableInfo.read(db, "notes");
        if (!_infoNotes.equals(_existingNotes)) {
          return new RoomOpenHelper.ValidationResult(false, "notes(com.paperflow.app.data.local.database.entity.NoteEntity).\n"
                  + " Expected:\n" + _infoNotes + "\n"
                  + " Found:\n" + _existingNotes);
        }
        final HashMap<String, TableInfo.Column> _columnsAnnotations = new HashMap<String, TableInfo.Column>(7);
        _columnsAnnotations.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnnotations.put("page_id", new TableInfo.Column("page_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnnotations.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnnotations.put("position_data", new TableInfo.Column("position_data", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnnotations.put("color", new TableInfo.Column("color", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnnotations.put("content", new TableInfo.Column("content", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAnnotations.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAnnotations = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysAnnotations.add(new TableInfo.ForeignKey("pages", "CASCADE", "NO ACTION", Arrays.asList("page_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesAnnotations = new HashSet<TableInfo.Index>(1);
        _indicesAnnotations.add(new TableInfo.Index("index_annotations_page_id", false, Arrays.asList("page_id"), Arrays.asList("ASC")));
        final TableInfo _infoAnnotations = new TableInfo("annotations", _columnsAnnotations, _foreignKeysAnnotations, _indicesAnnotations);
        final TableInfo _existingAnnotations = TableInfo.read(db, "annotations");
        if (!_infoAnnotations.equals(_existingAnnotations)) {
          return new RoomOpenHelper.ValidationResult(false, "annotations(com.paperflow.app.data.local.database.entity.AnnotationEntity).\n"
                  + " Expected:\n" + _infoAnnotations + "\n"
                  + " Found:\n" + _existingAnnotations);
        }
        final HashMap<String, TableInfo.Column> _columnsFolders = new HashMap<String, TableInfo.Column>(8);
        _columnsFolders.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("parent_folder_id", new TableInfo.Column("parent_folder_id", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("is_locked", new TableInfo.Column("is_locked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("is_hidden", new TableInfo.Column("is_hidden", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("icon_name", new TableInfo.Column("icon_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFolders.put("created_at", new TableInfo.Column("created_at", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFolders = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysFolders.add(new TableInfo.ForeignKey("folders", "CASCADE", "NO ACTION", Arrays.asList("parent_folder_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesFolders = new HashSet<TableInfo.Index>(1);
        _indicesFolders.add(new TableInfo.Index("index_folders_parent_folder_id", false, Arrays.asList("parent_folder_id"), Arrays.asList("ASC")));
        final TableInfo _infoFolders = new TableInfo("folders", _columnsFolders, _foreignKeysFolders, _indicesFolders);
        final TableInfo _existingFolders = TableInfo.read(db, "folders");
        if (!_infoFolders.equals(_existingFolders)) {
          return new RoomOpenHelper.ValidationResult(false, "folders(com.paperflow.app.data.local.database.entity.FolderEntity).\n"
                  + " Expected:\n" + _infoFolders + "\n"
                  + " Found:\n" + _existingFolders);
        }
        final HashMap<String, TableInfo.Column> _columnsTags = new HashMap<String, TableInfo.Column>(3);
        _columnsTags.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTags.put("color_hex", new TableInfo.Column("color_hex", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTags = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTags = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTags = new TableInfo("tags", _columnsTags, _foreignKeysTags, _indicesTags);
        final TableInfo _existingTags = TableInfo.read(db, "tags");
        if (!_infoTags.equals(_existingTags)) {
          return new RoomOpenHelper.ValidationResult(false, "tags(com.paperflow.app.data.local.database.entity.TagEntity).\n"
                  + " Expected:\n" + _infoTags + "\n"
                  + " Found:\n" + _existingTags);
        }
        final HashMap<String, TableInfo.Column> _columnsDocumentTags = new HashMap<String, TableInfo.Column>(2);
        _columnsDocumentTags.put("document_id", new TableInfo.Column("document_id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDocumentTags.put("tag_id", new TableInfo.Column("tag_id", "INTEGER", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDocumentTags = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysDocumentTags.add(new TableInfo.ForeignKey("documents", "CASCADE", "NO ACTION", Arrays.asList("document_id"), Arrays.asList("id")));
        _foreignKeysDocumentTags.add(new TableInfo.ForeignKey("tags", "CASCADE", "NO ACTION", Arrays.asList("tag_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesDocumentTags = new HashSet<TableInfo.Index>(2);
        _indicesDocumentTags.add(new TableInfo.Index("index_document_tags_document_id", false, Arrays.asList("document_id"), Arrays.asList("ASC")));
        _indicesDocumentTags.add(new TableInfo.Index("index_document_tags_tag_id", false, Arrays.asList("tag_id"), Arrays.asList("ASC")));
        final TableInfo _infoDocumentTags = new TableInfo("document_tags", _columnsDocumentTags, _foreignKeysDocumentTags, _indicesDocumentTags);
        final TableInfo _existingDocumentTags = TableInfo.read(db, "document_tags");
        if (!_infoDocumentTags.equals(_existingDocumentTags)) {
          return new RoomOpenHelper.ValidationResult(false, "document_tags(com.paperflow.app.data.local.database.entity.DocumentTagEntity).\n"
                  + " Expected:\n" + _infoDocumentTags + "\n"
                  + " Found:\n" + _existingDocumentTags);
        }
        final HashMap<String, TableInfo.Column> _columnsActivity = new HashMap<String, TableInfo.Column>(4);
        _columnsActivity.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("document_id", new TableInfo.Column("document_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("action", new TableInfo.Column("action", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsActivity.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysActivity = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysActivity.add(new TableInfo.ForeignKey("documents", "CASCADE", "NO ACTION", Arrays.asList("document_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesActivity = new HashSet<TableInfo.Index>(2);
        _indicesActivity.add(new TableInfo.Index("index_activity_document_id", false, Arrays.asList("document_id"), Arrays.asList("ASC")));
        _indicesActivity.add(new TableInfo.Index("index_activity_timestamp", false, Arrays.asList("timestamp"), Arrays.asList("ASC")));
        final TableInfo _infoActivity = new TableInfo("activity", _columnsActivity, _foreignKeysActivity, _indicesActivity);
        final TableInfo _existingActivity = TableInfo.read(db, "activity");
        if (!_infoActivity.equals(_existingActivity)) {
          return new RoomOpenHelper.ValidationResult(false, "activity(com.paperflow.app.data.local.database.entity.ActivityEntity).\n"
                  + " Expected:\n" + _infoActivity + "\n"
                  + " Found:\n" + _existingActivity);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "fea3694754fc9dea3b12d8925bd3933e", "d06e92e9f7a09668c51025a365f9a41d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(1);
    _shadowTablesMap.put("ocr_index", "pages");
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "documents","pages","ocr_index","notes","annotations","folders","tags","document_tags","activity");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `documents`");
      _db.execSQL("DELETE FROM `pages`");
      _db.execSQL("DELETE FROM `ocr_index`");
      _db.execSQL("DELETE FROM `notes`");
      _db.execSQL("DELETE FROM `annotations`");
      _db.execSQL("DELETE FROM `folders`");
      _db.execSQL("DELETE FROM `tags`");
      _db.execSQL("DELETE FROM `document_tags`");
      _db.execSQL("DELETE FROM `activity`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DocumentDao.class, DocumentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PageDao.class, PageDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(OCRIndexDao.class, OCRIndexDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NoteDao.class, NoteDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AnnotationDao.class, AnnotationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FolderDao.class, FolderDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ActivityDao.class, ActivityDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DocumentDao documentDao() {
    if (_documentDao != null) {
      return _documentDao;
    } else {
      synchronized(this) {
        if(_documentDao == null) {
          _documentDao = new DocumentDao_Impl(this);
        }
        return _documentDao;
      }
    }
  }

  @Override
  public PageDao pageDao() {
    if (_pageDao != null) {
      return _pageDao;
    } else {
      synchronized(this) {
        if(_pageDao == null) {
          _pageDao = new PageDao_Impl(this);
        }
        return _pageDao;
      }
    }
  }

  @Override
  public OCRIndexDao ocrIndexDao() {
    if (_oCRIndexDao != null) {
      return _oCRIndexDao;
    } else {
      synchronized(this) {
        if(_oCRIndexDao == null) {
          _oCRIndexDao = new OCRIndexDao_Impl(this);
        }
        return _oCRIndexDao;
      }
    }
  }

  @Override
  public NoteDao noteDao() {
    if (_noteDao != null) {
      return _noteDao;
    } else {
      synchronized(this) {
        if(_noteDao == null) {
          _noteDao = new NoteDao_Impl(this);
        }
        return _noteDao;
      }
    }
  }

  @Override
  public AnnotationDao annotationDao() {
    if (_annotationDao != null) {
      return _annotationDao;
    } else {
      synchronized(this) {
        if(_annotationDao == null) {
          _annotationDao = new AnnotationDao_Impl(this);
        }
        return _annotationDao;
      }
    }
  }

  @Override
  public FolderDao folderDao() {
    if (_folderDao != null) {
      return _folderDao;
    } else {
      synchronized(this) {
        if(_folderDao == null) {
          _folderDao = new FolderDao_Impl(this);
        }
        return _folderDao;
      }
    }
  }

  @Override
  public ActivityDao activityDao() {
    if (_activityDao != null) {
      return _activityDao;
    } else {
      synchronized(this) {
        if(_activityDao == null) {
          _activityDao = new ActivityDao_Impl(this);
        }
        return _activityDao;
      }
    }
  }
}
