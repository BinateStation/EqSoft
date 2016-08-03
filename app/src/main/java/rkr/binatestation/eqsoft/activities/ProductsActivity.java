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

import java.util.List;

import rkr.binatestation.eqsoft.R;
import rkr.binatestation.eqsoft.adapters.ProductAdapter;
import rkr.binatestation.eqsoft.models.ProductModel;

public class ProductsActivity extends AppCompatActivity {
    SearchView productSearch;
    RecyclerView productsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        productSearch = (SearchView) findViewById(R.id.AP_productSearch);
        productsRecyclerView = (RecyclerView) findViewById(R.id.Ap_productsRecyclerView);

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
                productsRecyclerView.setAdapter(new ProductAdapter(productModels));
            }
        }.execute();
    }

    public void checkOut(View view) {
        startActivity(new Intent(getBaseContext(), OrderActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.global_menu, menu);
        return true;
    }

}
