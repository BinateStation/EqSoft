package rkr.binatestation.eqsoft.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by RKR on 1/8/2016.
 * ProductModel.
 */
public class ReceiptModel implements Serializable {

    private String receiptId;
    private String receiptDateTime;
    private String customerCode;
    private Double amount;
    private String userId;

    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public ReceiptModel(String receiptId, String receiptDateTime, String customerCode, Double amount, String userId) {
        this.receiptId = receiptId;
        this.receiptDateTime = receiptDateTime;
        this.customerCode = customerCode;
        this.amount = amount;
        this.userId = userId;
    }

    public ReceiptModel(Context context) {
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public String getReceiptId() {
        return receiptId;
    }

    private String getReceiptDateTime() {
        return receiptDateTime;
    }

    private String getCustomerCode() {
        return customerCode;
    }

    public Double getAmount() {
        return amount;
    }

    private String getUserId() {
        return userId;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(ReceiptModel obj) {
        if (getRow(obj.getCustomerCode()) == null) {
            if (obj.getAmount() > 0) {
                ContentValues values = new ContentValues();
                values.put(ReceiptsTable.COLUMN_NAME_RECEIPT_DATE, obj.getReceiptDateTime());
                values.put(ReceiptsTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
                values.put(ReceiptsTable.COLUMN_NAME_AMOUNT, obj.getAmount());
                values.put(ReceiptsTable.COLUMN_NAME_USER_ID, obj.getUserId());

                long insertId;
                insertId = database.insert(ReceiptsTable.TABLE_NAME, null, values);

                Log.i("InsertID", "Categories : " + insertId);
            }
        } else {
            updateRow(obj);
        }
    }

    private void updateRow(ReceiptModel obj) {

        ContentValues values = new ContentValues();
        values.put(ReceiptsTable.COLUMN_NAME_RECEIPT_DATE, obj.getReceiptDateTime());
        values.put(ReceiptsTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
        values.put(ReceiptsTable.COLUMN_NAME_AMOUNT, obj.getAmount());
        values.put(ReceiptsTable.COLUMN_NAME_USER_ID, obj.getUserId());

        database.update(ReceiptsTable.TABLE_NAME, values, ReceiptsTable.COLUMN_NAME_CUSTOMER_CODE + " = ?", new String[]{obj.getCustomerCode()});
        System.out.println("Categories Row Updated with id: " + obj.getCustomerCode());
    }

    public void deleteRow(String receiptId) {
        database.delete(ReceiptsTable.TABLE_NAME, ReceiptsTable._ID + " = ?", new String[]{receiptId});
        System.out.println("Categories Row deleted with id: " + receiptId);
    }

    public void deleteAll() {
        database.delete(ReceiptsTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public Double getTotalAmountReceived() {
        Double totalAmountReceived = 0.0;
        Cursor cursor = database.query(ReceiptsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReceiptModel obj = cursorToProductModel(cursor);
            totalAmountReceived += obj.getAmount();
            cursor.moveToNext();
        }
        cursor.close();
        return totalAmountReceived;
    }

    public JSONArray getAllRowsAsJSONArray() {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = database.query(ReceiptsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            JSONObject obj = cursorToJSONObject(cursor);
            jsonArray.put(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return jsonArray;
    }


    public ReceiptModel getRow(String customerCode) {
        Cursor cursor = database.query(ReceiptsTable.TABLE_NAME, null, ReceiptsTable.COLUMN_NAME_CUSTOMER_CODE + " = ?", new String[]{customerCode}, null, null, null);
        ReceiptModel productModel = null;
        if (cursor.moveToFirst()) {
            productModel = cursorToProductModel(cursor);
        }
        cursor.close();
        return productModel;
    }

    /**
     * This method will counts how many rows available in this table
     *
     * @return the count of the OrdersTable#TABLE_NAME
     */
    public String getCount() {
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + ReceiptsTable.TABLE_NAME, null);
        Integer count = 0;
        if (cursor.moveToFirst())
            count += cursor.getInt(0);
        cursor.close();
        return "" + count;
    }

    @Contract("_ -> !null")
    private ReceiptModel cursorToProductModel(Cursor cursor) {
        return new ReceiptModel(
                cursor.getString(0),
                cursor.getString(ReceiptsTable.COLUMN_INDEX_RECEIPT_DATE),
                cursor.getString(ReceiptsTable.COLUMN_INDEX_CUSTOMER_CODE),
                cursor.getDouble(ReceiptsTable.COLUMN_INDEX_AMOUNT),
                cursor.getString(ReceiptsTable.COLUMN_INDEX_USER_ID)
        );
    }

    private JSONObject cursorToJSONObject(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DocNo", cursor.getString(0));
            jsonObject.put("DocDate", cursor.getString(ReceiptsTable.COLUMN_INDEX_RECEIPT_DATE));
            jsonObject.put("DocTime", cursor.getString(ReceiptsTable.COLUMN_INDEX_RECEIPT_DATE));
            jsonObject.put("CustomerCode", cursor.getString(ReceiptsTable.COLUMN_INDEX_CUSTOMER_CODE));
            jsonObject.put("Amount", cursor.getString(ReceiptsTable.COLUMN_INDEX_AMOUNT));
            jsonObject.put("user_id", cursor.getString(ReceiptsTable.COLUMN_INDEX_USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    class ReceiptsTable implements BaseColumns {
        static final String TABLE_NAME = "receipts";
        static final String COLUMN_NAME_RECEIPT_DATE = "receipt_date";
        static final String COLUMN_NAME_CUSTOMER_CODE = "customer_code";
        static final String COLUMN_NAME_AMOUNT = "amount";
        static final String COLUMN_NAME_USER_ID = "user_id";
        static final int COLUMN_INDEX_RECEIPT_DATE = 1;
        static final int COLUMN_INDEX_CUSTOMER_CODE = 2;
        static final int COLUMN_INDEX_AMOUNT = 3;
        static final int COLUMN_INDEX_USER_ID = 4;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String UNIQUE = " UNIQUE ";
        static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_RECEIPT_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CUSTOMER_CODE + TEXT_TYPE + UNIQUE + COMMA_SEP +
                        COLUMN_NAME_AMOUNT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_USER_ID + TEXT_TYPE +
                        " )";
    }

}
