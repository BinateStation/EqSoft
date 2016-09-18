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
import java.util.List;

/**
 * Created by RKR on 1/8/2016.
 * ProductModel.
 */
public class OrderItemModel implements Serializable {

    String orderId;
    String productCode;
    Double rate;
    Double quantity;
    Double amount;
    String customerCode;

    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public OrderItemModel(String orderId, String productCode, Double rate, Double quantity, Double amount, String customerCode) {
        this.orderId = orderId;
        this.productCode = productCode;
        this.rate = rate;
        this.quantity = quantity;
        this.amount = amount;
        this.customerCode = customerCode;
    }

    public OrderItemModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public static OrderItemModel cursorToOrderItemModelStatic(Cursor cursor) {
        return new OrderItemModel(
                cursor.getString(OrderItemsTable.COLUMN_INDEX_ORDER_ID),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_PRODUCT_CODE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_RATE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_QUANTITY),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_AMOUNT),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_CUSTOMER_CODE)
        );
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(OrderItemModel obj) {
        if (getRow(obj.getProductCode(), obj.getOrderId()) == null) {
            ContentValues values = new ContentValues();
            values.put(OrderItemsTable.COLUMN_NAME_ORDER_ID, obj.getOrderId());
            values.put(OrderItemsTable.COLUMN_NAME_PRODUCT_CODE, obj.getProductCode());
            values.put(OrderItemsTable.COLUMN_NAME_RATE, obj.getRate());
            values.put(OrderItemsTable.COLUMN_NAME_QUANTITY, obj.getQuantity());
            values.put(OrderItemsTable.COLUMN_NAME_AMOUNT, obj.getAmount());
            values.put(OrderItemsTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());

            long insertId;
            insertId = database.insert(OrderItemsTable.TABLE_NAME, null, values);

            Log.i("InsertID", "Categories : " + insertId);
        } else {
            updateRow(obj);
        }
    }

    /**
     * This method will do a compound insert in to the table
     * It will splits the list in to lists of 500 objects and generate insert query for all 500 objects in each
     *
     * @param productModelList the list of table rows to insert.
     */
    public void insertMultipleRows(List<OrderItemModel> productModelList) {
        for (List<OrderItemModel> masterList : Lists.partition(productModelList, 500)) {
            String query = "REPLACE INTO '" +
                    OrderItemsTable.TABLE_NAME + "' ('" +
                    OrderItemsTable.COLUMN_NAME_ORDER_ID + "','" +
                    OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + "','" +
                    OrderItemsTable.COLUMN_NAME_RATE + "','" +
                    OrderItemsTable.COLUMN_NAME_QUANTITY + "','" +
                    OrderItemsTable.COLUMN_NAME_AMOUNT + "','" +
                    OrderItemsTable.COLUMN_NAME_CUSTOMER_CODE + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                OrderItemModel master = masterList.get(i);
                query += master.getOrderId() + "' , '" +
                        master.getProductCode() + "' , '" +
                        master.getRate() + "' , '" +
                        master.getQuantity() + "' , '" +
                        master.getAmount() + "' , '" +
                        master.getCustomerCode() + "')";
                if (i != (masterList.size() - 1)) {
                    query += ",('";
                }
            }
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Query executed", cursor.getCount() + " : " + query);
            cursor.close();
        }
    }

    public void updateRow(OrderItemModel obj) {

        ContentValues values = new ContentValues();
        values.put(OrderItemsTable.COLUMN_NAME_ORDER_ID, obj.getOrderId());
        values.put(OrderItemsTable.COLUMN_NAME_PRODUCT_CODE, obj.getProductCode());
        values.put(OrderItemsTable.COLUMN_NAME_RATE, obj.getRate());
        values.put(OrderItemsTable.COLUMN_NAME_QUANTITY, obj.getQuantity());
        values.put(OrderItemsTable.COLUMN_NAME_AMOUNT, obj.getAmount());
        values.put(OrderItemsTable.COLUMN_NAME_CUSTOMER_CODE, obj.getCustomerCode());

        database.update(OrderItemsTable.TABLE_NAME, values,
                OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ? and " +
                        OrderItemsTable.COLUMN_NAME_ORDER_ID + " = ? ", new String[]{obj.getProductCode(), "" + obj.getOrderId()});
        System.out.println("Categories Row Updated with id: " + obj.getProductCode());
    }

    public void deleteRow(String productCode, String customerCode) {
        database.delete(OrderItemsTable.TABLE_NAME, OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ? AND " +
                OrderItemsTable.COLUMN_NAME_CUSTOMER_CODE + " = ? ", new String[]{productCode, customerCode});
        System.out.println("Categories Row deleted with id: " + productCode + " , " + customerCode);
    }

    public void deleteAll(String orderId) {
        database.delete(OrderItemsTable.TABLE_NAME, OrderItemsTable.COLUMN_NAME_ORDER_ID + " = ?", new String[]{orderId});
        System.out.println("Categories table Deleted ALL with Order ID : " + orderId);
    }

    public void deleteAll() {
        database.delete(OrderItemsTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<OrderItemModel> getAllRows() {
        List<OrderItemModel> list = new ArrayList<>();
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderItemModel obj = cursorToOrderItemModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public JSONArray getAllRowsAsJSONArray(String orderId) {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, OrderItemsTable.COLUMN_NAME_ORDER_ID + " = ?", new String[]{orderId}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            JSONObject obj = cursorToJSONObject(cursor);
            jsonArray.put(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return jsonArray;
    }

    public List<OrderItemModelTemp> getAllRows(String orderId) {
        List<OrderItemModelTemp> list = new ArrayList<>();
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, OrderItemsTable.COLUMN_NAME_ORDER_ID + " = ?", new String[]{orderId}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderItemModel obj = cursorToOrderItemModel(cursor);
            OrderItemModelTemp temp = new OrderItemModelTemp(
                    obj.getProductCode(),
                    obj.getRate(),
                    obj.getQuantity(),
                    obj.getAmount(),
                    true
            );
            list.add(temp);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public String getTotalAmount(String orderId) {
        Double totalAmount = 0.0;
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, OrderItemsTable.COLUMN_NAME_ORDER_ID + " = ?", new String[]{orderId}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderItemModel obj = cursorToOrderItemModel(cursor);
            totalAmount += obj.getAmount();
            cursor.moveToNext();
        }
        cursor.close();
        return "" + totalAmount;
    }

    public Double getTotalQuantity(String productCode) {
        Double totalQuantity = 0.0;
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ?", new String[]{productCode}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderItemModel obj = cursorToOrderItemModel(cursor);
            totalQuantity += obj.getQuantity();
            cursor.moveToNext();
        }
        cursor.close();
        return totalQuantity;
    }

    public OrderItemModel getRow(String productCode, String orderId) {
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null,
                OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ? and " +
                        OrderItemsTable.COLUMN_NAME_ORDER_ID + " = ? ", new String[]{productCode, orderId}, null, null, null);
        OrderItemModel productModel = null;
        if (cursor.moveToFirst()) {
            productModel = cursorToOrderItemModel(cursor);
        }
        cursor.close();
        return productModel;
    }

    @Contract("_ -> !null")
    private OrderItemModel cursorToOrderItemModel(Cursor cursor) {
        return new OrderItemModel(
                cursor.getString(OrderItemsTable.COLUMN_INDEX_ORDER_ID),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_PRODUCT_CODE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_RATE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_QUANTITY),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_AMOUNT),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_CUSTOMER_CODE));
    }

    private JSONObject cursorToJSONObject(Cursor cursor) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ProductCode", cursor.getString(OrderItemsTable.COLUMN_INDEX_PRODUCT_CODE));
            jsonObject.put("Rate", cursor.getString(OrderItemsTable.COLUMN_INDEX_RATE));
            jsonObject.put("Quantity", cursor.getString(OrderItemsTable.COLUMN_INDEX_QUANTITY));
            jsonObject.put("Amount", cursor.getString(OrderItemsTable.COLUMN_INDEX_AMOUNT));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    protected class OrderItemsTable implements BaseColumns {
        public static final String TABLE_NAME = "order_items";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_PRODUCT_CODE = "product_code";
        public static final String COLUMN_NAME_RATE = "rate";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final String COLUMN_NAME_CUSTOMER_CODE = "customer_code";
        public static final int COLUMN_INDEX_ORDER_ID = 1;
        public static final int COLUMN_INDEX_PRODUCT_CODE = 2;
        public static final int COLUMN_INDEX_RATE = 3;
        public static final int COLUMN_INDEX_QUANTITY = 4;
        public static final int COLUMN_INDEX_AMOUNT = 5;
        public static final int COLUMN_INDEX_CUSTOMER_CODE = 6;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String UNIQUE = " UNIQUE ";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_ORDER_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PRODUCT_CODE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_RATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_QUANTITY + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_AMOUNT + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CUSTOMER_CODE + TEXT_TYPE + COMMA_SEP +
                        UNIQUE + "( " + COLUMN_NAME_PRODUCT_CODE + COMMA_SEP + COLUMN_NAME_ORDER_ID + COMMA_SEP + COLUMN_NAME_CUSTOMER_CODE + ") ON CONFLICT REPLACE)";
    }

}
