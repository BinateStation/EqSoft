package rkr.binatestation.eqsoft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.network.DataSync;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class HomeActivity extends AppCompatActivity {

    TextView username, totalOrders, totalAmount, amountReceived, amountPending, noOfReceipts, noOfProducts, noOfCustomers;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        username = (TextView) findViewById(R.id.AH_username);
        totalOrders = (TextView) findViewById(R.id.AH_totalOrders);
        totalAmount = (TextView) findViewById(R.id.AH_totalAmount);
        amountReceived = (TextView) findViewById(R.id.AH_amountReceived);
        amountPending = (TextView) findViewById(R.id.AH_amountPending);
        noOfReceipts = (TextView) findViewById(R.id.AH_noOfReceipts);
        noOfProducts = (TextView) findViewById(R.id.AH_noOfProducts);
        noOfCustomers = (TextView) findViewById(R.id.AH_noOfCustomers);

        username.setText(Util.getStringFromSharedPreferences(username.getContext(), Constants.KEY_USER_NAME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeDashboard(getBaseContext());
    }

    private void initializeDashboard(final Context context) {
        new AsyncTask<Void, Void, Map<String, String>>() {
            @Override
            protected Map<String, String> doInBackground(Void... voids) {
                OrderModel orderModelDB = new OrderModel(context);
                orderModelDB.open();
                Map<String, String> stringMap = orderModelDB.getTotalAmountReceivedAndPendingAmount();
                stringMap.put("KEY_TOTAL_SALES", orderModelDB.getCount());
                orderModelDB.close();
                ReceiptModel receiptModelDB = new ReceiptModel(context);
                receiptModelDB.open();
                stringMap.put("KEY_TOTAL_RECEIPTS", receiptModelDB.getCount());
                receiptModelDB.close();
                ProductModel productModelDB = new ProductModel(context);
                productModelDB.open();
                stringMap.put("KEY_TOTAL_PRODUCTS", productModelDB.getCount());
                productModelDB.close();
                CustomerModel customerModelDB = new CustomerModel(context);
                customerModelDB.open();
                stringMap.put("KEY_TOTAL_CUSTOMERS", customerModelDB.getCount());
                customerModelDB.close();
                return stringMap;
            }

            @Override
            protected void onPostExecute(Map<String, String> stringStringMap) {
                super.onPostExecute(stringStringMap);
                totalOrders.setText(stringStringMap.get("KEY_TOTAL_SALES"));
                totalAmount.setText(stringStringMap.get("KEY_TOTAL_AMOUNT"));
                amountReceived.setText(stringStringMap.get("KEY_TOTAL_AMOUNT_RECEIVED"));
                amountPending.setText(stringStringMap.get("KEY_TOTAL_PENDING_AMOUNT"));
                noOfReceipts.setText(stringStringMap.get("KEY_TOTAL_RECEIPTS"));
                noOfProducts.setText(stringStringMap.get("KEY_TOTAL_PRODUCTS"));
                noOfCustomers.setText(stringStringMap.get("KEY_TOTAL_CUSTOMERS"));
            }
        }.execute();
    }

    public void onCustomerClick(View view) {
        startActivityForResult(new Intent(view.getContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER);
    }

    public void onProductsClick(View view) {
        startActivity(new Intent(view.getContext(), ProductsActivity.class));
    }

    public void onReceiptsClick(View view) {
        startActivity(new Intent(view.getContext(), ReceiptsActivity.class));
    }

    public void order(View view) {
        startActivityForResult(new Intent(view.getContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER);
    }


    private void alertSync() {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Alert")
                .setMessage("Sync will replace the previously sync data. Please ensure that previously synced data is copied to your computer and proceed..")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sync();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void sync() {
        new DataSync(getBaseContext()) {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Please wait ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (aBoolean) {
                    initializeDashboard(getBaseContext());
                    Util.showAlert(HomeActivity.this, "Alert", "Successfully synced", false);
                } else {
                    Util.showAlert(HomeActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
                }
            }
        }.execute(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.GM_usbSync:
                alertSync();
                return true;
            case R.id.GM_logout:
                Util.logoutAlert(HomeActivity.this, "Alert", "Are you sure you want to logout.?");
                return true;
            case R.id.GM_clearAll:
                alertClearAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertClearAll() {
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Alert")
                .setMessage("This will clear all the data in your database, and can't able to recollect. Are you sure you need to proceed..?")
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearAll();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void clearAll() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                ProductModel productModelDB = new ProductModel(getBaseContext());
                productModelDB.open();
                productModelDB.deleteAll();
                productModelDB.close();

                CustomerModel customerModelDB = new CustomerModel(getBaseContext());
                customerModelDB.open();
                customerModelDB.deleteAll();
                customerModelDB.close();

                OrderModel orderModelDB = new OrderModel(getBaseContext());
                orderModelDB.open();
                orderModelDB.deleteAll();
                orderModelDB.close();

                OrderItemModel orderItemModelDB = new OrderItemModel(getBaseContext());
                orderItemModelDB.open();
                orderItemModelDB.deleteAll();
                orderItemModelDB.close();

                ReceiptModel receiptModelDB = new ReceiptModel(getBaseContext());
                receiptModelDB.open();
                receiptModelDB.deleteAll();
                receiptModelDB.close();

                return true;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = new ProgressDialog(HomeActivity.this);
                progressDialog.setMessage("Please wait ...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (aBoolean) {
                    initializeDashboard(getBaseContext());
                    Util.showAlert(HomeActivity.this, "Alert", "Successfully deleted", false);
                } else {
                    Util.showAlert(HomeActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_CUSTOMER && data.hasExtra(Constants.KEY_CUSTOMER)) {
            startActivity(new Intent(getBaseContext(), OrderActivity.class)
                    .putExtra(Constants.KEY_CUSTOMER, data.getSerializableExtra(Constants.KEY_CUSTOMER))
            );
        }
    }
}
