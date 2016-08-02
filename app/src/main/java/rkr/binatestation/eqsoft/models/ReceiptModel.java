package rkr.binatestation.eqsoft.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.google.common.collect.Lists;

import org.jetbrains.annotations.Contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by RKR on 1/8/2016.
 * ProductModel.
 */
public class ReceiptModel implements Serializable {

    Long receiptId;
    String receiptDateTime;
    String customerCode;
    String amount;

    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public ReceiptModel(Long receiptId, String receiptDateTime, String customerCode, String amount) {
        this.receiptId = receiptId;
        this.receiptDateTime = receiptDateTime;
        this.customerCode = customerCode;
        this.amount = amount;
    }

    public ReceiptModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public String getReceiptDateTime() {
        return receiptDateTime;
    }

    public void setReceiptDateTime(String receiptDateTime) {
        this.receiptDateTime = receiptDateTime;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(ReceiptModel obj) {
        ContentValues values = new ContentValues();
        values.put(ReceiptsTable.COLUMN_NAME_RECEIPT_ID, obj.getReceiptId());
        values.put(ReceiptsTable.COLUMN_NAME_RECEIPT_DATE, obj.getReceiptDateTime());
        values.put(ReceiptsTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
        values.put(ReceiptsTable.COLUMN_NAME_AMOUNT, obj.getAmount());

        long insertId;
        insertId = database.insert(ReceiptsTable.TABLE_NAME, null, values);

        Log.i("InsertID", "Categories : " + insertId);
    }

    /**
     * This method will do a compound insert in to the table
     * It will splits the list in to lists of 500 objects and generate insert query for all 500 objects in each
     *
     * @param productModelList the list of table rows to insert.
     */
    public void insertMultipleRows(List<ReceiptModel> productModelList) {
        for (List<ReceiptModel> masterList : Lists.partition(productModelList, 500)) {
            String query = "REPLACE INTO '" +
                    ReceiptsTable.TABLE_NAME + "' ('" +
                    ReceiptsTable.COLUMN_NAME_RECEIPT_ID + "','" +
                    ReceiptsTable.COLUMN_NAME_RECEIPT_DATE + "','" +
                    ReceiptsTable.COLUMN_NAME_CUSTOMER_CODE + "','" +
                    ReceiptsTable.COLUMN_NAME_AMOUNT + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                ReceiptModel master = masterList.get(i);
                query += master.getReceiptId() + "' , '" +
                        master.getReceiptDateTime() + "' , '" +
                        master.getCustomerCode() + "' , '" +
                        master.getAmount() + "')";
                if (i != (masterList.size() - 1)) {
                    query += ",('";
                }
            }
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Query executed", cursor.getCount() + " : " + query);
            cursor.close();
        }
    }

    public void updateRow(ReceiptModel obj, String id) {

        ContentValues values = new ContentValues();
        values.put(ReceiptsTable.COLUMN_NAME_RECEIPT_ID, obj.getReceiptId());
        values.put(ReceiptsTable.COLUMN_NAME_RECEIPT_DATE, obj.getReceiptDateTime());
        values.put(ReceiptsTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
        values.put(ReceiptsTable.COLUMN_NAME_AMOUNT, obj.getAmount());

        database.update(ReceiptsTable.TABLE_NAME, values, ReceiptsTable._ID + " = ?", new String[]{id});
        System.out.println("Categories Row Updated with id: " + id);
    }

    public void deleteRow(String receiptId) {
        database.delete(ReceiptsTable.TABLE_NAME, ReceiptsTable.COLUMN_NAME_RECEIPT_ID + " = ?", new String[]{receiptId});
        System.out.println("Categories Row deleted with id: " + receiptId);
    }

    public void deleteAll() {
        database.delete(ReceiptsTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<ReceiptModel> getAllRows() {
        List<ReceiptModel> list = new ArrayList<>();
        Cursor cursor = database.query(ReceiptsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ReceiptModel obj = cursorToProductModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public ReceiptModel getRow(String receiptId) {
        Cursor cursor = database.query(ReceiptsTable.TABLE_NAME, null, ReceiptsTable.COLUMN_NAME_RECEIPT_ID + " = ?", new String[]{receiptId}, null, null, null);
        ReceiptModel productModel = null;
        if (cursor.moveToFirst()) {
            productModel = cursorToProductModel(cursor);
        }
        cursor.close();
        return productModel;
    }

    @Contract("_ -> !null")
    private ReceiptModel cursorToProductModel(Cursor cursor) {
        return new ReceiptModel(
                cursor.getLong(ReceiptsTable.COLUMN_INDEX_RECEIPT_ID),
                cursor.getString(ReceiptsTable.COLUMN_INDEX_RECEIPT_DATE),
                cursor.getString(ReceiptsTable.COLUMN_INDEX_CUSTOMER_CODE),
                cursor.getString(ReceiptsTable.COLUMN_INDEX_AMOUNT)
        );
    }

    protected class ReceiptsTable implements BaseColumns {
        public static final String TABLE_NAME = "receipts";
        public static final String COLUMN_NAME_RECEIPT_ID = "receipt_id";
        public static final String COLUMN_NAME_RECEIPT_DATE = "receipt_date";
        public static final String COLUMN_NAME_CUSTOMER_CODE = "customer_code";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final int COLUMN_INDEX_RECEIPT_ID = 1;
        public static final int COLUMN_INDEX_RECEIPT_DATE = 2;
        public static final int COLUMN_INDEX_CUSTOMER_CODE = 3;
        public static final int COLUMN_INDEX_AMOUNT = 4;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_RECEIPT_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_RECEIPT_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CUSTOMER_CODE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_AMOUNT + TEXT_TYPE +
                        " )";
    }

}
