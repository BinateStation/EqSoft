package rkr.binatestation.eqsoft.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RKRsEqSoftSQLiteHelper extends SQLiteOpenHelper {

    private static int DB_VERSION = 3;
    Context context;

    public RKRsEqSoftSQLiteHelper(Context context) {
        super(context, context.getPackageName(), null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(UserDetailsModel.UserDetailsTable.SQL_CREATE_USER_DETAILS);
        database.execSQL(CustomerModel.CustomersTable.SQL_CREATE_USER_DETAILS);
        database.execSQL(ProductModel.ProductsTable.SQL_CREATE_USER_DETAILS);
        database.execSQL(OrderModel.OrdersTable.SQL_CREATE_USER_DETAILS);
        database.execSQL(OrderItemModel.OrderItemsTable.SQL_CREATE_USER_DETAILS);
        database.execSQL(ReceiptModel.ReceiptsTable.SQL_CREATE_USER_DETAILS);
        database.execSQL(OrderItemModelTemp.OrderItemsTable.SQL_CREATE_USER_DETAILS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(RKRsEqSoftSQLiteHelper.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + UserDetailsModel.UserDetailsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + CustomerModel.CustomersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ProductModel.ProductsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OrderModel.OrdersTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OrderItemModel.OrderItemsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReceiptModel.ReceiptsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OrderItemModelTemp.OrderItemsTable.TABLE_NAME);
        onCreate(db);
    }
}