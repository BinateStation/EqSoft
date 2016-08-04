package rkr.binatestation.eqsoft.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.utils.Constants;

public class OrderActivity extends AppCompatActivity {
    RecyclerView selectedProductsRecyclerView;
    TextView customerLedgerName, previousBalance;
    CustomerModel customerModel;

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

        selectedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(selectedProductsRecyclerView.getContext()));
//        selectedProductsRecyclerView.setAdapter(new ProductAdapter(2));
        setCustomerDetails();
    }

    public void setCustomerDetails() {
        if (customerModel != null) {
            customerLedgerName.setText(customerModel.getLedgerName());
            previousBalance.setText(customerModel.getBalance());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }
}
