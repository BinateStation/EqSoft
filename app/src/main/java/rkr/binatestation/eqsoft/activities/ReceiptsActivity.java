package rkr.binatestation.eqsoft.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.network.DataSync;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class ReceiptsActivity extends AppCompatActivity {
    CustomerModel customerModel;
    ProgressDialog progressDialog;

    TextView customerLedgerName, mobile, balance;
    TextInputEditText receivedAmount;

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
        receivedAmount.requestFocus();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.GM_usbSync:
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
                return true;
            case R.id.GM_logout:
                Util.logoutAlert(ReceiptsActivity.this, "Alert", "Are you sure you want to logout.?");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showSearchDialog(View view) {
        startActivityForResult(new Intent(getBaseContext(), CustomersActivity.class), Constants.REQUEST_CODE_CUSTOMER);
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
        }.execute();
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
                    receivedAmount.setText(receiptModel.getAmount());
                    receivedAmount.setSelection(receivedAmount.getText().length());
                } else {
                    receivedAmount.setText("");
                }
            }
        }.execute();
    }

    public void done(View view) {
        if (customerModel != null) {
            saveReceipts(view.getContext(), receivedAmount.getText().toString().trim());
        } else {
            Util.showAlert(view.getContext(), "Alert", "Please select the customer.!");
        }
    }

    private void saveReceipts(final Context context, final String receivedAmount) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (receivedAmount.length() > 0) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_CODE_CUSTOMER && data.hasExtra(Constants.KEY_CUSTOMER)) {
            customerModel = (CustomerModel) data.getSerializableExtra(Constants.KEY_CUSTOMER);
            setCustomerDetails();
        }
    }
}
