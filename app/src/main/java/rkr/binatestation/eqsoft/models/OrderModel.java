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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RKR on 1/8/2016.
 * ProductModel.
 */
public class OrderModel implements Serializable {

    String orderId;
    String docDate;
    String customerCode;
    String amount;
    String receivedAmount;
    String dueDate;
    String remarks;
    String userId;

    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public OrderModel(String orderId, String docDate, String customerCode, String amount, String receivedAmount, String dueDate, String remarks, String userId) {
        this.orderId = orderId;
        this.docDate = docDate;
        this.customerCode = customerCode;
        this.amount = amount;
        this.receivedAmount = receivedAmount;
        this.dueDate = dueDate;
        this.remarks = remarks;
        this.userId = userId;
    }

    public OrderModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Long insert(OrderModel obj) {
        ContentValues values = new ContentValues();
        values.put(OrdersTable.COLUMN_NAME_DOC_DATE, obj.getDocDate());
        values.put(OrdersTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
        values.put(OrdersTable.COLUMN_NAME_AMOUNT, obj.getAmount());
        values.put(OrdersTable.COLUMN_NAME_RECEIVED_AMOUNT, obj.getReceivedAmount());
        values.put(OrdersTable.COLUMN_NAME_DUE_DATE, obj.getDueDate());
        values.put(OrdersTable.COLUMN_NAME_REMARKS, obj.getRemarks());
        values.put(OrdersTable.COLUMN_NAME_USER_ID, obj.getUserId());

        long insertId;
        insertId = database.insert(OrdersTable.TABLE_NAME, null, values);

        Log.i("InsertID", "Categories : " + insertId);
        return insertId;
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
                    OrdersTable.COLUMN_NAME_REMARKS + "','" +
                    OrdersTable.COLUMN_NAME_USER_ID + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                OrderModel master = masterList.get(i);
                query += master.getDocDate() + "' , '" +
                        master.getCustomerCode() + "' , '" +
                        master.getAmount() + "' , '" +
                        master.getReceivedAmount() + "' , '" +
                        master.getDueDate() + "' , '" +
                        master.getRemarks() + "' , '" +
                        master.getUserId() + "')";
                if (i != (masterList.size() - 1)) {
                    query += ",('";
                }
            }
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Query executed", cursor.getCount() + " : " + query);
            cursor.close();
        }
    }

    public void updateRow(OrderModel obj) {

        ContentValues values = new ContentValues();
        values.put(OrdersTable.COLUMN_NAME_DOC_DATE, obj.getDocDate());
        values.put(OrdersTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());
        values.put(OrdersTable.COLUMN_NAME_AMOUNT, obj.getAmount());
        values.put(OrdersTable.COLUMN_NAME_RECEIVED_AMOUNT, obj.getReceivedAmount());
        values.put(OrdersTable.COLUMN_NAME_DUE_DATE, obj.getDueDate());
        values.put(OrdersTable.COLUMN_NAME_REMARKS, obj.getRemarks());
        values.put(OrdersTable.COLUMN_NAME_USER_ID, obj.getUserId());

        database.update(OrdersTable.TABLE_NAME, values, OrdersTable._ID + " = ?", new String[]{"" + obj.getOrderId()});
        System.out.println("Categories Row Updated with id: " + obj.getOrderId());
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

    public JSONArray getAllRowsAsJSONArray() {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = database.query(OrdersTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            JSONObject obj = cursorToJSONObject(cursor);
            jsonArray.put(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return jsonArray;
    }

    public Map<String, String> getTotalAmountReceivedAndPendingAmount() {
        Map<String, String> stringMap = new LinkedHashMap<>();
        Cursor cursor = database.query(OrdersTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        Double totalAmount = 0.0;
        Double totalAmountReceived = 0.0;
        while (!cursor.isAfterLast()) {
            OrderModel obj = cursorToProductModel(cursor);
            totalAmount += Double.parseDouble(obj.getAmount());
            totalAmountReceived += Double.parseDouble(obj.getReceivedAmount());
            cursor.moveToNext();
        }
        cursor.close();
        stringMap.put("KEY_TOTAL_AMOUNT_RECEIVED", "" + totalAmountReceived);
        stringMap.put("KEY_TOTAL_PENDING_AMOUNT", "" + (totalAmount - totalAmountReceived));
        return stringMap;
    }


    public OrderModel getRow(String id) {
        Cursor cursor = database.query(OrdersTable.TABLE_NAME, null, OrdersTable._ID + " = ?", new String[]{id}, null, null, null);
        OrderModel orderModel = null;
        if (cursor.moveToFirst()) {
            orderModel = cursorToProductModel(cursor);
        }
        cursor.close();
        return orderModel;
    }

    public OrderModel getCustomersRow(String customerCode) {
        Cursor cursor = database.query(OrdersTable.TABLE_NAME, null, OrdersTable.COLUMN_NAME_CUSTOMER_CODE + " = ?", new String[]{customerCode}, null, null, null);
        OrderModel orderModel = null;
        if (cursor.moveToFirst()) {
            orderModel = cursorToProductModel(cursor);
        }
        cursor.close();
        return orderModel;
    }

    /**
     * This method will counts how many rows available in this table
     *
     * @return the count of the OrdersTable#TABLE_NAME
     */
    public String getCount() {
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + OrdersTable.TABLE_NAME, null);
        Integer count = 0;
        if (cursor.moveToFirst())
            count += cursor.getInt(0);
        cursor.close();
        return "" + count;
    }

    @Contract("_ -> !null")
    private OrderModel cursorToProductModel(Cursor cursor) {
        return new OrderModel(
                cursor.getString(0),
                cursor.getString(OrdersTable.COLUMN_INDEX_DOC_DATE),
                cursor.getString(OrdersTable.COLUMN_INDEX_CUSTOMER_CODE),
                cursor.getString(OrdersTable.COLUMN_INDEX_AMOUNT),
                cursor.getString(OrdersTable.COLUMN_INDEX_RECEIVED_AMOUNT),
                cursor.getString(OrdersTable.COLUMN_INDEX_DUE_DATE),
                cursor.getString(OrdersTable.COLUMN_INDEX_REMARKS),
                cursor.getString(OrdersTable.COLUMN_INDEX_USER_ID)
        );
    }

    private JSONObject cursorToJSONObject(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("order_id", cursor.getString(0));
            jsonObject.put("doc_date", cursor.getString(OrdersTable.COLUMN_INDEX_DOC_DATE));
            jsonObject.put("customer_code", cursor.getString(OrdersTable.COLUMN_INDEX_CUSTOMER_CODE));
            jsonObject.put("amount", cursor.getString(OrdersTable.COLUMN_INDEX_AMOUNT));
            jsonObject.put("received_amount", cursor.getString(OrdersTable.COLUMN_INDEX_RECEIVED_AMOUNT));
            jsonObject.put("due_date", cursor.getString(OrdersTable.COLUMN_INDEX_DUE_DATE));
            jsonObject.put("remarks", cursor.getString(OrdersTable.COLUMN_INDEX_REMARKS));
            jsonObject.put("user_id", cursor.getString(OrdersTable.COLUMN_INDEX_USER_ID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected class OrdersTable implements BaseColumns {
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_NAME_DOC_DATE = "doc_date";
        public static final String COLUMN_NAME_CUSTOMER_CODE = "customer_code";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_RECEIVED_AMOUNT = "received_amount";
        public static final String COLUMN_NAME_DUE_DATE = "due_date";
        public static final String COLUMN_NAME_REMARKS = "remarks";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final int COLUMN_INDEX_DOC_DATE = 1;
        public static final int COLUMN_INDEX_CUSTOMER_CODE = 2;
        public static final int COLUMN_INDEX_AMOUNT = 3;
        public static final int COLUMN_INDEX_RECEIVED_AMOUNT = 4;
        public static final int COLUMN_INDEX_DUE_DATE = 5;
        public static final int COLUMN_INDEX_REMARKS = 6;
        public static final int COLUMN_INDEX_USER_ID = 7;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String UNIQUE = " UNIQUE ";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_DOC_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CUSTOMER_CODE + TEXT_TYPE + UNIQUE + COMMA_SEP +
                        COLUMN_NAME_AMOUNT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_RECEIVED_AMOUNT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_DUE_DATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_REMARKS + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_USER_ID + TEXT_TYPE +
                        " )";
    }

}
