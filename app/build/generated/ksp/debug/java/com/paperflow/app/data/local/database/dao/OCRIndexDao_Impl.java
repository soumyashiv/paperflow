package com.paperflow.app.data.local.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.paperflow.app.data.local.database.entity.OCRIndexEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class OCRIndexDao_Impl implements OCRIndexDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<OCRIndexEntity> __insertionAdapterOfOCRIndexEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForPage;

  private final SharedSQLiteStatement __preparedStmtOfDeleteForDocument;

  public OCRIndexDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfOCRIndexEntity = new EntityInsertionAdapter<OCRIndexEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ocr_index` (`rowid`,`document_id`,`extracted_text`) VALUES (nullif(?, 0),?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final OCRIndexEntity entity) {
        statement.bindLong(1, entity.getPageId());
        statement.bindLong(2, entity.getDocumentId());
        statement.bindString(3, entity.getExtractedText());
      }
    };
    this.__preparedStmtOfDeleteForPage = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ocr_index WHERE rowid = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteForDocument = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM ocr_index WHERE document_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object index(final OCRIndexEntity entry, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfOCRIndexEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForPage(final long pageId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForPage.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDeleteForPage.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteForDocument(final long documentId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteForDocument.acquire();
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
          __preparedStmtOfDeleteForDocument.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object search(final String query,
      final Continuation<? super List<OCRIndexEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT rowid, document_id, extracted_text\n"
            + "        FROM ocr_index\n"
            + "        WHERE ocr_index MATCH ?\n"
            + "        LIMIT 100\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OCRIndexEntity>>() {
      @Override
      @NonNull
      public List<OCRIndexEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPageId = 0;
          final int _cursorIndexOfDocumentId = 1;
          final int _cursorIndexOfExtractedText = 2;
          final List<OCRIndexEntity> _result = new ArrayList<OCRIndexEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OCRIndexEntity _item;
            final long _tmpPageId;
            _tmpPageId = _cursor.getLong(_cursorIndexOfPageId);
            final long _tmpDocumentId;
            _tmpDocumentId = _cursor.getLong(_cursorIndexOfDocumentId);
            final String _tmpExtractedText;
            _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            _item = new OCRIndexEntity(_tmpPageId,_tmpDocumentId,_tmpExtractedText);
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
  public Object searchVisible(final String query,
      final Continuation<? super List<OCRIndexEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT oi.rowid, oi.document_id, oi.extracted_text\n"
            + "        FROM ocr_index oi\n"
            + "        INNER JOIN documents d ON oi.document_id = d.id\n"
            + "        WHERE ocr_index MATCH ?\n"
            + "          AND d.is_hidden = 0\n"
            + "          AND d.is_locked = 0\n"
            + "        LIMIT 50\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<OCRIndexEntity>>() {
      @Override
      @NonNull
      public List<OCRIndexEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPageId = 0;
          final int _cursorIndexOfDocumentId = 1;
          final int _cursorIndexOfExtractedText = 2;
          final List<OCRIndexEntity> _result = new ArrayList<OCRIndexEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final OCRIndexEntity _item;
            final long _tmpPageId;
            _tmpPageId = _cursor.getLong(_cursorIndexOfPageId);
            final long _tmpDocumentId;
            _tmpDocumentId = _cursor.getLong(_cursorIndexOfDocumentId);
            final String _tmpExtractedText;
            _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            _item = new OCRIndexEntity(_tmpPageId,_tmpDocumentId,_tmpExtractedText);
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
  public Object countIndexedPages(final long documentId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM ocr_index WHERE document_id = ?";
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
