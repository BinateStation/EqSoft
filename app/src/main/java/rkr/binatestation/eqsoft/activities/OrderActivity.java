package rkr.binatestation.eqsoft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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

public class OrderActivity extends AppCompatActivity {
    RecyclerView selectedProductsRecyclerView;
    TextView customerLedgerName, previousBalance;
    TextInputEditText receivedAmount;
    AppCompatTextView totalAmount;
    CustomerModel customerModel;
    ProductAdapter productAdapter;
    Map<String, OrderItemModel> orderItemModelMap = new LinkedHashMap<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customerModel = (CustomerModel) getIntent().getSerializableExtra(Constants.KEY_CUSTOMER);

        selectedProductsRecyclerView = (RecyclerView) findViewById(R.id.AO_selectedProducts);
        customerLedgerName = (TextView) findViewById(R.id.AO_customerLedgerName);
        previousBalance = (TextView) findViewById(R.id.AO_previousBalance);
        totalAmount = (AppCompatTextView) findViewById(R.id.AO_totalAmount);
        receivedAmount = (TextInputEditText) findViewById(R.id.AO_receivedAmount);
        receivedAmount.requestFocus();
        receivedAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    proceedOrder(textView);
                    return true;
                }
                return false;
            }
        });

        selectedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(selectedProductsRecyclerView.getContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCustomerDetails();
    }

    public void setCustomerDetails() {
        if (customerModel != null) {
            customerLedgerName.setText(customerModel.getLedgerName());
            setCustomerBalance(previousBalance.getContext(), customerModel.getCode(), previousBalance);
            setOrderItemModelMap();
        }
    }

    private void setCustomerBalance(final Context context, final String customerCode, final TextView balanceTextView) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                CustomerModel customerModelDB = new CustomerModel(context);
                customerModelDB.open();
                String balance = customerModelDB.getCustomerBalance(customerCode);
                customerModelDB.close();
                return balance;
            }

            @Override
            protected void onPostExecute(String balance) {
                super.onPostExecute(balance);
                balanceTextView.setText(balance);
            }
        }.execute();
    }

    private void setOrderItemModelMap() {
        new AsyncTask<Void, Void, String[]>() {
            @Override
            protected String[] doInBackground(Void... voids) {
                OrderModel orderModelDB = new OrderModel(getBaseContext());
                orderModelDB.open();
                OrderModel orderModel = orderModelDB.getCustomersRow(customerModel.getCode());
                orderModelDB.close();

                String[] result = new String[2];
                if (orderModel != null) {
                    result[0] = orderModel.getAmount();
                    OrderItemModel orderItemModelDB = new OrderItemModel(getBaseContext());
                    orderItemModelDB.open();
                    orderItemModelMap = orderItemModelDB.getAllRows(orderModel.getOrderId());
                    orderItemModelDB.close();
                } else {
                    result[0] = "";
                }

                ReceiptModel receiptModelDB = new ReceiptModel(getBaseContext());
                receiptModelDB.open();
                ReceiptModel receiptModel = receiptModelDB.getRow(customerModel.getCode());
                receiptModelDB.close();

                if (receiptModel != null) {
                    result[1] = receiptModel.getAmount();
                } else {
                    result[1] = "";
                }
                return result;
            }

            @Override
            protected void onPostExecute(String[] result) {
                super.onPostExecute(result);
                try {
                    if (result != null) {
                        totalAmount.setText(result[0]);
                    }
                    if (result != null) {
                        receivedAmount.setText(result[1]);
                        receivedAmount.setSelection(receivedAmount.getText().length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (orderItemModelMap.size() > 0) {
                    getProducts(TextUtils.join(",", orderItemModelMap.keySet()));
                }
            }
        }.execute();
    }

    private void getProducts(final String query) {
        new AsyncTask<Void, Void, List<ProductModel>>() {
            @Override
            protected List<ProductModel> doInBackground(Void... voids) {
                ProductModel productModelDB = new ProductModel(getBaseContext());
                productModelDB.open();
                List<ProductModel> productModelList = productModelDB.getAllSelectedProducts(query);
                productModelDB.close();
                return productModelList;
            }

            @Override
            protected void onPostExecute(List<ProductModel> productModels) {
                super.onPostExecute(productModels);
                selectedProductsRecyclerView.setAdapter(productAdapter = new ProductAdapter(productModels, customerModel, orderItemModelMap, false, new ProductAdapter.OnAdapterInteractionListener() {
                    @Override
                    public void onItemClicked() {
                    }

                    @Override
                    public void onProductSelected() {

                    }
                }));
            }
        }.execute();
    }

    public void addMore(View view) {
        startActivity(new Intent(getBaseContext(), ProductsActivity.class)
                .putExtra(Constants.KEY_CUSTOMER, customerModel));
    }

    public void proceedOrder(View view) {
        if (customerModel != null) {
            saveOrder(view.getContext(), receivedAmount.getText().toString().trim());
        }
    }

    private void saveOrder(final Context context, final String receivedAmount) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                OrderModel orderModelDB = new OrderModel(context);
                orderModelDB.open();
                OrderModel orderModel = orderModelDB.getCustomersRow(customerModel.getCode());
                orderModelDB.close();

                if (orderModel != null && receivedAmount.length() > 0) {
                    orderModelDB.open();
                    orderModelDB.updateRow(new OrderModel(
                            orderModel.getOrderId(),
                            orderModel.getDocDate(),
                            orderModel.getCustomerCode(),
                            orderModel.getAmount(),
                            "" + (Double.parseDouble(orderModel.getReceivedAmount()) + Double.parseDouble(receivedAmount)),
                            orderModel.getDueDate(),
                            orderModel.getRemarks(),
                            orderModel.getUserId()
                    ));
                    orderModelDB.close();
                    CustomerModel customerModelDB = new CustomerModel(context);
                    customerModelDB.open();
                    try {
                        if (customerModel != null) {
                            customerModel.setBalance("" + (
                                    Double.parseDouble(customerModel.getBalance()) -
                                            (Double.parseDouble(orderModel.getReceivedAmount()) + Double.parseDouble(receivedAmount)))
                            );
                            customerModelDB.updateRow(customerModel);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    customerModelDB.close();

                    ReceiptModel receiptModelDB = new ReceiptModel(context);
                    receiptModelDB.open();
                    receiptModelDB.insert(new ReceiptModel(
                            "0",
                            Util.getCurrentDate("yyyy-MM-dd HH:mm:ss"),
                            customerModel.getCode(),
                            receivedAmount,
                            Util.getStringFromSharedPreferences(context, Constants.KEY_USER_ID)
                    ));
                    receiptModelDB.close();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                startActivity(new Intent(
                        getBaseContext(),
                        HomeActivity.class
                ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    private void alertSync() {
        new AlertDialog.Builder(OrderActivity.this)
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
                progressDialog = new ProgressDialog(OrderActivity.this);
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
                    Util.showAlert(OrderActivity.this, "Alert", "Successfully synced", false);
                } else {
                    Util.showAlert(OrderActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
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
                Util.logoutAlert(OrderActivity.this, "Alert", "Are you sure you want to logout.?");
                return true;
            case R.id.GM_clearAll:
                alertClearAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertClearAll() {
        new AlertDialog.Builder(OrderActivity.this)
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
                progressDialog = new ProgressDialog(OrderActivity.this);
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
                    Util.showAlert(OrderActivity.this, "Alert", "Successfully deleted", false);
                } else {
                    Util.showAlert(OrderActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
                }
            }
        }.execute();
    }

}
