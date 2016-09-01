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
public class ProductModel implements Serializable {
    String category;
    String code;
    Double MRP;
    String name;
    Double sellingRate;
    Double stock;
    Double taxRate;
    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public ProductModel(String category, String code, Double MRP, String name, Double sellingRate, Double stock, Double taxRate) {
        this.category = category;
        this.code = code;
        this.MRP = MRP;
        this.name = name;
        this.sellingRate = sellingRate;
        this.stock = stock;
        this.taxRate = taxRate;
    }

    public ProductModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getMRP() {
        return MRP;
    }

    public void setMRP(Double MRP) {
        this.MRP = MRP;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(Double sellingRate) {
        this.sellingRate = sellingRate;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public Double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(Double taxRate) {
        this.taxRate = taxRate;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(ProductModel obj) {
        if (getRow(obj.getCode()) == null) {
            ContentValues values = new ContentValues();
            values.put(ProductsTable.COLUMN_NAME_CATEGORY, obj.getCategory());
            values.put(ProductsTable.COLUMN_NAME_CODE, obj.getCode());
            values.put(ProductsTable.COLUMN_NAME_MRP, obj.getMRP());
            values.put(ProductsTable.COLUMN_NAME_NAME, obj.getName());
            values.put(ProductsTable.COLUMN_NAME_SELLING_RATE, obj.getSellingRate());
            values.put(ProductsTable.COLUMN_NAME_STOCK, obj.getStock());
            values.put(ProductsTable.COLUMN_NAME_TAX_RATE, obj.getTaxRate());

            long insertId;
            insertId = database.insert(ProductsTable.TABLE_NAME, null, values);

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
    public void insertMultipleRows(List<ProductModel> productModelList) {
        for (List<ProductModel> masterList : Lists.partition(productModelList, 500)) {
            String query = "REPLACE INTO '" +
                    ProductsTable.TABLE_NAME + "' ('" +
                    ProductsTable.COLUMN_NAME_CATEGORY + "','" +
                    ProductsTable.COLUMN_NAME_CODE + "','" +
                    ProductsTable.COLUMN_NAME_MRP + "','" +
                    ProductsTable.COLUMN_NAME_NAME + "','" +
                    ProductsTable.COLUMN_NAME_SELLING_RATE + "','" +
                    ProductsTable.COLUMN_NAME_STOCK + "','" +
                    ProductsTable.COLUMN_NAME_TAX_RATE + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                ProductModel master = masterList.get(i);
                query += master.getCategory() + "' , '" +
                        master.getCode() + "' , '" +
                        master.getMRP() + "' , '" +
                        master.getName() + "' , '" +
                        master.getSellingRate() + "' , '" +
                        master.getStock() + "' , '" +
                        master.getTaxRate() + "')";
                if (i != (masterList.size() - 1)) {
                    query += ",('";
                }
            }
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Query executed", cursor.getCount() + " : " + query);
            cursor.close();
        }
    }

    public void updateRow(ProductModel obj) {

        ContentValues values = new ContentValues();
        values.put(ProductsTable.COLUMN_NAME_CATEGORY, obj.getCategory());
        values.put(ProductsTable.COLUMN_NAME_CODE, obj.getCode());
        values.put(ProductsTable.COLUMN_NAME_MRP, obj.getMRP());
        values.put(ProductsTable.COLUMN_NAME_NAME, obj.getName());
        values.put(ProductsTable.COLUMN_NAME_SELLING_RATE, obj.getSellingRate());
        values.put(ProductsTable.COLUMN_NAME_STOCK, obj.getStock());
        values.put(ProductsTable.COLUMN_NAME_TAX_RATE, obj.getTaxRate());

        database.update(ProductsTable.TABLE_NAME, values, ProductsTable.COLUMN_NAME_CODE + " = ?", new String[]{obj.getCode()});
        System.out.println("Categories Row Updated with id: " + obj.getCode());
    }

    public void deleteRow(ProductModel obj) {
        database.delete(ProductsTable.TABLE_NAME, ProductsTable.COLUMN_NAME_CODE + " = ?", new String[]{obj.getCode()});
        System.out.println("Categories Row deleted with id: " + obj.getCode());
    }

    public void deleteAll() {
        database.delete(ProductsTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<ProductModel> getAllRows() {
        List<ProductModel> list = new ArrayList<>();
        Cursor cursor = database.query(ProductsTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ProductModel obj = cursorToProductModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<ProductModel> getAllRows(String query, int sortType) {
        List<ProductModel> list = new ArrayList<>();
        String sortBy;
        switch (sortType) {
            case 1:
                sortBy = ProductsTable.COLUMN_NAME_CATEGORY;
                break;
            default:
                sortBy = ProductsTable.COLUMN_NAME_NAME;
        }
        Cursor cursor = database.query(ProductsTable.TABLE_NAME, null, ProductsTable.COLUMN_NAME_NAME + " LIKE '" + query.replace(" ", "%") + "%' OR " +
                ProductsTable.COLUMN_NAME_CODE + " LIKE '" + query.replace(" ", "%") + "%' OR " +
                ProductsTable.COLUMN_NAME_CATEGORY + " LIKE '" + query.replace(" ", "%") + "%' ", null, null, null, sortBy);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ProductModel obj = cursorToProductModel(cursor);
            obj.setStock(getCurrentStock(obj.getCode()));
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<ProductModel> getAllSelectedProducts(String selectionArgs) {
        List<ProductModel> list = new ArrayList<>();
        Cursor cursor = database.query(ProductsTable.TABLE_NAME, null, String.format("%s IN (%s)", ProductsTable.COLUMN_NAME_CODE, selectionArgs), null, null, null, ProductsTable.COLUMN_NAME_CATEGORY);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ProductModel obj = cursorToProductModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public ProductModel getRow(String code) {
        Cursor cursor = database.query(ProductsTable.TABLE_NAME, null, ProductsTable.COLUMN_NAME_CODE + " = ?", new String[]{code}, null, null, null);
        ProductModel productModel = null;
        if (cursor.moveToFirst()) {
            productModel = cursorToProductModel(cursor);
        }
        cursor.close();
        return productModel;
    }

    public Double getCurrentStock(String code) {
        Cursor cursor = database.query(ProductsTable.TABLE_NAME, null, ProductsTable.COLUMN_NAME_CODE + " = ?", new String[]{code}, null, null, null);
        Double currentStock = 0.0;
        if (cursor.moveToFirst()) {
            currentStock += cursorToProductModel(cursor).getStock();
        }
        cursor.close();
        Cursor cursorOrderItem = database.query(OrderItemModel.OrderItemsTable.TABLE_NAME, null, OrderItemModel.OrderItemsTable.COLUMN_NAME_PRODUCT_CODE + " = ?", new String[]{code}, null, null, null);
        cursorOrderItem.moveToFirst();
        while (!cursorOrderItem.isAfterLast()) {
            OrderItemModel obj = OrderItemModel.cursorToOrderItemModelStatic(cursorOrderItem);
            currentStock -= obj.getQuantity();
            cursorOrderItem.moveToNext();
        }
        cursorOrderItem.close();
        return currentStock;
    }

    /**
     * This method will counts how many rows available in this table
     *
     * @return the count of the OrdersTable#TABLE_NAME
     */
    public String getCount() {
        Cursor cursor = database.rawQuery("SELECT count(*) FROM " + ProductsTable.TABLE_NAME, null);
        Integer count = 0;
        if (cursor.moveToFirst())
            count += cursor.getInt(0);
        cursor.close();
        return "" + count;
    }

    @Contract("_ -> !null")
    private ProductModel cursorToProductModel(Cursor cursor) {
        return new ProductModel(
                cursor.getString(ProductsTable.COLUMN_INDEX_CATEGORY),
                cursor.getString(ProductsTable.COLUMN_INDEX_CODE),
                cursor.getDouble(ProductsTable.COLUMN_INDEX_MRP),
                cursor.getString(ProductsTable.COLUMN_INDEX_NAME),
                cursor.getDouble(ProductsTable.COLUMN_INDEX_SELLING_RATE),
                cursor.getDouble(ProductsTable.COLUMN_INDEX_STOCK),
                cursor.getDouble(ProductsTable.COLUMN_INDEX_TAX_RATE)
        );
    }

    protected class ProductsTable implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_MRP = "MRP";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SELLING_RATE = "selling_rate";
        public static final String COLUMN_NAME_STOCK = "stock";
        public static final String COLUMN_NAME_TAX_RATE = "tax_rate";
        public static final int COLUMN_INDEX_CATEGORY = 1;
        public static final int COLUMN_INDEX_CODE = 2;
        public static final int COLUMN_INDEX_MRP = 3;
        public static final int COLUMN_INDEX_NAME = 4;
        public static final int COLUMN_INDEX_SELLING_RATE = 5;
        public static final int COLUMN_INDEX_STOCK = 6;
        public static final int COLUMN_INDEX_TAX_RATE = 7;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        private static final String UNIQUE = " UNIQUE ";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_CATEGORY + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CODE + TEXT_TYPE + UNIQUE + COMMA_SEP +
                        COLUMN_NAME_MRP + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_SELLING_RATE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_STOCK + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_TAX_RATE + TEXT_TYPE +
                        " )";
    }

}
