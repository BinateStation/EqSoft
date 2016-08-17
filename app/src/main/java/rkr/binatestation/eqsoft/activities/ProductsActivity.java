package rkr.binatestation.eqsoft.activities;

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
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.adapters.ProductAdapter;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.network.DataSync;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class ProductsActivity extends AppCompatActivity {
    SearchView productSearch;
    RecyclerView productsRecyclerView;
    TextView customerName, totalAmount;
    AppCompatSpinner sort;
    ProgressDialog progressDialog;

    CustomerModel customerModel;
    ProductAdapter productAdapter;
    Map<String, OrderItemModel> orderItemModelMap = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getIntent().hasExtra(Constants.KEY_CUSTOMER)) {
            customerModel = (CustomerModel) getIntent().getSerializableExtra(Constants.KEY_CUSTOMER);
        }
        productSearch = (SearchView) findViewById(R.id.AP_productSearch);
        productsRecyclerView = (RecyclerView) findViewById(R.id.Ap_productsRecyclerView);
        customerName = (TextView) findViewById(R.id.AP_customerLedgerName);
        totalAmount = (TextView) findViewById(R.id.AP_totalAmount);
        sort = (AppCompatSpinner) findViewById(R.id.AP_sort);

        productSearch.setQueryHint(getString(R.string.type_to_search));
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(productsRecyclerView.getContext()));
        productSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getProducts(query, sort.getSelectedItemPosition());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getProducts(newText, sort.getSelectedItemPosition());
                return true;
            }
        });
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getProducts("", sort.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCustomerName();
    }

    public void setCustomerName() {
        if (customerModel != null) {
            customerName.setText(customerModel.getLedgerName());
            setOrderItemModelMap();
        } else {
            totalAmount.setText("");
        }
    }

    private void setOrderItemModelMap() {
        new AsyncTask<Void, Void, OrderModel>() {
            @Override
            protected OrderModel doInBackground(Void... voids) {
                OrderModel orderModelDB = new OrderModel(getBaseContext());
                orderModelDB.open();
                OrderModel orderModel = orderModelDB.getCustomersRow(customerModel.getCode());
                orderModelDB.close();

                if (orderModel != null) {
                    OrderItemModel orderItemModelDB = new OrderItemModel(getBaseContext());
                    orderItemModelDB.open();
                    orderItemModelMap = orderItemModelDB.getAllRows(orderModel.getOrderId());
                    orderItemModelDB.close();
                } else {
                    orderItemModelMap.clear();
                }
                return orderModel;
            }

            @Override
            protected void onPostExecute(OrderModel orderModel) {
                super.onPostExecute(orderModel);
                if (orderModel != null) {
                    totalAmount.setText(orderModel.getAmount());
                } else {
                    totalAmount.setText("");
                }
                if (productAdapter != null) {
                    productAdapter.setOrderItemModelMap(orderItemModelMap);
                    productAdapter.notifyDataSetChanged();
                }
            }
        }.execute();
    }

    private void getProducts(final String query, final int sortType) {
        new AsyncTask<Void, Void, List<ProductModel>>() {
            @Override
            protected List<ProductModel> doInBackground(Void... voids) {
                ProductModel productModelDB = new ProductModel(getBaseContext());
                productModelDB.open();
                List<ProductModel> productModelList = productModelDB.getAllRows(query, sortType);
                productModelDB.close();
                return productModelList;
            }

            @Override
            protected void onPostExecute(List<ProductModel> productModels) {
                super.onPostExecute(productModels);
                productsRecyclerView.setAdapter(productAdapter = new ProductAdapter(productModels, customerModel, orderItemModelMap, true, new ProductAdapter.OnAdapterInteractionListener() {
                    @Override
                    public void onItemClicked() {
                        startActivityForResult(new Intent(getBaseContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER);
                    }

                    @Override
                    public void onProductSelected() {
                        setOrderItemModelMap();
                    }
                }));
            }
        }.execute();
    }

    public void checkOut(View view) {
        startActivity(
                new Intent(getBaseContext(), OrderActivity.class)
                        .putExtra(Constants.KEY_CUSTOMER, customerModel)
        );
    }

    public void selectCustomer(View view) {
        startActivityForResult(new Intent(view.getContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    private void alertSync() {
        new AlertDialog.Builder(ProductsActivity.this)
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
                progressDialog = new ProgressDialog(ProductsActivity.this);
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
                    Util.showAlert(ProductsActivity.this, "Alert", "Successfully synced", false);
                } else {
                    Util.showAlert(ProductsActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
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
                Util.logoutAlert(ProductsActivity.this, "Alert", "Are you sure you want to logout.?");
                return true;
            case R.id.GM_clearAll:
                alertClearAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertClearAll() {
        new AlertDialog.Builder(ProductsActivity.this)
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
                progressDialog = new ProgressDialog(ProductsActivity.this);
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
                    Util.showAlert(ProductsActivity.this, "Alert", "Successfully deleted", false);
                } else {
                    Util.showAlert(ProductsActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
                }
            }
        }.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_CUSTOMER && data.hasExtra(Constants.KEY_CUSTOMER)) {
            customerModel = (CustomerModel) data.getSerializableExtra(Constants.KEY_CUSTOMER);
            if (productAdapter != null && customerModel != null) {
                productAdapter.setCustomerModel(customerModel);
            }
            setCustomerName();
        }
    }
}
