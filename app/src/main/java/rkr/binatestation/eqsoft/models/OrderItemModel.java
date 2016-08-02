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
public class OrderItemModel implements Serializable {

    Long orderId;
    String productCode;
    String rate;
    String quantity;
    String amount;

    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public OrderItemModel(Long orderId, String productCode, String rate, String quantity, String amount) {
        this.orderId = orderId;
        this.productCode = productCode;
        this.rate = rate;
        this.quantity = quantity;
        this.amount = amount;
    }

    public OrderItemModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
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

    public void insert(OrderItemModel obj) {
        ContentValues values = new ContentValues();
        values.put(OrderItemsTable.COLUMN_NAME_ORDER_ID, obj.getOrderId());
        values.put(OrderItemsTable.COLUMN_NAME_PRODUCT_CODE, obj.getProductCode());
        values.put(OrderItemsTable.COLUMN_NAME_RATE, obj.getRate());
        values.put(OrderItemsTable.COLUMN_NAME_QUANTITY, obj.getQuantity());
        values.put(OrderItemsTable.COLUMN_NAME_AMOUNT, obj.getAmount());

        long insertId;
        insertId = database.insert(OrderItemsTable.TABLE_NAME, null, values);

        Log.i("InsertID", "Categories : " + insertId);
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
                    OrderItemsTable.COLUMN_NAME_AMOUNT + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                OrderItemModel master = masterList.get(i);
                query += master.getOrderId() + "' , '" +
                        master.getProductCode() + "' , '" +
                        master.getRate() + "' , '" +
                        master.getQuantity() + "' , '" +
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

    public void updateRow(OrderItemModel obj, String id) {

        ContentValues values = new ContentValues();
        values.put(OrderItemsTable.COLUMN_NAME_ORDER_ID, obj.getOrderId());
        values.put(OrderItemsTable.COLUMN_NAME_PRODUCT_CODE, obj.getProductCode());
        values.put(OrderItemsTable.COLUMN_NAME_RATE, obj.getRate());
        values.put(OrderItemsTable.COLUMN_NAME_QUANTITY, obj.getQuantity());
        values.put(OrderItemsTable.COLUMN_NAME_AMOUNT, obj.getAmount());

        database.update(OrderItemsTable.TABLE_NAME, values, OrderItemsTable._ID + " = ?", new String[]{id});
        System.out.println("Categories Row Updated with id: " + id);
    }

    public void deleteRow(String id) {
        database.delete(OrderItemsTable.TABLE_NAME, OrderItemsTable._ID + " = ?", new String[]{id});
        System.out.println("Categories Row deleted with id: " + id);
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
            OrderItemModel obj = cursorToProductModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public OrderItemModel getRow(String id) {
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, OrderItemsTable._ID + " = ?", new String[]{id}, null, null, null);
        OrderItemModel productModel = null;
        if (cursor.moveToFirst()) {
            productModel = cursorToProductModel(cursor);
        }
        cursor.close();
        return productModel;
    }

    @Contract("_ -> !null")
    private OrderItemModel cursorToProductModel(Cursor cursor) {
        return new OrderItemModel(
                cursor.getLong(OrderItemsTable.COLUMN_INDEX_ORDER_ID),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_PRODUCT_CODE),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_RATE),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_QUANTITY),
                cursor.getString(OrderItemsTable.COLUMN_INDEX_AMOUNT)
        );
    }

    protected class OrderItemsTable implements BaseColumns {
        public static final String TABLE_NAME = "order_items";
        public static final String COLUMN_NAME_ORDER_ID = "order_id";
        public static final String COLUMN_NAME_PRODUCT_CODE = "product_code";
        public static final String COLUMN_NAME_RATE = "rate";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final int COLUMN_INDEX_ORDER_ID = 1;
        public static final int COLUMN_INDEX_PRODUCT_CODE = 2;
        public static final int COLUMN_INDEX_RATE = 3;
        public static final int COLUMN_INDEX_QUANTITY = 4;
        public static final int COLUMN_INDEX_AMOUNT = 5;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_ORDER_ID + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PRODUCT_CODE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_RATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_QUANTITY + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_AMOUNT + TEXT_TYPE +
                        " )";
    }

}
