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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by RKR on 1/8/2016.
 * OrderItemModelTemp.
 */
public class OrderItemModelTemp implements Serializable {

    String productCode;
    Double rate;
    Double quantity;
    Double amount;
    Boolean isTemp;

    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public OrderItemModelTemp(String productCode, Double rate, Double quantity, Double amount, Boolean isTemp) {
        this.productCode = productCode;
        this.rate = rate;
        this.quantity = quantity;
        this.amount = amount;
        this.isTemp = isTemp;
    }

    public OrderItemModelTemp(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public static OrderItemModelTemp cursorToOrderItemModelTempStatic(Cursor cursor) {
        return new OrderItemModelTemp(
                cursor.getString(OrderItemsTable.COLUMN_INDEX_PRODUCT_CODE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_RATE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_QUANTITY),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_AMOUNT),
                true
        );
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

    public Boolean getTemp() {
        return isTemp;
    }

    public void setTemp(Boolean temp) {
        isTemp = temp;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(OrderItemModelTemp obj) {
        if (getRow(obj.getProductCode()) == null) {
            ContentValues values = new ContentValues();
            values.put(OrderItemsTable.COLUMN_NAME_PRODUCT_CODE, obj.getProductCode());
            values.put(OrderItemsTable.COLUMN_NAME_RATE, obj.getRate());
            values.put(OrderItemsTable.COLUMN_NAME_QUANTITY, obj.getQuantity());
            values.put(OrderItemsTable.COLUMN_NAME_AMOUNT, obj.getAmount());

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
    public void insertMultipleRows(List<OrderItemModelTemp> productModelList) {
        for (List<OrderItemModelTemp> masterList : Lists.partition(productModelList, 500)) {
            String query = "REPLACE INTO '" +
                    OrderItemsTable.TABLE_NAME + "' ('" +
                    OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + "','" +
                    OrderItemsTable.COLUMN_NAME_RATE + "','" +
                    OrderItemsTable.COLUMN_NAME_QUANTITY + "','" +
                    OrderItemsTable.COLUMN_NAME_AMOUNT + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                OrderItemModelTemp master = masterList.get(i);
                query += master.getProductCode() + "' , '" +
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

    public void updateRow(OrderItemModelTemp obj) {

        ContentValues values = new ContentValues();
        values.put(OrderItemsTable.COLUMN_NAME_PRODUCT_CODE, obj.getProductCode());
        values.put(OrderItemsTable.COLUMN_NAME_RATE, obj.getRate());
        values.put(OrderItemsTable.COLUMN_NAME_QUANTITY, obj.getQuantity());
        values.put(OrderItemsTable.COLUMN_NAME_AMOUNT, obj.getAmount());

        database.update(OrderItemsTable.TABLE_NAME, values,
                OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ? ", new String[]{obj.getProductCode()});
        System.out.println("Categories Row Updated with id: " + obj.getProductCode());
    }

    public void deleteRow(String productCode) {
        database.delete(OrderItemsTable.TABLE_NAME, OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ? ", new String[]{productCode});
        System.out.println("Categories Row deleted with id: " + productCode);
    }

    public void deleteAll() {
        database.delete(OrderItemsTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<OrderItemModelTemp> getAllRows() {
        List<OrderItemModelTemp> list = new ArrayList<>();
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderItemModelTemp obj = cursorToOrderItemModelTemp(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public Map<String, OrderItemModelTemp> getAllRowsAsMap() {
        Map<String, OrderItemModelTemp> list = new LinkedHashMap<>();
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            OrderItemModelTemp obj = cursorToOrderItemModelTemp(cursor);
            list.put(obj.getProductCode(), obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public OrderItemModelTemp getRow(String productCode) {
        Cursor cursor = database.query(OrderItemsTable.TABLE_NAME, null,
                OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ? ", new String[]{productCode}, null, null, null);
        OrderItemModelTemp productModel = null;
        if (cursor.moveToFirst()) {
            productModel = cursorToOrderItemModelTemp(cursor);
        }
        cursor.close();
        return productModel;
    }

    @Contract("_ -> !null")
    private OrderItemModelTemp cursorToOrderItemModelTemp(Cursor cursor) {
        return new OrderItemModelTemp(
                cursor.getString(OrderItemsTable.COLUMN_INDEX_PRODUCT_CODE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_RATE),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_QUANTITY),
                cursor.getDouble(OrderItemsTable.COLUMN_INDEX_AMOUNT),
                true
        );
    }

    protected class OrderItemsTable implements BaseColumns {
        public static final String TABLE_NAME = "order_items_temp";
        public static final String COLUMN_NAME_PRODUCT_CODE = "product_code";
        public static final String COLUMN_NAME_RATE = "rate";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_AMOUNT = "amount";
        public static final int COLUMN_INDEX_PRODUCT_CODE = 1;
        public static final int COLUMN_INDEX_RATE = 2;
        public static final int COLUMN_INDEX_QUANTITY = 3;
        public static final int COLUMN_INDEX_AMOUNT = 4;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String UNIQUE = " UNIQUE ";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_PRODUCT_CODE + TEXT_TYPE + UNIQUE + COMMA_SEP +
                        COLUMN_NAME_RATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_QUANTITY + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_AMOUNT + TEXT_TYPE +
                        " )";
    }

}
