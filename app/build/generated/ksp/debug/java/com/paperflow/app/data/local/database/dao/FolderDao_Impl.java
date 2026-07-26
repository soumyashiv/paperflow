package com.paperflow.app.data.local.database.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.paperflow.app.data.local.database.entity.FolderEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class FolderDao_Impl implements FolderDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<FolderEntity> __insertionAdapterOfFolderEntity;

  private final EntityDeletionOrUpdateAdapter<FolderEntity> __deletionAdapterOfFolderEntity;

  private final EntityDeletionOrUpdateAdapter<FolderEntity> __updateAdapterOfFolderEntity;

  private final SharedSQLiteStatement __preparedStmtOfSetLocked;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  public FolderDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfFolderEntity = new EntityInsertionAdapter<FolderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `folders` (`id`,`name`,`parent_folder_id`,`is_locked`,`is_hidden`,`color_hex`,`icon_name`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FolderEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getParentFolderId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getParentFolderId());
        }
        final int _tmp = entity.isLocked() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.isHidden() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindString(6, entity.getColorHex());
        statement.bindString(7, entity.getIconName());
        statement.bindLong(8, entity.getCreatedAt());
      }
    };
    this.__deletionAdapterOfFolderEntity = new EntityDeletionOrUpdateAdapter<FolderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `folders` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FolderEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfFolderEntity = new EntityDeletionOrUpdateAdapter<FolderEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `folders` SET `id` = ?,`name` = ?,`parent_folder_id` = ?,`is_locked` = ?,`is_hidden` = ?,`color_hex` = ?,`icon_name` = ?,`created_at` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final FolderEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        if (entity.getParentFolderId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getParentFolderId());
        }
        final int _tmp = entity.isLocked() ? 1 : 0;
        statement.bindLong(4, _tmp);
        final int _tmp_1 = entity.isHidden() ? 1 : 0;
        statement.bindLong(5, _tmp_1);
        statement.bindString(6, entity.getColorHex());
        statement.bindString(7, entity.getIconName());
        statement.bindLong(8, entity.getCreatedAt());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__preparedStmtOfSetLocked = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE folders SET is_locked = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM folders WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertFolder(final FolderEntity folder,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfFolderEntity.insertAndReturnId(folder);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteFolder(final FolderEntity folder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfFolderEntity.handle(folder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateFolder(final FolderEntity folder,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfFolderEntity.handle(folder);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object setLocked(final long id, final boolean locked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetLocked.acquire();
        int _argIndex = 1;
        final int _tmp = locked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfSetLocked.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
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
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<FolderEntity>> getRootFolders() {
    final String _sql = "SELECT `folders`.`id` AS `id`, `folders`.`name` AS `name`, `folders`.`parent_folder_id` AS `parent_folder_id`, `folders`.`is_locked` AS `is_locked`, `folders`.`is_hidden` AS `is_hidden`, `folders`.`color_hex` AS `color_hex`, `folders`.`icon_name` AS `icon_name`, `folders`.`created_at` AS `created_at` FROM folders WHERE parent_folder_id IS NULL AND is_hidden = 0 ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"folders"}, new Callable<List<FolderEntity>>() {
      @Override
      @NonNull
      public List<FolderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfName = 1;
          final int _cursorIndexOfParentFolderId = 2;
          final int _cursorIndexOfIsLocked = 3;
          final int _cursorIndexOfIsHidden = 4;
          final int _cursorIndexOfColorHex = 5;
          final int _cursorIndexOfIconName = 6;
          final int _cursorIndexOfCreatedAt = 7;
          final List<FolderEntity> _result = new ArrayList<FolderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FolderEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Long _tmpParentFolderId;
            if (_cursor.isNull(_cursorIndexOfParentFolderId)) {
              _tmpParentFolderId = null;
            } else {
              _tmpParentFolderId = _cursor.getLong(_cursorIndexOfParentFolderId);
            }
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            final boolean _tmpIsHidden;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_1 != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new FolderEntity(_tmpId,_tmpName,_tmpParentFolderId,_tmpIsLocked,_tmpIsHidden,_tmpColorHex,_tmpIconName,_tmpCreatedAt);
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
  public Flow<List<FolderEntity>> getChildFolders(final long parentId) {
    final String _sql = "SELECT * FROM folders WHERE parent_folder_id = ? AND is_hidden = 0 ORDER BY created_at ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, parentId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"folders"}, new Callable<List<FolderEntity>>() {
      @Override
      @NonNull
      public List<FolderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfParentFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_folder_id");
          final int _cursorIndexOfIsLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_locked");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "is_hidden");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<FolderEntity> _result = new ArrayList<FolderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FolderEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Long _tmpParentFolderId;
            if (_cursor.isNull(_cursorIndexOfParentFolderId)) {
              _tmpParentFolderId = null;
            } else {
              _tmpParentFolderId = _cursor.getLong(_cursorIndexOfParentFolderId);
            }
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            final boolean _tmpIsHidden;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_1 != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new FolderEntity(_tmpId,_tmpName,_tmpParentFolderId,_tmpIsLocked,_tmpIsHidden,_tmpColorHex,_tmpIconName,_tmpCreatedAt);
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
  public Object getFolderById(final long id, final Continuation<? super FolderEntity> $completion) {
    final String _sql = "SELECT * FROM folders WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<FolderEntity>() {
      @Override
      @Nullable
      public FolderEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfParentFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_folder_id");
          final int _cursorIndexOfIsLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_locked");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "is_hidden");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final FolderEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Long _tmpParentFolderId;
            if (_cursor.isNull(_cursorIndexOfParentFolderId)) {
              _tmpParentFolderId = null;
            } else {
              _tmpParentFolderId = _cursor.getLong(_cursorIndexOfParentFolderId);
            }
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            final boolean _tmpIsHidden;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_1 != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new FolderEntity(_tmpId,_tmpName,_tmpParentFolderId,_tmpIsLocked,_tmpIsHidden,_tmpColorHex,_tmpIconName,_tmpCreatedAt);
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
  public Object searchFolders(final String query,
      final Continuation<? super List<FolderEntity>> $completion) {
    final String _sql = "SELECT * FROM folders WHERE name LIKE '%' || ? || '%' AND is_hidden = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<FolderEntity>>() {
      @Override
      @NonNull
      public List<FolderEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfParentFolderId = CursorUtil.getColumnIndexOrThrow(_cursor, "parent_folder_id");
          final int _cursorIndexOfIsLocked = CursorUtil.getColumnIndexOrThrow(_cursor, "is_locked");
          final int _cursorIndexOfIsHidden = CursorUtil.getColumnIndexOrThrow(_cursor, "is_hidden");
          final int _cursorIndexOfColorHex = CursorUtil.getColumnIndexOrThrow(_cursor, "color_hex");
          final int _cursorIndexOfIconName = CursorUtil.getColumnIndexOrThrow(_cursor, "icon_name");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "created_at");
          final List<FolderEntity> _result = new ArrayList<FolderEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final FolderEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Long _tmpParentFolderId;
            if (_cursor.isNull(_cursorIndexOfParentFolderId)) {
              _tmpParentFolderId = null;
            } else {
              _tmpParentFolderId = _cursor.getLong(_cursorIndexOfParentFolderId);
            }
            final boolean _tmpIsLocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLocked);
            _tmpIsLocked = _tmp != 0;
            final boolean _tmpIsHidden;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsHidden);
            _tmpIsHidden = _tmp_1 != 0;
            final String _tmpColorHex;
            _tmpColorHex = _cursor.getString(_cursorIndexOfColorHex);
            final String _tmpIconName;
            _tmpIconName = _cursor.getString(_cursorIndexOfIconName);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new FolderEntity(_tmpId,_tmpName,_tmpParentFolderId,_tmpIsLocked,_tmpIsHidden,_tmpColorHex,_tmpIconName,_tmpCreatedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
