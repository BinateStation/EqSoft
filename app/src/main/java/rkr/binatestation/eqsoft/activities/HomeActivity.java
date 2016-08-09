package rkr.binatestation.eqsoft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.Map;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.fragments.CustomerSearchFragment;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.network.DataSync;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class HomeActivity extends AppCompatActivity {

    TextView totalSales, amountReceived, amountPending, noOfReceipts, noOfProducts, noOfCustomers;
    CustomerSearchFragment customerSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        totalSales = (TextView) findViewById(R.id.AH_totalSales);
        amountReceived = (TextView) findViewById(R.id.AH_amountReceived);
        amountPending = (TextView) findViewById(R.id.AH_amountPending);
        noOfReceipts = (TextView) findViewById(R.id.AH_noOfReceipts);
        noOfProducts = (TextView) findViewById(R.id.AH_noOfProducts);
        noOfCustomers = (TextView) findViewById(R.id.AH_noOfCustomers);
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
                totalSales.setText(stringStringMap.get("KEY_TOTAL_SALES"));
                amountReceived.setText(stringStringMap.get("KEY_TOTAL_AMOUNT_RECEIVED"));
                amountPending.setText(stringStringMap.get("KEY_TOTAL_PENDING_AMOUNT"));
                noOfReceipts.setText(stringStringMap.get("KEY_TOTAL_RECEIPTS"));
                noOfProducts.setText(stringStringMap.get("KEY_TOTAL_PRODUCTS"));
                noOfCustomers.setText(stringStringMap.get("KEY_TOTAL_CUSTOMERS"));
            }
        }.execute();
    }

    public void onCustomerClick(View view) {
        startActivity(new Intent(view.getContext(), CustomersActivity.class));
    }

    public void onProductsClick(View view) {
        startActivity(new Intent(view.getContext(), ProductsActivity.class));
    }

    public void onReceiptsClick(View view) {
        startActivity(new Intent(view.getContext(), ReceiptsActivity.class));
    }

    public void order(View view) {
        customerSearchFragment = CustomerSearchFragment.newInstance(new CustomerSearchFragment.OnCustomerSearchFragmentInteractionListener() {
            @Override
            public void onItemSelected(CustomerModel customerModel) {
                if (customerSearchFragment != null) {
                    customerSearchFragment.dismiss();
                }
                startActivity(
                        new Intent(getBaseContext(), OrderActivity.class)
                                .putExtra(Constants.KEY_CUSTOMER, customerModel)
                );
            }
        });
        customerSearchFragment.show(getSupportFragmentManager(), CustomerSearchFragment.tag);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.GM_usbSync:
                new DataSync(getBaseContext()) {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        super.onPostExecute(aBoolean);
                        if (aBoolean) {
                            Util.showAlert(HomeActivity.this, "Alert", "Successfully synced", false);
                        } else {
                            Util.showAlert(HomeActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
                        }
                    }
                }.execute(0);
                return true;
            case R.id.GM_logout:
                Util.logoutAlert(HomeActivity.this, "Alert", "Are you sure you want to logout.?");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
