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
public class OrderModel implements Serializable {

    Long orderId;
    String docDate;
    String customerCode;
    String amount;
    String receivedAmount;
    String dueDate;
    String remarks;

    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public OrderModel(Long orderId, String docDate, String customerCode, String amount, String receivedAmount, String dueDate, String remarks) {
        this.orderId = orderId;
        this.docDate = docDate;
        this.customerCode = customerCode;
        this.amount = amount;
        this.receivedAmount = receivedAmount;
        this.dueDate = dueDate;
        this.remarks = remarks;
    }

    public OrderModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
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

    public String getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(String receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(OrderModel obj) {
        ContentValues values = new ContentValues();
        values.put(OrdersTable.COLUMN_NAME_DOC_DATE, obj.getDocDate());
        values.put(OrdersTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
        values.put(OrdersTable.COLUMN_NAME_AMOUNT, obj.getAmount());
        values.put(OrdersTable.COLUMN_NAME_RECEIVED_AMOUNT, obj.getReceivedAmount());
        values.put(OrdersTable.COLUMN_NAME_DUE_DATE, obj.getDueDate());
        values.put(OrdersTable.COLUMN_NAME_REMARKS, obj.getRemarks());

        long insertId;
        insertId = database.insert(OrdersTable.TABLE_NAME, null, values);

        Log.i("InsertID", "Categories : " + insertId);
    }

    /**
     * This method will do a compound insert in to the table
     * It will splits the list in to lists of 500 objects and generate insert query for all 500 objects in each
     *
     * @param productModelList the list of table rows to insert.
     */
    public void insertMultipleRows(List<OrderModel> productModelList) {
        for (List<OrderModel> masterList : Lists.partition(productModelList, 500)) {
            String query = "REPLACE INTO '" +
                    OrdersTable.TABLE_NAME + "' ('" +
                    OrdersTable.COLUMN_NAME_DOC_DATE + "','" +
                    OrdersTable.COLUMN_NAME_CUSTOMER_CODE + "','" +
                    OrdersTable.COLUMN_NAME_AMOUNT + "','" +
                    OrdersTable.COLUMN_NAME_RECEIVED_AMOUNT + "','" +
                    OrdersTable.COLUMN_NAME_DUE_DATE + "','" +
                    OrdersTable.COLUMN_NAME_REMARKS + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                OrderModel master = masterList.get(i);
                query += master.getDocDate() + "' , '" +
                        master.getCustomerCode() + "' , '" +
                        master.getAmount() + "' , '" +
                        master.getReceivedAmount() + "' , '" +
                        master.getDueDate() + "' , '" +
                        master.getRemarks() + "')";
                if (i != (masterList.size() - 1)) {
                    query += ",('";
                }
            }
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Query executed", cursor.getCount() + " : " + query);
            cursor.close();
        }
    }

    public void updateRow(OrderModel obj, String id) {

        ContentValues values = new ContentValues();
        values.put(OrdersTable.COLUMN_NAME_DOC_DATE, obj.getDocDate());
        values.put(OrdersTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
        values.put(OrdersTable.COLUMN_NAME_AMOUNT, obj.getAmount());
        values.put(OrdersTable.COLUMN_NAME_RECEIVED_AMOUNT, obj.getReceivedAmount());
        values.put(OrdersTable.COLUMN_NAME_DUE_DATE, obj.getDueDate());
        values.put(OrdersTable.COLUMN_NAME_REMARKS, obj.getRemarks());

        database.update(OrdersTable.TABLE_NAME, values, OrdersTable._ID + " = ?", new String[]{id});
        System.out.println("Categories Row Updated with id: " + id);
    }

    public void deleteRow(String id) {
        database.delete(OrdersTable.TABLE_NAME, OrdersTable._ID + " = ?", new String[]{id});
        System.out.println("Categories Row deleted with id: " + id);
    }

    public void deleteAll() {
        database.delete(OrdersTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<OrderModel> getAllRows() {
        List<OrderModel> list = new ArrayList<>();
        Cursor cursor = database.query(OrdersTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderModel obj = cursorToProductModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public OrderModel getRow(String id) {
        Cursor cursor = database.query(OrdersTable.TABLE_NAME, null, OrdersTable._ID + " = ?", new String[]{id}, null, null, null);
        OrderModel productModel = null;
        if (cursor.moveToFirst()) {
            productModel = cursorToProductModel(cursor);
        }
        cursor.close();
        return productModel;
    }

    @Contract("_ -> !null")
    private OrderModel cursorToProductModel(Cursor cursor) {
        return new OrderModel(
                cursor.getLong(0),
                cursor.getString(OrdersTable.COLUMN_INDEX_DOC_DATE),
                cursor.getString(OrdersTable.COLUMN_INDEX_CUSTOMER_CODE),
                cursor.getString(OrdersTable.COLUMN_INDEX_AMOUNT),
                cursor.getString(OrdersTable.COLUMN_INDEX_RECEIVED_AMOUNT),
                cursor.getString(OrdersTable.COLUMN_INDEX_DUE_DATE),
                cursor.getString(OrdersTable.COLUMN_INDEX_REMARKS)
        );
    }

    protected class OrdersTable implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_NAME_DOC_DATE = "doc_date";
        public static final String COLUMN_NAME_CUSTOMER_CODE = "customer_code";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_RECEIVED_AMOUNT = "received_amount";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_REMARKS = "remarks";
        public static final int COLUMN_INDEX_DOC_DATE = 1;
        public static final int COLUMN_INDEX_CUSTOMER_CODE = 2;
        public static final int COLUMN_INDEX_AMOUNT = 3;
        public static final int COLUMN_INDEX_RECEIVED_AMOUNT = 4;
        public static final int COLUMN_INDEX_DUE_DATE = 5;
        public static final int COLUMN_INDEX_REMARKS = 6;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_DOC_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CUSTOMER_CODE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_AMOUNT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_RECEIVED_AMOUNT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_DUE_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_REMARKS + TEXT_TYPE +
                        " )";
    }

}
