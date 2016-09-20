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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by RKR on 1/8/2016.
 * ProductModel.
 */
public class OrderModel implements Serializable {

    private String orderId;
    private String docDate;
    private String customerCode;
    private Double amount;
    private Double receivedAmount;
    private String dueDate;
    private String remarks;
    private String userId;

    private Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public OrderModel(String orderId, String docDate, String customerCode, Double amount, Double receivedAmount, String dueDate, String remarks, String userId) {
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

    public String getDocDate() {
        return docDate;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public Double getAmount() {
        return amount;
    }

    private Double getReceivedAmount() {
        return receivedAmount;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getUserId() {
        return userId;
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

    public void deleteRow(String orderId) {
        database.delete(OrdersTable.TABLE_NAME, OrdersTable._ID + " = ?", new String[]{orderId});
        System.out.println("Categories Row deleted with id: " + orderId);
    }

    public void deleteAll() {
        database.delete(OrdersTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
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
        while (!cursor.isAfterLast()) {
            OrderModel obj = cursorToOrderModel(cursor);
            totalAmount += obj.getAmount();
            cursor.moveToNext();
        }
        cursor.close();
        stringMap.put("KEY_TOTAL_AMOUNT", String.format(Locale.getDefault(), "%.2f", totalAmount));
        return stringMap;
    }


    public OrderModel getCustomersRow(String customerCode) {
        Cursor cursor = database.query(OrdersTable.TABLE_NAME, null, OrdersTable.COLUMN_NAME_CUSTOMER_CODE + " = ? ", new String[]{customerCode}, null, null, null);
        OrderModel orderModel = null;
        if (cursor.moveToFirst()) {
            orderModel = cursorToOrderModel(cursor);
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
    private OrderModel cursorToOrderModel(Cursor cursor) {
        return new OrderModel(
                cursor.getString(0),
                cursor.getString(OrdersTable.COLUMN_INDEX_DOC_DATE),
                cursor.getString(OrdersTable.COLUMN_INDEX_CUSTOMER_CODE),
                cursor.getDouble(OrdersTable.COLUMN_INDEX_AMOUNT),
                cursor.getDouble(OrdersTable.COLUMN_INDEX_RECEIVED_AMOUNT),
                cursor.getString(OrdersTable.COLUMN_INDEX_DUE_DATE),
                cursor.getString(OrdersTable.COLUMN_INDEX_REMARKS),
                cursor.getString(OrdersTable.COLUMN_INDEX_USER_ID)
        );
    }

    private JSONObject cursorToJSONObject(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("DocNo", cursor.getString(0));
            jsonObject.put("DocDate", cursor.getString(OrdersTable.COLUMN_INDEX_DOC_DATE));
            jsonObject.put("DocTime", cursor.getString(OrdersTable.COLUMN_INDEX_DOC_DATE));
            jsonObject.put("CustomerCode", cursor.getString(OrdersTable.COLUMN_INDEX_CUSTOMER_CODE));
            jsonObject.put("Amount", cursor.getString(OrdersTable.COLUMN_INDEX_AMOUNT));
            jsonObject.put("Received Amount", cursor.getString(OrdersTable.COLUMN_INDEX_RECEIVED_AMOUNT));
            jsonObject.put("DueDate", cursor.getString(OrdersTable.COLUMN_INDEX_DUE_DATE));
            jsonObject.put("Remarks", cursor.getString(OrdersTable.COLUMN_INDEX_REMARKS));
            jsonObject.put("user_id", cursor.getString(OrdersTable.COLUMN_INDEX_USER_ID));

            OrderItemModel orderItemModelDB = new OrderItemModel(context);
            orderItemModelDB.open();
            JSONArray orderItemsJsonArray = orderItemModelDB.getAllRowsAsJSONArray(cursor.getString(0));
            orderItemModelDB.close();
            jsonObject.put("Items", orderItemsJsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    class OrdersTable implements BaseColumns {
        static final String TABLE_NAME = "orders";
        static final String COLUMN_NAME_DOC_DATE = "doc_date";
        static final String COLUMN_NAME_CUSTOMER_CODE = "customer_code";
        static final String COLUMN_NAME_AMOUNT = "amount";
        static final String COLUMN_NAME_RECEIVED_AMOUNT = "received_amount";
        static final String COLUMN_NAME_DUE_DATE = "due_date";
        static final String COLUMN_NAME_REMARKS = "remarks";
        static final String COLUMN_NAME_USER_ID = "user_id";
        static final int COLUMN_INDEX_DOC_DATE = 1;
        static final int COLUMN_INDEX_CUSTOMER_CODE = 2;
        static final int COLUMN_INDEX_AMOUNT = 3;
        static final int COLUMN_INDEX_RECEIVED_AMOUNT = 4;
        static final int COLUMN_INDEX_DUE_DATE = 5;
        static final int COLUMN_INDEX_REMARKS = 6;
        static final int COLUMN_INDEX_USER_ID = 7;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String UNIQUE = " UNIQUE ";
        static final String SQL_CREATE_USER_DETAILS =
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
