package rkr.binatestation.eqsoft.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
import rkr.binatestation.eqsoft.utils.Constants;

public class OrderActivity extends AppCompatActivity {
    RecyclerView selectedProductsRecyclerView;
    TextView customerLedgerName, previousBalance;
    AppCompatTextView totalAmount;
    CustomerModel customerModel;
    ProductAdapter productAdapter;
    Map<String, OrderItemModel> orderItemModelMap = new LinkedHashMap<>();

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

        selectedProductsRecyclerView.setLayoutManager(new LinearLayoutManager(selectedProductsRecyclerView.getContext()));
        setCustomerDetails();
    }

    public void setCustomerDetails() {
        if (customerModel != null) {
            customerLedgerName.setText(customerModel.getLedgerName());
            previousBalance.setText(customerModel.getBalance());
            setOrderItemModelMap();
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
                }
                return orderModel;
            }

            @Override
            protected void onPostExecute(OrderModel orderModel) {
                super.onPostExecute(orderModel);
                if (orderModel != null) {
                    totalAmount.setText(orderModel.getAmount());
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }
}
