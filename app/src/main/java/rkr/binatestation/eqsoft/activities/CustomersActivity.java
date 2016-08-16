package rkr.binatestation.eqsoft.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.adapters.CustomerAdapter;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.network.DataSync;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class CustomersActivity extends AppCompatActivity {
    ProgressDialog progressDialog;

    SearchView customerSearch;
    RecyclerView customersRecyclerView;
    LinearLayoutManager linearLayoutManager;
    AppCompatSpinner sort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customerSearch = (SearchView) findViewById(R.id.AC_customerSearch);
        customersRecyclerView = (RecyclerView) findViewById(R.id.AC_customerRecyclerView);
        sort = (AppCompatSpinner) findViewById(R.id.AC_sort);

        customerSearch.setQueryHint(getString(R.string.type_to_search));
        customersRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(customersRecyclerView.getContext()));

        linearLayoutManager.scrollToPosition(10);

        customerSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getCustomerList(query, sort.getSelectedItemPosition());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getCustomerList(newText, sort.getSelectedItemPosition());
                return true;
            }
        });

        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getCustomerList("", sort.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        getCustomerList("", sort.getSelectedItemPosition());
    }

    private void getCustomerList(final String query, final int sortType) {
        new AsyncTask<Void, Void, List<CustomerModel>>() {
            @Override
            protected List<CustomerModel> doInBackground(Void... voids) {
                CustomerModel customerModelDB = new CustomerModel(getBaseContext());
                customerModelDB.open();
                List<CustomerModel> customerModelList = customerModelDB.getAllRows(query, sortType);
                customerModelDB.close();
                return customerModelList;
            }

            @Override
            protected void onPostExecute(List<CustomerModel> customerModelList) {
                super.onPostExecute(customerModelList);
                customersRecyclerView.setAdapter(new CustomerAdapter(customerModelList, new CustomerAdapter.OnAdapterInteractionListener() {
                    @Override
                    public void onItemSelected(CustomerModel customerModel) {
                        if (customerModel != null) {
                            getSharedPreferences(getPackageName(), MODE_PRIVATE).edit()
                                    .putString(Constants.KEY_LAST_SELECTED_CUSTOMER, customerModel.getCode()).apply();
                        }
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra(Constants.KEY_CUSTOMER, customerModel);
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                }));
                if (linearLayoutManager != null) {
                    linearLayoutManager.scrollToPosition(findLastSelectedCustomerPosition(customerModelList));
                }
            }
        }.execute();
    }

    private int findLastSelectedCustomerPosition(List<CustomerModel> customerModelList) {
        for (int i = 0; i < customerModelList.size(); i++) {
            if (customerModelList.get(i).getCode().equalsIgnoreCase(
                    Util.getStringFromSharedPreferences(getBaseContext(), Constants.KEY_LAST_SELECTED_CUSTOMER)
            )) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    private void alertSync() {
        new AlertDialog.Builder(CustomersActivity.this)
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
                progressDialog = new ProgressDialog(CustomersActivity.this);
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
                    Util.showAlert(CustomersActivity.this, "Alert", "Successfully synced", false);
                } else {
                    Util.showAlert(CustomersActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
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
                Util.logoutAlert(CustomersActivity.this, "Alert", "Are you sure you want to logout.?");
                return true;
            case R.id.GM_clearAll:
                alertClearAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertClearAll() {
        new AlertDialog.Builder(CustomersActivity.this)
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
                progressDialog = new ProgressDialog(CustomersActivity.this);
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
                    Util.showAlert(CustomersActivity.this, "Alert", "Successfully deleted", false);
                } else {
                    Util.showAlert(CustomersActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
                }
            }
        }.execute();
    }

}
