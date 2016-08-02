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
 * CustomerModel.
 */
public class CustomerModel implements Serializable {
    String address1;
    String address2;
    String address3;
    String balance;
    String code;
    String email;
    String ledgerName;
    String mobile;
    String name;
    String phone;
    String route;
    String routeIndex;
    Context context;
    private SQLiteDatabase database;
    private RKRsEqSoftSQLiteHelper dbHelper;

    public CustomerModel(String address1, String address2, String address3, String balance, String code, String email, String ledgerName, String mobile, String name, String phone, String route, String routeIndex) {
        this.address1 = address1;
        this.address2 = address2;
        this.address3 = address3;
        this.balance = balance;
        this.code = code;
        this.email = email;
        this.ledgerName = ledgerName;
        this.mobile = mobile;
        this.name = name;
        this.phone = phone;
        this.route = route;
        this.routeIndex = routeIndex;
    }

    public CustomerModel(Context context) {
        this.context = context;
        dbHelper = new RKRsEqSoftSQLiteHelper(context);
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLedgerName() {
        return ledgerName;
    }

    public void setLedgerName(String ledgerName) {
        this.ledgerName = ledgerName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getRouteIndex() {
        return routeIndex;
    }

    public void setRouteIndex(String routeIndex) {
        this.routeIndex = routeIndex;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void insert(CustomerModel obj) {
        if (getRow(obj.getCode()) == null) {
            ContentValues values = new ContentValues();
            values.put(CustomersTable.COLUMN_NAME_ADDRESS_1, obj.getAddress1());
            values.put(CustomersTable.COLUMN_NAME_ADDRESS_2, obj.getAddress2());
            values.put(CustomersTable.COLUMN_NAME_ADDRESS_3, obj.getAddress3());
            values.put(CustomersTable.COLUMN_NAME_BALANCE, obj.getBalance());
            values.put(CustomersTable.COLUMN_NAME_CODE, obj.getCode());
            values.put(CustomersTable.COLUMN_NAME_EMAIL, obj.getEmail());
            values.put(CustomersTable.COLUMN_NAME_LEDGER_NAME, obj.getLedgerName());
            values.put(CustomersTable.COLUMN_NAME_MOBILE, obj.getMobile());
            values.put(CustomersTable.COLUMN_NAME_NAME, obj.getName());
            values.put(CustomersTable.COLUMN_NAME_PHONE, obj.getPhone());
            values.put(CustomersTable.COLUMN_NAME_ROUTE, obj.getRoute());
            values.put(CustomersTable.COLUMN_NAME_ROUTE_INDEX, obj.getRouteIndex());

            long insertId;
            insertId = database.insert(CustomersTable.TABLE_NAME, null, values);

            Log.i("InsertID", "Categories : " + insertId);
        } else {
            updateRow(obj);
        }
    }

    /**
     * This method will do a compound insert in to the table
     * It will splits the list in to lists of 500 objects and generate insert query for all 500 objects in each
     *
     * @param customerModelList the list of table rows to insert.
     */
    public void insertMultipleRows(List<CustomerModel> customerModelList) {
        for (List<CustomerModel> masterList : Lists.partition(customerModelList, 500)) {
            String query = "REPLACE INTO '" +
                    CustomersTable.TABLE_NAME + "' ('" +
                    CustomersTable.COLUMN_NAME_ADDRESS_1 + "','" +
                    CustomersTable.COLUMN_NAME_ADDRESS_2 + "','" +
                    CustomersTable.COLUMN_NAME_ADDRESS_3 + "','" +
                    CustomersTable.COLUMN_NAME_BALANCE + "','" +
                    CustomersTable.COLUMN_NAME_CODE + "','" +
                    CustomersTable.COLUMN_NAME_EMAIL + "','" +
                    CustomersTable.COLUMN_NAME_LEDGER_NAME + "','" +
                    CustomersTable.COLUMN_NAME_MOBILE + "','" +
                    CustomersTable.COLUMN_NAME_NAME + "','" +
                    CustomersTable.COLUMN_NAME_PHONE + "','" +
                    CustomersTable.COLUMN_NAME_ROUTE + "','" +
                    CustomersTable.COLUMN_NAME_ROUTE_INDEX + "') VALUES ('";
            for (int i = 0; i < masterList.size(); i++) {
                CustomerModel master = masterList.get(i);
                query += master.getAddress1() + "' , '" +
                        master.getAddress2() + "' , '" +
                        master.getAddress3() + "' , '" +
                        master.getBalance() + "' , '" +
                        master.getCode() + "' , '" +
                        master.getEmail() + "' , '" +
                        master.getLedgerName() + "' , '" +
                        master.getMobile() + "' , '" +
                        master.getName() + "' , '" +
                        master.getPhone() + "' , '" +
                        master.getRoute() + "' , '" +
                        master.getRouteIndex() + "')";
                if (i != (masterList.size() - 1)) {
                    query += ",('";
                }
            }
            Cursor cursor = database.rawQuery(query, null);
            Log.d("Query executed", cursor.getCount() + " : " + query);
            cursor.close();
        }
    }

    public void updateRow(CustomerModel obj) {

        ContentValues values = new ContentValues();
        values.put(CustomersTable.COLUMN_NAME_ADDRESS_1, obj.getAddress1());
        values.put(CustomersTable.COLUMN_NAME_ADDRESS_2, obj.getAddress2());
        values.put(CustomersTable.COLUMN_NAME_ADDRESS_3, obj.getAddress3());
        values.put(CustomersTable.COLUMN_NAME_BALANCE, obj.getBalance());
        values.put(CustomersTable.COLUMN_NAME_CODE, obj.getCode());
        values.put(CustomersTable.COLUMN_NAME_EMAIL, obj.getEmail());
        values.put(CustomersTable.COLUMN_NAME_LEDGER_NAME, obj.getLedgerName());
        values.put(CustomersTable.COLUMN_NAME_MOBILE, obj.getMobile());
        values.put(CustomersTable.COLUMN_NAME_NAME, obj.getName());
        values.put(CustomersTable.COLUMN_NAME_PHONE, obj.getPhone());
        values.put(CustomersTable.COLUMN_NAME_ROUTE, obj.getRoute());
        values.put(CustomersTable.COLUMN_NAME_ROUTE_INDEX, obj.getRouteIndex());

        database.update(CustomersTable.TABLE_NAME, values, CustomersTable.COLUMN_NAME_CODE + " = ?", new String[]{obj.getCode()});
        System.out.println("Categories Row Updated with id: " + obj.getCode());
    }

    public void deleteRow(CustomerModel obj) {
        database.delete(CustomersTable.TABLE_NAME, CustomersTable.COLUMN_NAME_CODE + " = ?", new String[]{obj.getCode()});
        System.out.println("Categories Row deleted with id: " + obj.getCode());
    }

    public void deleteAll() {
        database.delete(CustomersTable.TABLE_NAME, null, null);
        System.out.println("Categories table Deleted ALL");
    }

    public List<CustomerModel> getAllRows() {
        List<CustomerModel> list = new ArrayList<>();
        Cursor cursor = database.query(CustomersTable.TABLE_NAME, null, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CustomerModel obj = cursorToCustomerModel(cursor);
            list.add(obj);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }


    public CustomerModel getRow(String code) {
        Cursor cursor = database.query(CustomersTable.TABLE_NAME, null, CustomersTable.COLUMN_NAME_CODE + " = ?", new String[]{code}, null, null, null);
        CustomerModel customerModel = null;
        if (cursor.moveToFirst()) {
            customerModel = cursorToCustomerModel(cursor);
        }
        cursor.close();
        return customerModel;
    }

    @Contract("_ -> !null")
    private CustomerModel cursorToCustomerModel(Cursor cursor) {
        return new CustomerModel(
                cursor.getString(CustomersTable.COLUMN_INDEX_ADDRESS_1),
                cursor.getString(CustomersTable.COLUMN_INDEX_ADDRESS_2),
                cursor.getString(CustomersTable.COLUMN_INDEX_ADDRESS_3),
                cursor.getString(CustomersTable.COLUMN_INDEX_BALANCE),
                cursor.getString(CustomersTable.COLUMN_INDEX_CODE),
                cursor.getString(CustomersTable.COLUMN_INDEX_EMAIL),
                cursor.getString(CustomersTable.COLUMN_INDEX_LEDGER_NAME),
                cursor.getString(CustomersTable.COLUMN_INDEX_MOBILE),
                cursor.getString(CustomersTable.COLUMN_INDEX_NAME),
                cursor.getString(CustomersTable.COLUMN_INDEX_PHONE),
                cursor.getString(CustomersTable.COLUMN_INDEX_ROUTE),
                cursor.getString(CustomersTable.COLUMN_INDEX_ROUTE_INDEX)
        );
    }

    protected class CustomersTable implements BaseColumns {
        public static final String TABLE_NAME = "customers";
        public static final String COLUMN_NAME_ADDRESS_1 = "address1";
        public static final String COLUMN_NAME_ADDRESS_2 = "address2";
        public static final String COLUMN_NAME_ADDRESS_3 = "address3";
        public static final String COLUMN_NAME_BALANCE = "balance";
        public static final String COLUMN_NAME_CODE = "code";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_LEDGER_NAME = "ledger_name";
        public static final String COLUMN_NAME_MOBILE = "mobile";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PHONE = "phone";
        public static final String COLUMN_NAME_ROUTE = "route";
        public static final String COLUMN_NAME_ROUTE_INDEX = "route_index";
        public static final int COLUMN_INDEX_ADDRESS_1 = 1;
        public static final int COLUMN_INDEX_ADDRESS_2 = 2;
        public static final int COLUMN_INDEX_ADDRESS_3 = 3;
        public static final int COLUMN_INDEX_BALANCE = 4;
        public static final int COLUMN_INDEX_CODE = 5;
        public static final int COLUMN_INDEX_EMAIL = 6;
        public static final int COLUMN_INDEX_LEDGER_NAME = 7;
        public static final int COLUMN_INDEX_MOBILE = 8;
        public static final int COLUMN_INDEX_NAME = 9;
        public static final int COLUMN_INDEX_PHONE = 10;
        public static final int COLUMN_INDEX_ROUTE = 11;
        public static final int COLUMN_INDEX_ROUTE_INDEX = 12;

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ",";
        public static final String SQL_CREATE_USER_DETAILS =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_ADDRESS_1 + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ADDRESS_2 + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ADDRESS_3 + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_BALANCE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_CODE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_EMAIL + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_LEDGER_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_MOBILE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_PHONE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ROUTE + TEXT_TYPE + COMMA_SEP +
                        COLUMN_NAME_ROUTE_INDEX + TEXT_TYPE +
                        " )";
    }

}
