package com.paperflow.app.data.local.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.paperflow.app.data.local.database.entity.PageEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PageDao_Impl implements PageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PageEntity> __insertionAdapterOfPageEntity;

  private final EntityDeletionOrUpdateAdapter<PageEntity> __deletionAdapterOfPageEntity;

  private final EntityDeletionOrUpdateAdapter<PageEntity> __updateAdapterOfPageEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateExtractedText;

  private final SharedSQLiteStatement __preparedStmtOfUpdateThumbnail;

  private final SharedSQLiteStatement __preparedStmtOfUpdateOrder;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePageNumber;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllPagesForDocument;

  public PageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPageEntity = new EntityInsertionAdapter<PageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pages` (`id`,`document_id`,`page_number`,`thumbnail_path`,`preview_path`,`extracted_text`,`order_index`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDocumentId());
        statement.bindLong(3, entity.getPageNumber());
        if (entity.getThumbnailPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getThumbnailPath());
        }
        if (entity.getPreviewPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPreviewPath());
        }
        if (entity.getExtractedText() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getExtractedText());
        }
        statement.bindLong(7, entity.getOrderIndex());
      }
    };
    this.__deletionAdapterOfPageEntity = new EntityDeletionOrUpdateAdapter<PageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `pages` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PageEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfPageEntity = new EntityDeletionOrUpdateAdapter<PageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `pages` SET `id` = ?,`document_id` = ?,`page_number` = ?,`thumbnail_path` = ?,`preview_path` = ?,`extracted_text` = ?,`order_index` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PageEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getDocumentId());
        statement.bindLong(3, entity.getPageNumber());
        if (entity.getThumbnailPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getThumbnailPath());
        }
        if (entity.getPreviewPath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPreviewPath());
        }
        if (entity.getExtractedText() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getExtractedText());
        }
        statement.bindLong(7, entity.getOrderIndex());
        statement.bindLong(8, entity.getId());
      }
    };
    this.__preparedStmtOfUpdateExtractedText = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE pages SET extracted_text = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateThumbnail = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE pages SET thumbnail_path = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateOrder = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE pages SET order_index = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePageNumber = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE pages SET page_number = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllPagesForDocument = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM pages WHERE document_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertPage(final PageEntity page, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPageEntity.insertAndReturnId(page);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPages(final List<PageEntity> pages,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPageEntity.insert(pages);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePage(final PageEntity page, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPageEntity.handle(page);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePage(final PageEntity page, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPageEntity.handle(page);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object reorderPages(final long documentId, final List<Long> orderedPageIds,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> PageDao.DefaultImpls.reorderPages(PageDao_Impl.this, documentId, orderedPageIds, __cont), $completion);
  }

  @Override
  public Object updateExtractedText(final long pageId, final String text,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateExtractedText.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, text);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, pageId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateExtractedText.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateThumbnail(final long pageId, final String path,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateThumbnail.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, path);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, pageId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateThumbnail.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateOrder(final long pageId, final int order,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateOrder.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, order);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, pageId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateOrder.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePageNumber(final long pageId, final int pageNumber,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePageNumber.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, pageNumber);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, pageId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdatePageNumber.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllPagesForDocument(final long documentId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllPagesForDocument.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, documentId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAllPagesForDocument.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PageEntity>> getPagesForDocument(final long documentId) {
    final String _sql = "SELECT * FROM pages WHERE document_id = ? ORDER BY order_index ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, documentId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"pages"}, new Callable<List<PageEntity>>() {
      @Override
      @NonNull
      public List<PageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDocumentId = CursorUtil.getColumnIndexOrThrow(_cursor, "document_id");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "page_number");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnail_path");
          final int _cursorIndexOfPreviewPath = CursorUtil.getColumnIndexOrThrow(_cursor, "preview_path");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extracted_text");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "order_index");
          final List<PageEntity> _result = new ArrayList<PageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDocumentId;
            _tmpDocumentId = _cursor.getLong(_cursorIndexOfDocumentId);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final String _tmpPreviewPath;
            if (_cursor.isNull(_cursorIndexOfPreviewPath)) {
              _tmpPreviewPath = null;
            } else {
              _tmpPreviewPath = _cursor.getString(_cursorIndexOfPreviewPath);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new PageEntity(_tmpId,_tmpDocumentId,_tmpPageNumber,_tmpThumbnailPath,_tmpPreviewPath,_tmpExtractedText,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPagesForDocumentSync(final long documentId,
      final Continuation<? super List<PageEntity>> $completion) {
    final String _sql = "SELECT * FROM pages WHERE document_id = ? ORDER BY order_index ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, documentId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<PageEntity>>() {
      @Override
      @NonNull
      public List<PageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDocumentId = CursorUtil.getColumnIndexOrThrow(_cursor, "document_id");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "page_number");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnail_path");
          final int _cursorIndexOfPreviewPath = CursorUtil.getColumnIndexOrThrow(_cursor, "preview_path");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extracted_text");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "order_index");
          final List<PageEntity> _result = new ArrayList<PageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PageEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDocumentId;
            _tmpDocumentId = _cursor.getLong(_cursorIndexOfDocumentId);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final String _tmpPreviewPath;
            if (_cursor.isNull(_cursorIndexOfPreviewPath)) {
              _tmpPreviewPath = null;
            } else {
              _tmpPreviewPath = _cursor.getString(_cursorIndexOfPreviewPath);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _item = new PageEntity(_tmpId,_tmpDocumentId,_tmpPageNumber,_tmpThumbnailPath,_tmpPreviewPath,_tmpExtractedText,_tmpOrderIndex);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPageById(final long pageId, final Continuation<? super PageEntity> $completion) {
    final String _sql = "SELECT * FROM pages WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, pageId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PageEntity>() {
      @Override
      @Nullable
      public PageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDocumentId = CursorUtil.getColumnIndexOrThrow(_cursor, "document_id");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "page_number");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnail_path");
          final int _cursorIndexOfPreviewPath = CursorUtil.getColumnIndexOrThrow(_cursor, "preview_path");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extracted_text");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "order_index");
          final PageEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDocumentId;
            _tmpDocumentId = _cursor.getLong(_cursorIndexOfDocumentId);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final String _tmpPreviewPath;
            if (_cursor.isNull(_cursorIndexOfPreviewPath)) {
              _tmpPreviewPath = null;
            } else {
              _tmpPreviewPath = _cursor.getString(_cursorIndexOfPreviewPath);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _result = new PageEntity(_tmpId,_tmpDocumentId,_tmpPageNumber,_tmpThumbnailPath,_tmpPreviewPath,_tmpExtractedText,_tmpOrderIndex);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPage(final long documentId, final int pageNumber,
      final Continuation<? super PageEntity> $completion) {
    final String _sql = "SELECT * FROM pages WHERE document_id = ? AND page_number = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, documentId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, pageNumber);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PageEntity>() {
      @Override
      @Nullable
      public PageEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDocumentId = CursorUtil.getColumnIndexOrThrow(_cursor, "document_id");
          final int _cursorIndexOfPageNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "page_number");
          final int _cursorIndexOfThumbnailPath = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnail_path");
          final int _cursorIndexOfPreviewPath = CursorUtil.getColumnIndexOrThrow(_cursor, "preview_path");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extracted_text");
          final int _cursorIndexOfOrderIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "order_index");
          final PageEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpDocumentId;
            _tmpDocumentId = _cursor.getLong(_cursorIndexOfDocumentId);
            final int _tmpPageNumber;
            _tmpPageNumber = _cursor.getInt(_cursorIndexOfPageNumber);
            final String _tmpThumbnailPath;
            if (_cursor.isNull(_cursorIndexOfThumbnailPath)) {
              _tmpThumbnailPath = null;
            } else {
              _tmpThumbnailPath = _cursor.getString(_cursorIndexOfThumbnailPath);
            }
            final String _tmpPreviewPath;
            if (_cursor.isNull(_cursorIndexOfPreviewPath)) {
              _tmpPreviewPath = null;
            } else {
              _tmpPreviewPath = _cursor.getString(_cursorIndexOfPreviewPath);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final int _tmpOrderIndex;
            _tmpOrderIndex = _cursor.getInt(_cursorIndexOfOrderIndex);
            _result = new PageEntity(_tmpId,_tmpDocumentId,_tmpPageNumber,_tmpThumbnailPath,_tmpPreviewPath,_tmpExtractedText,_tmpOrderIndex);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object countPagesForDocument(final long documentId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM pages WHERE document_id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, documentId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
