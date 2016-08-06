package rkr.binatestation.eqsoft.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.fragments.CustomerSearchFragment;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.ReceiptModel;
import rkr.binatestation.eqsoft.utils.Constants;
import rkr.binatestation.eqsoft.utils.Util;

public class ReceiptsActivity extends AppCompatActivity {
    CustomerSearchFragment customerSearchFragment;
    CustomerModel customerModel;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

    public void showSearchDialog(View view) {
        customerSearchFragment = CustomerSearchFragment.newInstance(new CustomerSearchFragment.OnCustomerSearchFragmentInteractionListener() {
            @Override
            public void onItemSelected(CustomerModel customerModel) {
                ReceiptsActivity.this.customerModel = customerModel;
                setCustomerDetails();
                if (customerSearchFragment != null) {
                    customerSearchFragment.dismiss();
                }
            }
        });
        customerSearchFragment.show(getSupportFragmentManager(), CustomerSearchFragment.tag);
    }

    public void setCustomerDetails() {
        if (customerModel != null) {
            customerLedgerName.setText(customerModel.getLedgerName());
            mobile.setText(customerModel.getMobile());
            balance.setText(customerModel.getBalance());
        }
    }

    public void done(View view) {
        saveReceipts(view.getContext(), receivedAmount.getText().toString().trim());
    }

    private void saveReceipts(final Context context, final String receivedAmount) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if (receivedAmount.length() > 0) {
                    CustomerModel customerModelDB = new CustomerModel(context);
                    customerModelDB.open();
                    try {
                        if (customerModel != null) {
                            customerModel.setBalance("" + (
                                    Double.parseDouble(customerModel.getBalance()) -
                                            Double.parseDouble(receivedAmount))
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
}
