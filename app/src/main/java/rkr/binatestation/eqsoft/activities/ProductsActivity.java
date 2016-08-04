package rkr.binatestation.eqsoft.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.adapters.ProductAdapter;
import rkr.binatestation.eqsoft.fragments.CustomerSearchFragment;
import rkr.binatestation.eqsoft.models.CustomerModel;
import rkr.binatestation.eqsoft.models.ProductModel;
import rkr.binatestation.eqsoft.utils.Constants;

public class ProductsActivity extends AppCompatActivity {
    SearchView productSearch;
    RecyclerView productsRecyclerView;
    TextView customerName, totalAmount;
    CustomerModel customerModel;
    ProductAdapter productAdapter;
    CustomerSearchFragment customerSearchFragment;

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

        productSearch.setQueryHint(getString(R.string.type_to_search));
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(productsRecyclerView.getContext()));
        productSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getProducts(newText);
                return true;
            }
        });
        getProducts("");
        setCustomerName();
    }

    public void setCustomerName() {
        if (customerModel != null) {
            customerName.setText(customerModel.getLedgerName());
        }
    }

    private void getProducts(final String query) {
        new AsyncTask<Void, Void, List<ProductModel>>() {
            @Override
            protected List<ProductModel> doInBackground(Void... voids) {
                ProductModel productModelDB = new ProductModel(getBaseContext());
                productModelDB.open();
                List<ProductModel> productModelList = productModelDB.getAllRows(query);
                productModelDB.close();
                return productModelList;
            }

            @Override
            protected void onPostExecute(List<ProductModel> productModels) {
                super.onPostExecute(productModels);
                productsRecyclerView.setAdapter(productAdapter = new ProductAdapter(productModels, customerModel, new ProductAdapter.OnAdapterInteractionListener() {
                    @Override
                    public void onItemClicked() {
                        customerSearchFragment = CustomerSearchFragment.newInstance(new CustomerSearchFragment.OnCustomerSearchFragmentInteractionListener() {
                            @Override
                            public void onItemSelected(CustomerModel customerModel) {
                                ProductsActivity.this.customerModel = customerModel;
                                if (productAdapter != null) {
                                    productAdapter.setCustomerModel(customerModel);
                                }
                                setCustomerName();
                                if (customerSearchFragment != null) {
                                    customerSearchFragment.dismiss();
                                }
                            }
                        });
                        customerSearchFragment.show(getSupportFragmentManager(), CustomerSearchFragment.tag);

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
        customerSearchFragment = CustomerSearchFragment.newInstance(new CustomerSearchFragment.OnCustomerSearchFragmentInteractionListener() {
            @Override
            public void onItemSelected(CustomerModel customerModel) {
                ProductsActivity.this.customerModel = customerModel;
                if (productAdapter != null) {
                    productAdapter.setCustomerModel(customerModel);
                }
                setCustomerName();
                if (customerSearchFragment != null) {
                    customerSearchFragment.dismiss();
                }
            }
        });
        customerSearchFragment.show(getSupportFragmentManager(), CustomerSearchFragment.tag);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

}
