package rkr.binatestation.eqsoft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.OrderItemModel;
import rkr.binatestation.eqsoft.models.OrderModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.network.DataSync;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class ReceiptsActivity extends AppCompatActivity {
    CustomerModel customerModel;
    ProgressDialog progressDialog;

    TextView customerLedgerName, mobile, balance;
    TextInputEditText receivedAmount;
    ImageButton clearText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customerLedgerName = (TextView) findViewById(R.id.AR_customerLedgerName);
        mobile = (TextView) findViewById(R.id.AR_customerMobile);
        balance = (TextView) findViewById(R.id.AR_balance);
        receivedAmount = (TextInputEditText) findViewById(R.id.AR_receivedAmount);
        clearText = (ImageButton) findViewById(R.id.AR_clearText);
        receivedAmount.requestFocus();

        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receivedAmount.setText("");
            }
        });
        receivedAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    done(textView);
                    return true;
                }
                return false;
            }
        });

        if (getIntent().hasExtra(Constants.KEY_CUSTOMER)) {
            customerModel = (CustomerModel) getIntent().getSerializableExtra(Constants.KEY_CUSTOMER);
            setCustomerDetails();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    private void alertSync() {
        new AlertDialog.Builder(ReceiptsActivity.this, R.style.AppTheme_Light_Dialog)
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
                progressDialog = new ProgressDialog(ReceiptsActivity.this);
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
                    Util.showAlert(ReceiptsActivity.this, "Alert", "Successfully synced", false);
                } else {
                    Util.showAlert(ReceiptsActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
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
                Util.logoutAlert(ReceiptsActivity.this, "Alert", "Are you sure you want to logout.?");
                return true;
            case R.id.GM_clearAll:
                alertClearAll();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void alertClearAll() {
        new AlertDialog.Builder(ReceiptsActivity.this, R.style.AppTheme_Light_Dialog)
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
                progressDialog = new ProgressDialog(ReceiptsActivity.this);
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
                    Util.showAlert(ReceiptsActivity.this, "Alert", "Successfully deleted", false);
                } else {
                    Util.showAlert(ReceiptsActivity.this, "Alert", "Some thing went wrong please contact administrator", false);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void showSearchDialog(View view) {
        startActivityForResult(new Intent(getBaseContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER_RECEIPT);
    }

    public void setCustomerDetails() {
        if (customerModel != null) {
            customerLedgerName.setText(customerModel.getLedgerName());
            mobile.setText(customerModel.getMobile());
            setCustomerBalance(balance.getContext(), customerModel.getCode(), balance);
            setReceivedAmount();
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setReceivedAmount() {
        new AsyncTask<Void, Void, ReceiptModel>() {
            @Override
            protected ReceiptModel doInBackground(Void... voids) {
                ReceiptModel receiptModelDB = new ReceiptModel(getBaseContext());
                receiptModelDB.open();
                ReceiptModel receiptModel = receiptModelDB.getRow(customerModel.getCode());
                receiptModelDB.close();
                return receiptModel;
            }

            @Override
            protected void onPostExecute(ReceiptModel receiptModel) {
                super.onPostExecute(receiptModel);
                if (receiptModel != null) {
                    receivedAmount.setText(String.format(Locale.getDefault(), "%.2f", receiptModel.getAmount()));
                    receivedAmount.setSelection(receivedAmount.getText().length());
                    receivedAmount.requestFocus();
                } else {
                    receivedAmount.setText("0.0");
                    receivedAmount.requestFocus();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void done(View view) {
        if (customerModel != null) {
            saveReceipts(view.getContext(), receivedAmount.getText().toString().trim());
        } else {
            Util.showAlert(view.getContext(), "Alert", "Please select the customer.!");
        }
    }

    private void saveReceipts(final Context context, final String receivedAmount) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                if (receivedAmount.length() > 0) {
                    Double receivedAmountDouble = Double.parseDouble(receivedAmount);
                    if (receivedAmountDouble <= 0.0) {
                        return "Please enter a valid amount.";
                    }
                    try {
                        ReceiptModel receiptModelDB = new ReceiptModel(context);
                        receiptModelDB.open();
                        receiptModelDB.insert(new ReceiptModel(
                                "0",
                                Util.getCurrentDate("yyyy-MM-dd HH:mm:ss"),
                                customerModel.getCode(),
                                receivedAmountDouble,
                                Util.getStringFromSharedPreferences(context, Constants.KEY_USER_ID)
                        ));
                        receiptModelDB.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        return "Please enter a valid amount.";
                    }
                    return "1";
                } else {
                    return "Please enter a valid amount.";
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if ("1".equalsIgnoreCase(result)) {
                    startActivity(new Intent(getBaseContext(), HomeActivity.class
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    Util.showAlert(context, "Alert", result);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_CUSTOMER_RECEIPT && data.hasExtra(Constants.KEY_CUSTOMER)) {
            customerModel = (CustomerModel) data.getSerializableExtra(Constants.KEY_CUSTOMER);
            setCustomerDetails();
        }
    }
}
