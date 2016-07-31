package rkr.binatestation.eqsoft.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RKR on 31/7/2016.
 * UserDetailsModel.
 */
public class UserDetailsModel implements Serializable {
    String userId;
    String userName;
    String password;
    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public UserDetailsModel(String userId, String userName, String password) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
    }

    public UserDetailsModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(UserDetailsModel obj) {
        if (getRow(obj.getUserId()) == null) {
            ContentValues values = new ContentValues();
            values.put(UserDetailsTable.COLUMN_NAME_USER_ID, obj.getUserId());
            values.put(UserDetailsTable.COLUMN_NAME_USER_NAME, obj.getUserName());
            values.put(UserDetailsTable.COLUMN_NAME_PASSWORD, obj.getPassword());

            long insertId;
            insertId = database.insert(UserDetailsTable.TABLE_NAME, null, values);

            Log.i("InsertID", "Categories : " + insertId);
        } else {
            updateRow(obj);
        }
    }

    public void updateRow(UserDetailsModel obj) {

        ContentValues values = new ContentValues();
        values.put(UserDetailsTable.COLUMN_NAME_USER_ID, obj.getUserId());
        values.put(UserDetailsTable.COLUMN_NAME_USER_NAME, obj.getUserName());
        values.put(UserDetailsTable.COLUMN_NAME_PASSWORD, obj.getPassword());

        database.update(UserDetailsTable.TABLE_NAME, values, UserDetailsTable.COLUMN_NAME_USER_ID + " = ?", new String[]{obj.getUserId()});
        System.out.println("Categories Row Updated with id: " + obj.getUserId());
    }

    public void deleteRow(UserDetailsModel obj) {
        database.delete(UserDetailsTable.TABLE_NAME, UserDetailsTable.COLUMN_NAME_USER_ID + " = ?", new String[]{obj.getUserId()});
        System.out.println("Categories Row deleted with id: " + obj.getUserId());
    }

    public void deleteAll() {
        database.delete(UserDetailsTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<UserDetailsModel> getAllRows() {
        List<UserDetailsModel> list = new ArrayList<>();
        Cursor cursor = database.query(UserDetailsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            UserDetailsModel obj = cursorToUserDetailsModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public UserDetailsModel getRow(String userId) {
        Cursor cursor = database.query(UserDetailsTable.TABLE_NAME, null, UserDetailsTable.COLUMN_NAME_USER_ID + " = ?", new String[]{userId}, null, null, null);
        if (cursor.moveToFirst()) {
            return cursorToUserDetailsModel(cursor);
        }
        cursor.close();
        return null;
    }

    @Contract("_ -> !null")
    private UserDetailsModel cursorToUserDetailsModel(Cursor cursor) {
        return new UserDetailsModel(
                cursor.getString(UserDetailsTable.COLUMN_INDEX_USER_ID),
                cursor.getString(UserDetailsTable.COLUMN_INDEX_USER_NAME),
                cursor.getString(UserDetailsTable.COLUMN_INDEX_PASSWORD)
        );
    }

    protected class UserDetailsTable implements BaseColumns {
        public static final String TABLE_NAME = "user_details";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
        public static final String COLUMN_NAME_PASSWORD = "password";
        public static final int COLUMN_INDEX_USER_ID = 1;
        public static final int COLUMN_INDEX_USER_NAME = 2;
        public static final int COLUMN_INDEX_PASSWORD = 3;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_USER_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_USER_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PASSWORD + TEXT_TYPE +
                        " )";
    }
}
