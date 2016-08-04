package rkr.binatestation.eqsoft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.fragments.CustomerSearchFragment;
import rkr.binatestation.eqsoft.models.CustomerModel;

public class ReceiptsActivity extends AppCompatActivity {
    CustomerSearchFragment customerSearchFragment;
    CustomerModel customerModel;

    TextView customerLedgerName, mobile, balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        customerLedgerName = (TextView) findViewById(R.id.AR_customerLedgerName);
        mobile = (TextView) findViewById(R.id.AR_customerMobile);
        balance = (TextView) findViewById(R.id.AR_balance);
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

}
