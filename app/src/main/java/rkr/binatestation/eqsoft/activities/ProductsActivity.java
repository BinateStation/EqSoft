package rkr.binatestation.eqsoft.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.adapters.ProductAdapter;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderItemModelTemp;
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
    Map<String, OrderItemModelTemp> orderItemModelMap = new LinkedHashMap<>();
    ReceiptModel receiptModel;

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
        if (sort != null) {
            getProducts("", sort.getSelectedItemPosition());
        }
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
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OrderModel orderModelDB = new OrderModel(getBaseContext());
                orderModelDB.open();
                OrderModel orderModel = orderModelDB.getCustomersRow(customerModel.getCode());
                if (orderModel != null) {
                    orderModelDB.deleteRow(orderModel.getOrderId());
                }
                orderModelDB.close();

                List<OrderItemModelTemp> orderItemModelTemps = new ArrayList<>();
                if (orderModel != null) {
                    OrderItemModel orderItemModelDB = new OrderItemModel(getBaseContext());
                    orderItemModelDB.open();
                    orderItemModelTemps = orderItemModelDB.getAllRows(orderModel.getOrderId());
                    orderItemModelDB.deleteAll(orderModel.getOrderId());
                    orderItemModelDB.close();
                }

                if (receiptModel == null) {
                    ReceiptModel receiptModelDB = new ReceiptModel(getBaseContext());
                    receiptModelDB.open();
                    receiptModel = receiptModelDB.getRow(customerModel.getCode());
                    receiptModelDB.close();
                }

                OrderItemModelTemp orderItemModelTempDB = new OrderItemModelTemp(getBaseContext());
                orderItemModelTempDB.open();
                orderItemModelTempDB.insertMultipleRows(orderItemModelTemps);
                orderItemModelMap = orderItemModelTempDB.getAllRowsAsMap();
                orderItemModelTempDB.close();

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Double total = 0.0;
                for (OrderItemModelTemp orderItemModelTemp : orderItemModelMap.values()) {
                    total += orderItemModelTemp.getAmount();
                }
                totalAmount.setText(String.format(Locale.getDefault(), "%.2f", total));
                if (productAdapter != null) {
                    productAdapter.setOrderItemModelMap(orderItemModelMap);
                    productAdapter.notifyDataSetChanged();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                        startActivityForResult(new Intent(getBaseContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER_ORDER_SUMMARY);
                    }

                    @Override
                    public void onProductSelected() {
                        setOrderItemModelMap();
                    }
                }));
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void checkOut(View view) {
        startActivity(new Intent(getBaseContext(), CheckoutActivity.class)
                .putExtra(Constants.KEY_CUSTOMER, customerModel)
                .putExtra(Constants.KEY_RECEIPT, receiptModel)
        );
    }

    public void selectCustomer(View view) {
        startActivityForResult(new Intent(view.getContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER_ORDER_SUMMARY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    private void alertSync() {
        new AlertDialog.Builder(ProductsActivity.this, R.style.AppTheme_Light_Dialog)
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(ProductsActivity.this, R.style.AppTheme_Light_Dialog)
                .setTitle("Alert")
                .setMessage("By going back you will loose all the orders. Do you need to proceed ?")
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeAllOrdersAndGoBack();
                    }
                }).show();

    }

    private void removeAllOrdersAndGoBack() {
        try {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    if (orderItemModelMap.size() > 0 && customerModel != null) {
                        List<OrderItemModel> orderItemModels = new ArrayList<>();
                        for (OrderItemModelTemp temp : orderItemModelMap.values()) {
                            if (!temp.getNew()) {
                                orderItemModels.add(new OrderItemModel(
                                        "",
                                        temp.getProductCode(),
                                        temp.getRate(),
                                        temp.getQuantity(),
                                        temp.getAmount(),
                                        (customerModel != null) ? customerModel.getCode() : ""
                                ));
                            }
                        }

                        if (orderItemModels.size() > 0) {
                            OrderModel orderModelDB = new OrderModel(getBaseContext());
                            orderModelDB.open();
                            OrderModel orderModel = orderModelDB.getCustomersRow(customerModel.getCode());
                            Long orderId;
                            Double receivedAmountDouble = 0.0;
                            Double totalAmountDouble = 0.0;
                            for (OrderItemModelTemp temp : orderItemModelMap.values()) {
                                if (temp != null) {
                                    totalAmountDouble += (temp.getQuantity() * temp.getRate());
                                }
                            }
                            try {
                                ReceiptModel receiptModelDB = new ReceiptModel(getBaseContext());
                                receiptModelDB.open();
                                ReceiptModel receiptModel = receiptModelDB.getRow(customerModel.getCode());
                                receiptModelDB.close();
                                if (receiptModel != null) {
                                    receivedAmountDouble = receiptModel.getAmount();
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }


                            if (orderModel == null) {
                                orderId = orderModelDB.insert(new OrderModel(
                                        "0",
                                        Util.getCurrentDate("yyyy-MM-dd HH:mm:ss"),
                                        customerModel.getCode(),
                                        totalAmountDouble,
                                        receivedAmountDouble,
                                        "",
                                        "",
                                        Util.getStringFromSharedPreferences(getBaseContext(), Constants.KEY_USER_ID)
                                ));
                            } else {
                                orderId = Long.parseLong(orderModel.getOrderId());
                                orderModelDB.updateRow(new OrderModel(
                                        orderModel.getOrderId(),
                                        orderModel.getDocDate(),
                                        orderModel.getCustomerCode(),
                                        totalAmountDouble,
                                        receivedAmountDouble,
                                        orderModel.getDueDate(),
                                        orderModel.getRemarks(),
                                        orderModel.getUserId()
                                ));
                            }
                            orderModelDB.close();

                            for (OrderItemModel orderItemModel :
                                    orderItemModels) {
                                orderItemModel.setOrderId("" + orderId);
                            }

                            OrderItemModel orderItemModelDB = new OrderItemModel(getBaseContext());
                            orderItemModelDB.open();
                            orderItemModelDB.insertMultipleRows(orderItemModels);
                            orderItemModelDB.close();
                            try {
                                if (receivedAmountDouble >= 0) {
                                    ReceiptModel receiptModelDB = new ReceiptModel(getBaseContext());
                                    receiptModelDB.open();
                                    receiptModelDB.insert(new ReceiptModel(
                                            "0",
                                            Util.getCurrentDate("yyyy-MM-dd HH:mm:ss"),
                                            customerModel.getCode(),
                                            receivedAmountDouble,
                                            Util.getStringFromSharedPreferences(getBaseContext(), Constants.KEY_USER_ID)
                                    ));
                                    receiptModelDB.close();
                                }
                            } catch (NumberFormatException | SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    OrderItemModelTemp orderItemModelTempDB = new OrderItemModelTemp(ProductsActivity.this);
                    orderItemModelTempDB.open();
                    orderItemModelTempDB.deleteAll();
                    orderItemModelTempDB.close();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    startActivity(new Intent(getBaseContext(), HomeActivity.class
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void alertClearAll() {
        new AlertDialog.Builder(ProductsActivity.this, R.style.AppTheme_Light_Dialog)
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_CUSTOMER_ORDER_SUMMARY && data.hasExtra(Constants.KEY_CUSTOMER)) {
            customerModel = (CustomerModel) data.getSerializableExtra(Constants.KEY_CUSTOMER);
            if (productAdapter != null && customerModel != null) {
                productAdapter.setCustomerModel(customerModel);
            }
            setCustomerName();
        }
    }
}
